package cn.autoforged.monster_checklist_mod_1784790200.tracking;

import cn.autoforged.monster_checklist_mod_1784790200.MainMod;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MainMod.MODID);

    public static final Supplier<AttachmentType<Integer>> CLAIMED_MILESTONES =
            ATTACHMENT_TYPES.register("claimed_milestones",
                    () -> AttachmentType.builder(() -> 0)
                            .serialize(Codec.INT)
                            .build());

    public static final Supplier<AttachmentType<CompoundTag>> ATTRIBUTE_BOOSTS =
            ATTACHMENT_TYPES.register("attribute_boosts",
                    () -> AttachmentType.builder(() -> new CompoundTag())
                            .serialize(CompoundTag.CODEC)
                            .build());
}
