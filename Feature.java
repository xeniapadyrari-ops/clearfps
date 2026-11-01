package dev.clearfps.feature;

import net.minecraft.client.Minecraft;

public abstract class Feature {
    private final String id;
    private final String name;
    private final Category category;
    private final String description;
    private boolean enabled;

    protected Feature(String id, String name, Category category, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public final String id() { return id; }
    public final String name() { return name; }
    public final Category category() { return category; }
    public final String description() { return description; }
    public final boolean isEnabled() { return enabled; }

    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
        Features.save();
    }

    /** Restores the saved state without running onEnable (used when loading config). */
    final void loadState(boolean value) {
        this.enabled = value;
    }

    protected void onEnable() {}
    protected void onDisable() {}

    /** Called every client tick while the feature is enabled and a world is loaded. */
    public void onTick(Minecraft mc) {}

    /** Right click on the feature in the menu. */
    public void onRightClick() {}

    /** Small gray text next to the name in the HUD list / menu. */
    public String suffix() { return null; }

    public boolean persistent() { return true; }
    public boolean showInList() { return true; }
}
