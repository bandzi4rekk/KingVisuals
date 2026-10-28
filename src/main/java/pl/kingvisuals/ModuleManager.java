package pl.kingvisuals;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class ModuleManager {
    public static final String[] TABS = {"HUD", "Visuals", "Client", "FPS"};
    public static final List<Module> MODULES = new ArrayList<>();

    public static Module WATERMARK, SHOW_FPS, SHOW_PING;

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("kingvisuals.properties");

    private ModuleManager() {}

    public static void init() {
        // HUD
        WATERMARK = add(new Module("Watermark", "Napis KingVisuals w prawym górnym rogu", "HUD"));
        SHOW_FPS = add(new Module("FPS", "Pokazuje aktualne FPS", "HUD"));
        SHOW_PING = add(new Module("Ping", "Pokazuje ping do serwera", "HUD"));
        WATERMARK.enabled = true;
        SHOW_FPS.enabled = true;
        SHOW_PING.enabled = true;

        // Visuals (na razie tylko przełączniki, efekty dojdą w kolejnych krokach)
        add(new Module("TargetHUD", "Imię i życie ostatnio uderzonego celu", "Visuals"));
        add(new Module("Hit Particles", "Własne particle przy trafieniu", "Visuals"));
        add(new Module("Totem Particles", "Własne particle totemu", "Visuals"));
        add(new Module("Jump Circles", "Kręgi przy skoku", "Visuals"));
        add(new Module("Pearl Trajectory", "Tor lotu perły", "Visuals"));
        add(new Module("Trails", "Ślad za graczem", "Visuals"));
        add(new Module("China Hat", "Kapelusz nad głową gracza", "Visuals"));
        add(new Module("Ring Crosshair", "Celownik w kształcie pierścienia", "Visuals"));
        add(new Module("Własne pelerynki", "Własny wygląd peleryny", "Visuals"));

        // Client
        add(new Module("Dźwięki", "Własne dźwięki trafień i critów", "Client"));

        // FPS
        for (Module m : FpsBoost.create()) add(m);
    }

    private static Module add(Module m) {
        MODULES.add(m);
        return m;
    }

    /** Wywolywane po starcie klienta, bo moduly FPS zmieniaja opcje gry. */
    public static void load() {
        Properties p = new Properties();
        if (Files.exists(FILE)) {
            try (InputStream in = Files.newInputStream(FILE)) {
                p.load(in);
            } catch (IOException ignored) {}
        }
        for (Module m : MODULES) {
            String v = p.getProperty(m.name);
            if (v != null) {
                boolean want = Boolean.parseBoolean(v);
                // pole 'enabled' dla domyslnie wlaczonych HUD ustawiamy recznie, reszta przez setEnabled
                if (m.enabled && !want) m.enabled = false;
                else m.setEnabled(want);
            }
        }
    }

    public static void save() {
        Properties p = new Properties();
        for (Module m : MODULES) p.setProperty(m.name, Boolean.toString(m.enabled));
        try (OutputStream out = Files.newOutputStream(FILE)) {
            p.store(out, "KingVisuals");
        } catch (IOException ignored) {}
    }
}
