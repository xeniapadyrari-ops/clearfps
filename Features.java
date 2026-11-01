package dev.clearfps.feature;

import dev.clearfps.feature.impl.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class Features {
    private static final List<Feature> ALL = new ArrayList<>();

    // Freecam first: it must release the movement keys before other features read them.
    public static final Freecam FREECAM = add(new Freecam());
    public static final Triggerbot TRIGGERBOT = add(new Triggerbot());
    public static final AutoTotem AUTO_TOTEM = add(new AutoTotem());
    public static final Hud HUD = add(new Hud());
    public static final InvisibleVision INVISIBLE_VISION = add(new InvisibleVision());
    public static final Fullbright FULLBRIGHT = add(new Fullbright());
    public static final Sprint SPRINT = add(new Sprint());
    public static final AutoRespawn AUTO_RESPAWN = add(new AutoRespawn());
    public static final AntiAfk ANTI_AFK = add(new AntiAfk());

    private Features() {}

    private static <T extends Feature> T add(T feature) {
        ALL.add(feature);
        return feature;
    }

    public static List<Feature> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static List<Feature> byCategory(Category category) {
        List<Feature> out = new ArrayList<>();
        for (Feature f : ALL) {
            if (f.category() == category) out.add(f);
        }
        return out;
    }

    public static void init() {
        HUD.loadState(true); // HUD is on by default
        load();
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            if (FREECAM.isEnabled()) FREECAM.setEnabled(false);
            return;
        }
        for (Feature f : ALL) {
            if (f.isEnabled()) f.onTick(mc);
        }
    }

    /** Used by EntityMixin: should this entity be drawn even though it is invisible? */
    public static boolean shouldRevealInvisible(Entity entity) {
        return INVISIBLE_VISION.isEnabled()
                && entity instanceof LivingEntity
                && entity.level().isClientSide()
                && entity != Minecraft.getInstance().player;
    }

    // ---- config ----------------------------------------------------------

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("clearfps.properties");
    }

    private static void load() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file())) {
            props.load(in);
        } catch (IOException e) {
            return;
        }
        for (Feature f : ALL) {
            String v = props.getProperty(f.id());
            if (v != null && f.persistent()) f.loadState(Boolean.parseBoolean(v));
        }
    }

    public static void save() {
        Properties props = new Properties();
        for (Feature f : ALL) {
            if (f.persistent()) props.setProperty(f.id(), Boolean.toString(f.isEnabled()));
        }
        try (OutputStream out = Files.newOutputStream(file())) {
            props.store(out, "clearfps");
        } catch (IOException ignored) {
        }
    }
}
