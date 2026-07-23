package cn.autoforged.monster_checklist_mod_1784790200.item;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MainMod.MODID);

    public static final DeferredItem<Item> MONSTER_CHECKLIST = registerItem("monster_checklist",
            () -> new MonsterChecklistItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }
}
