package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;

/** The actual work happens in EntityMixin (isInvisible / isInvisibleTo return false). */
public final class InvisibleVision extends Feature {
    public InvisibleVision() {
        super("invisible_vision", "Invis Vision", Category.VISUAL,
                "Renders invisible players and mobs as if they were visible.");
    }
}
