package cn.autoforged.monster_checklist_mod_1784790200.command;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import cn.autoforged.monster_checklist_mod_1784790200.ModEventHandlers;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class ModCommands {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("monsterchecklist")
                .then(Commands.literal("add")
                        .then(Commands.literal("gift")
                                .requires(s -> s.hasPermission(2))
                                .executes(ctx -> {
                                    var player = ctx.getSource().getPlayerOrException();
                                    ModEventHandlers.grantAttributeBoosts(player);
                                    ctx.getSource().sendSuccess(
                                            () -> Component.translatable("command." + MainMod.MODID + ".gift_success"), true);
                                    return 1;
                                }))));
    }
}
