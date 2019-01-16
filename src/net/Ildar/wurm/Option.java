package net.Ildar.wurm;

public enum Option {
    ShowPrettySpellList("Show pretty spell list", "Adds a new spell list to the list of available actions"),
    ListRowSize("Pretty spell list row size", "Configures the row size of the pretty spell list"),
    HideOldSpellActions("Hide default spell actions", "Hides default spell actions from popup menu");

    static final String section = "Game";
    String description;
    String tooltip;

    Option(String description, String tooltip) {
        this.description = description;
        this.tooltip = tooltip;
    }
}
