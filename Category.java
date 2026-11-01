package dev.clearfps.feature;

public enum Category {
    COMBAT("Combat"),
    VISUAL("Visual"),
    ANARCHY("Anarchy");

    public final String label;

    Category(String label) {
        this.label = label;
    }
}
