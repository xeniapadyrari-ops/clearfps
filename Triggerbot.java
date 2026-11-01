package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import dev.clearfps.feature.Features;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public final class Triggerbot extends Feature {
    private boolean playersOnly = true;

    public Triggerbot() {
        super("triggerbot", "Triggerbot", Category.COMBAT,
                "Attacks whatever is under the crosshair when the cooldown is ready. Right click: players only / all mobs.");
    }

    @Override
    public void onTick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.gameMode == null) return;
        if (Features.FREECAM.isEnabled() || !mc.mouseHandler.isMouseGrabbed() || p.isUsingItem()) return;
        if (!(mc.hitResult instanceof EntityHitResult hit)) return;

        Entity target = hit.getEntity();
        if (target == p || !(target instanceof LivingEntity living) || !living.isAlive()) return;
        if (playersOnly && !(target instanceof Player)) return;
        if (p.getAttackStrengthScale(0.5F) < 0.95F) return;

        mc.gameMode.attack(p, target);
        p.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public void onRightClick() {
        playersOnly = !playersOnly;
    }

    @Override
    public String suffix() {
        return playersOnly ? "players" : "all";
    }
}
