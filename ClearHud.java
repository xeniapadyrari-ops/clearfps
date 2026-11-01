package dev.clearfps.hud;

import dev.clearfps.feature.Feature;
import dev.clearfps.feature.Features;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ClearHud {
    public static final int BG = 0xB0101018;
    public static final int LABEL = 0xFF8C93A8;
    public static final int VALUE = 0xFFFFFFFF;

    private static final int PAD = 6;

    private ClearHud() {}

    // ---- shared drawing helpers (also used by the menu) ----------------------

    /** Animated cyan -> violet accent color. */
    public static int accent(double phase) {
        double t = (System.currentTimeMillis() % 4000L) / 4000.0 * Math.PI * 2.0;
        float wave = 0.5F + 0.5F * (float) Math.sin(t + phase * 3.0);
        float hue = 0.52F + 0.16F * wave;
        return 0xFF000000 | Mth.hsvToRgb(hue, 0.55F, 1.0F);
    }

    /** Rectangle with notched (rounded-looking) corners. */
    public static void panel(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1 + 1, y1, x2 - 1, y1 + 1, color);
        g.fill(x1, y1 + 1, x2, y2 - 1, color);
        g.fill(x1 + 1, y2 - 1, x2 - 1, y2, color);
    }

    public static void gradientBar(GuiGraphicsExtractor g, int x1, int y, int x2, int h, double phase) {
        for (int x = x1; x < x2; x += 2) {
            g.fill(x, y, Math.min(x + 2, x2), y + h, accent(phase + (x - x1) / 160.0));
        }
    }

    // ---- HUD entry point -----------------------------------------------------

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        if (!Features.HUD.isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null) return;
        Font font = mc.font;

        int nextY = infoPanel(g, mc, p, font);
        playerRadar(g, mc, p, font, nextY + 4);
        featureList(g, mc, font);
    }

    private static int infoPanel(GuiGraphicsExtractor g, Minecraft mc, LocalPlayer p, Font font) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"FPS", Integer.toString(mc.getFps())});

        PlayerInfo info = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(p.getUUID());
        if (info != null) {
            rows.add(new String[]{"Ping", info.getLatency() + " ms"});
        }

        rows.add(new String[]{"XYZ", coords(p.getX(), p.getY(), p.getZ())});
        if (mc.level.dimension().equals(Level.OVERWORLD)) {
            rows.add(new String[]{"Nether", coords(p.getX() / 8.0, p.getY(), p.getZ() / 8.0)});
        } else if (mc.level.dimension().equals(Level.NETHER)) {
            rows.add(new String[]{"Overworld", coords(p.getX() * 8.0, p.getY(), p.getZ() * 8.0)});
        }
        rows.add(new String[]{"Facing", facing(p.getDirection())});

        int labelW = 0;
        int valueW = 0;
        for (String[] r : rows) {
            labelW = Math.max(labelW, font.width(r[0]));
            valueW = Math.max(valueW, font.width(r[1]));
        }
        int titleW = font.width("clearfps") + 24;
        int w = Math.max(PAD * 2 + labelW + 10 + valueW, PAD * 2 + titleW);

        int x = 6;
        int y = 6;
        int h = 20 + rows.size() * 11 + 3;
        panel(g, x, y, x + w, y + h, BG);
        gradientBar(g, x + 1, y, x + w - 1, 2, 0.0);

        g.text(font, "clear", x + PAD, y + 7, VALUE, true);
        g.text(font, "fps", x + PAD + font.width("clear"), y + 7, accent(0.0), true);

        int ly = y + 20;
        for (String[] r : rows) {
            g.text(font, r[0], x + PAD, ly, LABEL, true);
            g.text(font, r[1], x + PAD + labelW + 10, ly, VALUE, true);
            ly += 11;
        }
        return y + h;
    }

    private static void playerRadar(GuiGraphicsExtractor g, Minecraft mc, LocalPlayer p, Font font, int y) {
        List<Player> others = new ArrayList<>();
        for (Player other : mc.level.players()) {
            if (other != p) others.add(other);
        }
        if (others.isEmpty()) return;
        others.sort(Comparator.comparingDouble(o -> o.distanceToSqr(p)));

        int shown = Math.min(others.size(), 6);
        String header = "Players  " + others.size();
        int w = font.width(header) + PAD * 2;
        String[] names = new String[shown];
        String[] dists = new String[shown];
        for (int i = 0; i < shown; i++) {
            Player o = others.get(i);
            names[i] = o.getName().getString();
            dists[i] = Math.round(Math.sqrt(o.distanceToSqr(p))) + "m";
            w = Math.max(w, PAD * 2 + font.width(names[i]) + 10 + font.width(dists[i]));
        }

        int x = 6;
        int h = 17 + shown * 11 + 3;
        panel(g, x, y, x + w, y + h, BG);
        g.fill(x + 1, y, x + 3, y + h, accent(0.5));
        g.text(font, header, x + PAD + 2, y + 5, accent(0.5), true);

        int ly = y + 17;
        for (int i = 0; i < shown; i++) {
            double dist = Math.sqrt(others.get(i).distanceToSqr(p));
            int color = dist < 16 ? 0xFFFF5555 : dist < 48 ? 0xFFFFD155 : 0xFF9AE6A0;
            g.text(font, names[i], x + PAD + 2, ly, VALUE, true);
            g.text(font, dists[i], x + w - PAD - font.width(dists[i]), ly, color, true);
            ly += 11;
        }
    }

    private static void featureList(GuiGraphicsExtractor g, Minecraft mc, Font font) {
        List<Feature> active = new ArrayList<>();
        for (Feature f : Features.all()) {
            if (f.isEnabled() && f.showInList()) active.add(f);
        }
        active.sort(Comparator.comparingInt((Feature f) -> -rowWidth(font, f)));

        int screenW = mc.getWindow().getGuiScaledWidth();
        int y = 6;
        for (int i = 0; i < active.size(); i++) {
            Feature f = active.get(i);
            String suffix = f.suffix();
            int nameW = font.width(f.name());
            int x2 = screenW - 6;
            int x1 = x2 - rowWidth(font, f) - 10;
            int color = accent(i * 0.12);

            panel(g, x1, y, x2, y + 12, BG);
            g.fill(x2 - 2, y + 1, x2, y + 11, color);
            g.text(font, f.name(), x1 + 4, y + 2, color, true);
            if (suffix != null) {
                g.text(font, suffix, x1 + 4 + nameW + 4, y + 2, LABEL, true);
            }
            y += 13;
        }
    }

    private static int rowWidth(Font font, Feature f) {
        String suffix = f.suffix();
        return font.width(f.name()) + (suffix == null ? 0 : font.width(suffix) + 4);
    }

    private static String coords(double x, double y, double z) {
        return String.format(Locale.ROOT, "%.1f  %.1f  %.1f", x, y, z);
    }

    private static String facing(Direction d) {
        return switch (d) {
            case NORTH -> "North  -Z";
            case SOUTH -> "South  +Z";
            case EAST -> "East  +X";
            case WEST -> "West  -X";
            default -> d.getName();
        };
    }
}
