package cn.autoforged.monster_checklist_mod_1784790200.attribute;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, MainMod.MODID);

    public static final DeferredHolder<Attribute, Attribute> DODGE_CHANCE = ATTRIBUTES.register("dodge_chance",
            () -> new RangedAttribute("attribute." + MainMod.MODID + ".dodge_chance", 0.0, 0.0, 1.0).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> CRIT_CHANCE = ATTRIBUTES.register("crit_chance",
            () -> new RangedAttribute("attribute." + MainMod.MODID + ".crit_chance", 0.0, 0.0, 1.0).setSyncable(true));

}
