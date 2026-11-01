package dev.clearfps;

import com.mojang.blaze3d.platform.InputConstants;
import dev.clearfps.feature.Features;
import dev.clearfps.gui.ClearScreen;
import dev.clearfps.hud.ClearHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ClearFps implements ClientModInitializer {
    public static final String MOD_ID = "clearfps";

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        Features.init();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        // Menu key: ";" (can be rebound in Controls)
        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.clearfps.menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_SEMICOLON,
                category));

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
                ClearHud::render);

        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            while (openMenuKey.consumeClick()) {
                if (mc.player != null) {
                    mc.setScreen(new ClearScreen());
                }
            }
            Features.tick(mc);
        });
    }
}
