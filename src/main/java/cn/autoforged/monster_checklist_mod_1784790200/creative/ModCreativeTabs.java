package cn.autoforged.monster_checklist_mod_1784790200.creative;

import cn.autoforged.monster_checklist_mod_1784790200.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ModCreativeTabs {

    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.MONSTER_CHECKLIST.get());
        }
    }
}
