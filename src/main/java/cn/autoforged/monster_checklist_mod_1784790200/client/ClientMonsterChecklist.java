package cn.autoforged.monster_checklist_mod_1784790200.client;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import cn.autoforged.monster_checklist_mod_1784790200.item.ModItems;
import cn.autoforged.monster_checklist_mod_1784790200.screen.MonsterChecklistScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = MainMod.MODID, value = Dist.CLIENT)
public class ClientMonsterChecklist {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(ModItems.MONSTER_CHECKLIST.get())) {
            Minecraft.getInstance().setScreen(new MonsterChecklistScreen());
        }
    }
}