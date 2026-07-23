package cn.autoforged.monster_checklist_mod_1784790200.datagen;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import cn.autoforged.monster_checklist_mod_1784790200.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MainMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.MONSTER_CHECKLIST.get());
    }
}
