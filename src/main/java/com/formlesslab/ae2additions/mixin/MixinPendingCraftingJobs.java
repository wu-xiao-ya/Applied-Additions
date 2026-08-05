package com.formlesslab.ae2additions.mixin;

import ae2.api.stacks.AEKey;
import ae2.client.gui.me.common.PendingCraftingJobs;
import ae2.core.network.clientbound.CraftingJobStatusPacket;
import com.formlesslab.ae2additions.client.notification.WindowsCraftingNotification;
import com.formlesslab.ae2additions.init.Configurations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = PendingCraftingJobs.class, remap = false)
public abstract class MixinPendingCraftingJobs {
    @Inject(method = "jobStatus", at = @At("HEAD"))
    private static void ae2additions$notifyFinishedCraftingJob(UUID id, AEKey what, long requestedAmount, long remainingAmount, CraftingJobStatusPacket.Status status, CallbackInfo ci) {
        if (Configurations.CLIENT.craftingJobSystemNotifications && status == CraftingJobStatusPacket.Status.FINISHED) {
            WindowsCraftingNotification.showWhenUnfocused(what, requestedAmount);
        }
    }
}
