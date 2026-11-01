package dev.clearfps.mixin;

import dev.clearfps.feature.Features;
import dev.clearfps.feature.impl.Freecam;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    // --- Invisible Vision ---------------------------------------------------

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void clearfps$revealInvisible(CallbackInfoReturnable<Boolean> cir) {
        if (Features.shouldRevealInvisible((Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void clearfps$revealInvisibleTo(Player viewer, CallbackInfoReturnable<Boolean> cir) {
        if (Features.shouldRevealInvisible((Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    // --- Freecam: mouse look rotates the camera instead of the player --------

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void clearfps$freecamTurn(double yRot, double xRot, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer && Freecam.isRunning()) {
            Freecam.turn(yRot, xRot);
            ci.cancel();
        }
    }
}
