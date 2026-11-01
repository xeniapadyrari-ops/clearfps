package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import dev.clearfps.feature.Features;
import net.minecraft.client.Minecraft;

public final class Sprint extends Feature {
    public Sprint() {
        super("sprint", "Auto Sprint", Category.ANARCHY,
                "Holds sprint for you while you walk forward.");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player == null || Features.FREECAM.isEnabled()) return;
        if (mc.options.keyUp.isDown() && !mc.player.isShiftKeyDown()) {
            mc.options.keySprint.setDown(true);
        }
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        mc.options.keySprint.setDown(false);
    }
}
