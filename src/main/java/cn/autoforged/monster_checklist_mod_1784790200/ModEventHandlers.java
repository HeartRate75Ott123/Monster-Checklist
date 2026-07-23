package cn.autoforged.monster_checklist_mod_1784790200;

import cn.autoforged.monster_checklist_mod_1784790200.config.ModConfig;
import cn.autoforged.monster_checklist_mod_1784790200.tracking.ModAttachmentTypes;
import com.evandev.fieldguide.server.progress.FieldGuideProgressManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

public class ModEventHandlers {
    private static final ResourceLocation DODGE_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(MainMod.MODID, "dodge_chance");
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(MainMod.MODID, "crit_chance");

    private record BoostSlot(ResourceLocation modId, ResourceLocation attrId, String key, double boostPerLevel, int maxLevels, String langKey) {}

    private static final List<BoostSlot> BOOST_POOL = List.of(
            new BoostSlot(rl("mb_max_health"), def("generic.max_health"), "max_health", 2.0, 10, "max_health"),
            new BoostSlot(rl("mb_movement_speed"), def("generic.movement_speed"), "movement_speed", 0.02, 5, "movement_speed"),
            new BoostSlot(rl("mb_luck"), def("generic.luck"), "luck", 1.0, 5, "luck"),
            new BoostSlot(rl("mb_knockback_resistance"), def("generic.knockback_resistance"), "knockback_resistance", 0.1, 5, "knockback_resistance"),
            new BoostSlot(rl("mb_armor"), def("generic.armor"), "armor", 1.0, 10, "armor"),
            new BoostSlot(rl("mb_armor_toughness"), def("generic.armor_toughness"), "armor_toughness", 1.0, 10, "armor_toughness"),
            new BoostSlot(rl("mb_entity_interaction_range"), def("player.entity_interaction_range"), "entity_interaction_range", 0.5, 5, "entity_interaction_range"),
            new BoostSlot(rl("mb_block_interaction_range"), def("player.block_interaction_range"), "block_interaction_range", 0.5, 5, "block_interaction_range"),
            new BoostSlot(rl("mb_mining_efficiency"), def("player.mining_efficiency"), "mining_efficiency", 2.0, 5, "mining_efficiency"),
            new BoostSlot(rl("mb_dodge_chance"), rl("dodge_chance"), "dodge_chance", 0.02, 15, "dodge_chance"),
            new BoostSlot(rl("mb_crit_chance"), rl("crit_chance"), "crit_chance", 0.02, 15, "crit_chance"),
            new BoostSlot(rl("mb_max_energy"), rl("max_energy"), "max_energy", 1.0, 10, "max_energy")
    );

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        checkAndGrantMilestones(player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        checkAndGrantMilestones(player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        checkAndGrantMilestones(player);
    }

    public static void checkAndGrantMilestones(ServerPlayer player) {
        var progress = FieldGuideProgressManager.getInstance().getProgress(player);
        if (progress == null) return;

        int unlockedCount = progress.getUnlockedEntries().size();
        var type = ModAttachmentTypes.CLAIMED_MILESTONES.get();
        int claimed = player.getData(type);

        int[] thresholds = ModConfig.getMilestoneArray();
        boolean changed = false;
        for (int i = 0; i < thresholds.length; i++) {
            if ((claimed & (1 << i)) == 0 && unlockedCount >= thresholds[i]) {
                claimed |= (1 << i);
                grantAttributeBoosts(player);
                player.sendSystemMessage(Component.translatable(
                        "message." + MainMod.MODID + ".milestone_reached",
                        thresholds[i]));
                changed = true;
            }
        }

        if (changed) {
            player.setData(type, claimed);
        }
    }

    public static void grantAttributeBoosts(ServerPlayer player) {
        CompoundTag boosts = player.getData(ModAttachmentTypes.ATTRIBUTE_BOOSTS.get());
        List<BoostSlot> pool = new ArrayList<>(BOOST_POOL);
        pool.removeIf(s -> boosts.getInt(s.key()) >= s.maxLevels());

        if (pool.isEmpty()) return;

        int count = Math.min(2, pool.size());
        for (int i = 0; i < count; i++) {
            int idx = player.getRandom().nextInt(pool.size());
            BoostSlot slot = pool.remove(idx);

            int level = boosts.getInt(slot.key()) + 1;
            boosts.putInt(slot.key(), level);

            if ("max_energy".equals(slot.key())) {
                ParagliderCompat.grantVessel(player);
            } else {
                applyModifier(player, slot, level);
            }

            player.sendSystemMessage(Component.translatable(
                    "message." + MainMod.MODID + ".attribute_boosted",
                    Component.translatable("attribute." + MainMod.MODID + "." + slot.langKey()),
                    formatBoost(slot.boostPerLevel())));
        }
        player.setData(ModAttachmentTypes.ATTRIBUTE_BOOSTS.get(), boosts);
    }

    private static void applyModifier(ServerPlayer player, BoostSlot slot, int level) {
        var holder = BuiltInRegistries.ATTRIBUTE.getHolder(
                ResourceKey.create(Registries.ATTRIBUTE, slot.attrId())).orElse(null);
        if (holder == null) return;
        AttributeInstance instance = player.getAttribute(holder);
        if (instance == null) return;

        instance.removeModifier(slot.modId());
        instance.addPermanentModifier(new AttributeModifier(
                slot.modId(),
                level * slot.boostPerLevel(),
                AttributeModifier.Operation.ADD_VALUE));
    }

    private static String formatBoost(double value) {
        if (value == (long) value) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    @SubscribeEvent
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        double dodge = getAttrValue(player, DODGE_CHANCE_ID);
        if (dodge > 0 && player.getRandom().nextDouble() < dodge) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        double crit = getAttrValue(player, CRIT_CHANCE_ID);
        if (crit > 0 && player.getRandom().nextDouble() < crit) {
            event.setCriticalHit(true);
            event.setDamageMultiplier(1.5f);
        }
    }

    private static double getAttrValue(Player player, ResourceLocation id) {
        var holder = BuiltInRegistries.ATTRIBUTE.getHolder(
                ResourceKey.create(Registries.ATTRIBUTE, id)).orElse(null);
        if (holder == null) return 0;
        var instance = player.getAttribute(holder);
        return instance != null ? instance.getValue() : 0;
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MainMod.MODID, path);
    }

    private static class ParagliderCompat {
        static void grantVessel(ServerPlayer player) {
            if (!net.neoforged.fml.ModList.get().isLoaded("paraglider")) return;
            tictim.paraglider.api.vessel.VesselContainer.get(player).giveStaminaVessels(1, false, true);
        }
    }

    private static ResourceLocation def(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
