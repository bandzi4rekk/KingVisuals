package pl.kingvisuals;

public class Module {
    public final String name;
    public final String description;
    public final String category;
    public boolean enabled;
    private final Runnable onEnable;
    private final Runnable onDisable;

    public Module(String name, String description, String category) {
        this(name, description, category, null, null);
    }

    public Module(String name, String description, String category, Runnable onEnable, Runnable onDisable) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.onEnable = onEnable;
        this.onDisable = onDisable;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value && onEnable != null) onEnable.run();
        if (!value && onDisable != null) onDisable.run();
    }
}
