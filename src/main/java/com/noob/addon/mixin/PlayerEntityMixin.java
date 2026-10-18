package com.noob.addon.mixin;

import com.noob.addon.modules.TridentCooldown;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldownManager.class)
public class PlayerEntityMixin {

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void onSetCooldown(Item item, int duration, CallbackInfo ci) {
        if (item != Items.TRIDENT) return;

        TridentCooldown mod = Modules.get().get(TridentCooldown.class);
        if (mod == null || !mod.isActive()) return;

        double targetSeconds = mod.cooldownSeconds.get();

        if (targetSeconds == 0.0) {
            ci.cancel();
            return;
        }

        ci.cancel();
        int customTicks = (int)(targetSeconds * 20.0);
        if (customTicks > 0) {
            ((ItemCooldownManager)(Object)this).set(item, customTicks);
        }
    }
}