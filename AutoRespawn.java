package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class AutoRespawn extends Feature {
    private int deadTicks;

    public AutoRespawn() {
        super("auto_respawn", "Auto Respawn", Category.ANARCHY,
                "Clicks Respawn for you right after you die.");
    }

    @Override
    public void onTick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        if (p.isDeadOrDying()) {
            if (++deadTicks == 10) {
                p.respawn();
            }
        } else {
            deadTicks = 0;
        }
    }
}
