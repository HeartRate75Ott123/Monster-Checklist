package cn.autoforged.monster_checklist_mod_1784790200;

import cn.autoforged.monster_checklist_mod_1784790200.attribute.ModAttributes;
import cn.autoforged.monster_checklist_mod_1784790200.command.ModCommands;
import cn.autoforged.monster_checklist_mod_1784790200.config.ModConfig;
import cn.autoforged.monster_checklist_mod_1784790200.creative.ModCreativeTabs;
import cn.autoforged.monster_checklist_mod_1784790200.item.ModItems;
import cn.autoforged.monster_checklist_mod_1784790200.tracking.ModAttachmentTypes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@Mod(MainMod.MODID)
public class MainMod {
    public static final String MODID = "monster_checklist_mod_1784790200";

    public MainMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);

        modContainer.registerConfig(Type.CLIENT, ModConfig.SPEC);
        modEventBus.addListener(this::onAttributeModification);
        modEventBus.addListener(ModCreativeTabs::onBuildCreativeTabs);
        NeoForge.EVENT_BUS.register(new ModEventHandlers());
        NeoForge.EVENT_BUS.register(new ModCommands());

        if (FMLLoader.getDist().isClient()) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory) ConfigurationScreen::new);
        }
    }

    private void onAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.DODGE_CHANCE);
        event.add(EntityType.PLAYER, ModAttributes.CRIT_CHANCE);
    }
}
