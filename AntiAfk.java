package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

/** Every ~20s: swing the arm and nudge the view so idle-kick timers reset. */
public final class AntiAfk extends Feature {
    private int ticks;
    private boolean flip;

    public AntiAfk() {
        super("anti_afk", "Anti AFK", Category.ANARCHY,
                "Swings your arm and turns slightly every 20 seconds to avoid AFK kicks.");
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @Override
    public void onTick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || p.isDeadOrDying()) return;
        if (++ticks < 400) return;
        ticks = 0;
        flip = !flip;
        p.swing(InteractionHand.MAIN_HAND);
        p.setYRot(p.getYRot() + (flip ? 12.0F : -12.0F));
    }
}
