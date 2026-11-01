package dev.clearfps.gui;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import dev.clearfps.feature.Features;
import dev.clearfps.hud.ClearHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** The clearfps menu. Opens/closes with ";" - left click toggles, right click changes the option. */
public final class ClearScreen extends Screen {
    private static final int PANEL_W = 124;
    private static final int HEAD_H = 22;
    private static final int ROW_H = 18;
    private static final int GAP = 10;
    private static final int TOP = 48;

    public ClearScreen() {
        super(Component.literal("clearfps"));
    }

    private int panelLeft(int index) {
        int count = Category.values().length;
        int total = count * PANEL_W + (count - 1) * GAP;
        return (this.width - total) / 2 + index * (PANEL_W + GAP);
    }

    private Feature featureAt(double mx, double my) {
        Category[] cats = Category.values();
        for (int c = 0; c < cats.length; c++) {
            int x = panelLeft(c);
            List<Feature> list = Features.byCategory(cats[c]);
            for (int i = 0; i < list.size(); i++) {
                int y = TOP + HEAD_H + i * ROW_H;
                if (mx >= x && mx < x + PANEL_W && my >= y && my < y + ROW_H) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        Font font = Minecraft.getInstance().font;

        // Title
        String a = "clear";
        String b = "fps";
        int tx = (this.width - font.width(a) - font.width(b)) / 2;
        g.text(font, a, tx, 16, 0xFFFFFFFF, true);
        g.text(font, b, tx + font.width(a), 16, ClearHud.accent(0.0), true);
        String hint = "Left click: toggle    Right click: option    ;  /  Esc: close";
        g.text(font, hint, (this.width - font.width(hint)) / 2, 29, ClearHud.LABEL, false);

        Feature hovered = featureAt(mouseX, mouseY);
        Category[] cats = Category.values();
        for (int c = 0; c < cats.length; c++) {
            int x = panelLeft(c);
            List<Feature> list = Features.byCategory(cats[c]);
            int h = HEAD_H + list.size() * ROW_H + 4;

            ClearHud.panel(g, x, TOP, x + PANEL_W, TOP + h, 0xD0101018);
            ClearHud.gradientBar(g, x + 1, TOP, x + PANEL_W - 1, 2, c * 0.2);
            g.text(font, cats[c].label, x + 8, TOP + 8, 0xFFFFFFFF, true);

            for (int i = 0; i < list.size(); i++) {
                Feature f = list.get(i);
                int y = TOP + HEAD_H + i * ROW_H;
                int accent = ClearHud.accent(c * 0.2 + i * 0.1);
                boolean on = f.isEnabled();

                if (on) {
                    g.fill(x + 3, y, x + PANEL_W - 3, y + ROW_H - 2, (accent & 0x00FFFFFF) | 0x38000000);
                    g.fill(x + 3, y, x + 5, y + ROW_H - 2, accent);
                } else if (f == hovered) {
                    g.fill(x + 3, y, x + PANEL_W - 3, y + ROW_H - 2, 0x30FFFFFF);
                }

                g.text(font, f.name(), x + 10, y + 4, on ? 0xFFFFFFFF : 0xFFA0A7BA, true);
                String suffix = f.suffix();
                if (suffix != null) {
                    g.text(font, suffix, x + PANEL_W - 8 - font.width(suffix), y + 4, ClearHud.LABEL, true);
                }
            }
        }

        if (hovered != null) {
            String desc = hovered.description();
            int dw = font.width(desc);
            int dx = (this.width - dw) / 2;
            ClearHud.panel(g, dx - 8, this.height - 34, dx + dw + 8, this.height - 18, 0xD0101018);
            g.text(font, desc, dx, this.height - 30, 0xFFE6E9F2, false);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        Feature f = featureAt(event.x(), event.y());
        if (f != null) {
            if (event.button() == 0) {
                f.toggle();
            } else if (event.button() == 1) {
                f.onRightClick();
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_SEMICOLON) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }
}
