package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Client-side night vision. */
public final class Fullbright extends Feature {
    public Fullbright() {
        super("fullbright", "Fullbright", Category.VISUAL,
                "Permanent night vision (client-side only).");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player == null) return;
        MobEffectInstance current = mc.player.getEffect(MobEffects.NIGHT_VISION);
        if (current == null || current.getDuration() < 400) {
            mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0, false, false));
        }
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
