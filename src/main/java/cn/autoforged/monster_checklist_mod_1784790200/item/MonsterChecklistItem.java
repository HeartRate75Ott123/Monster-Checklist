package cn.autoforged.monster_checklist_mod_1784790200.item;

import cn.autoforged.monster_checklist_mod_1784790200.ModEventHandlers;
import cn.autoforged.monster_checklist_mod_1784790200.screen.MonsterChecklistScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MonsterChecklistItem extends Item {

    public MonsterChecklistItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new MonsterChecklistScreen());
        } else if (player instanceof ServerPlayer sp) {
            ModEventHandlers.checkAndGrantMilestones(sp);
        }

        return InteractionResultHolder.success(stack);
    }
}
