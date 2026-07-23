package cn.autoforged.monster_checklist_mod_1784790200.mixin;

import cn.autoforged.monster_checklist_mod_1784790200.ModEventHandlers;
import com.evandev.fieldguide.server.progress.PlayerFieldGuideProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerFieldGuideProgress.class)
public class FieldGuideUnlockMixin {

    @Inject(method = "unlock", at = @At("TAIL"))
    private void monsterChecklist_onUnlock(ServerPlayer player, ResourceLocation entryId, String variantId, boolean grantXp, CallbackInfo ci) {
        ModEventHandlers.checkAndGrantMilestones(player);
    }
}
