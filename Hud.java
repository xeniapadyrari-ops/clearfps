package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;

/** Toggle for the clearfps HUD (rendering lives in ClearHud). */
public final class Hud extends Feature {
    public Hud() {
        super("hud", "HUD", Category.VISUAL,
                "Watermark, FPS / ping / coords panel, nearby players and the active module list.");
    }

    @Override
    public boolean showInList() {
        return false;
    }
}
