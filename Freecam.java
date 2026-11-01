package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

/**
 * Detaches the camera from the player: a invisible Marker entity becomes the camera entity.
 * The real player is frozen (movement keys are released, mouse look is redirected to the camera).
 */
public final class Freecam extends Feature {
    private static final double[] SPEEDS = {0.5, 1.0, 2.0, 4.0};

    private static Marker camera;
    private static int speedIndex = 1;

    public Freecam() {
        super("freecam", "Freecam", Category.VISUAL,
                "Fly the camera around. WASD / Space / Shift, Ctrl = faster. Right click: change speed.");
    }

    public static boolean isRunning() {
        return camera != null;
    }

    /** Called from EntityMixin instead of LocalPlayer.turn while freecam is active. */
    public static void turn(double yRot, double xRot) {
        if (camera == null) return;
        float dy = (float) yRot * 0.15F;
        float dx = (float) xRot * 0.15F;
        camera.setYRot(camera.getYRot() + dy);
        camera.setXRot(Mth.clamp(camera.getXRot() + dx, -90.0F, 90.0F));
        camera.yRotO += dy;
        camera.xRotO = Mth.clamp(camera.xRotO + dx, -90.0F, 90.0F);
    }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            setEnabled(false);
            return;
        }
        Vec3 eye = mc.player.getEyePosition();
        camera = new Marker(EntityType.MARKER, mc.level);
        camera.setPos(eye.x, eye.y, eye.z);
        camera.setYRot(mc.player.getYRot());
        camera.setXRot(mc.player.getXRot());
        camera.setOldPosAndRot();
        mc.setCameraEntity(camera);
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setCameraEntity(mc.player);
        }
        camera = null;
    }

    @Override
    public void onTick(Minecraft mc) {
        if (camera == null) return;
        if (camera.level() != mc.level) { // dimension change / reconnect
            setEnabled(false);
            return;
        }
        if (mc.getCameraEntity() != camera) {
            mc.setCameraEntity(camera);
        }

        camera.setOldPosAndRot();

        long window = mc.getWindow().handle();
        boolean grabbed = mc.mouseHandler.isMouseGrabbed();
        double forward = 0, strafe = 0, vertical = 0;
        if (grabbed) {
            forward = axis(window, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S);
            strafe = axis(window, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_A);
            vertical = axis(window, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
        double step = 0.5 * SPEEDS[speedIndex] * (down(window, GLFW.GLFW_KEY_LEFT_CONTROL) ? 3.0 : 1.0);

        float yaw = camera.getYRot() * Mth.DEG_TO_RAD;
        float pitch = camera.getXRot() * Mth.DEG_TO_RAD;
        double fx = -Mth.sin(yaw) * Mth.cos(pitch);
        double fy = -Mth.sin(pitch);
        double fz = Mth.cos(yaw) * Mth.cos(pitch);
        double rx = -Mth.cos(yaw);
        double rz = -Mth.sin(yaw);

        camera.setPos(
                camera.getX() + (fx * forward + rx * strafe) * step,
                camera.getY() + (fy * forward + vertical) * step,
                camera.getZ() + (fz * forward + rz * strafe) * step);

        // Freeze the real player: release movement + block attack/use clicks.
        Options o = mc.options;
        KeyMapping[] frozen = {o.keyUp, o.keyDown, o.keyLeft, o.keyRight, o.keyJump, o.keyShift,
                o.keySprint, o.keyAttack, o.keyUse};
        for (KeyMapping k : frozen) {
            k.setDown(false);
        }
        while (o.keyAttack.consumeClick()) { }
        while (o.keyUse.consumeClick()) { }
        while (o.keyPickItem.consumeClick()) { }
    }

    @Override
    public void onRightClick() {
        speedIndex = (speedIndex + 1) % SPEEDS.length;
    }

    @Override
    public String suffix() {
        return SPEEDS[speedIndex] + "x";
    }

    @Override
    public boolean persistent() {
        return false;
    }

    private static boolean down(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    private static double axis(long window, int positive, int negative) {
        return (down(window, positive) ? 1 : 0) - (down(window, negative) ? 1 : 0);
    }
}
