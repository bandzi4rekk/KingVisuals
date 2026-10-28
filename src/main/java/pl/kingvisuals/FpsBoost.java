package pl.kingvisuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.SimpleOption;

import java.util.List;
import java.util.function.Function;

/** Moduly FPS: zmieniaja opcje gry po wlaczeniu i przywracaja stare wartosci po wylaczeniu. */
public final class FpsBoost {
    private FpsBoost() {}

    public static List<Module> create() {
        return List.of(
            option("Mniej particli", "Minimalna ilość particli w grze", GameOptions::getParticles, ParticlesMode.MINIMAL),
            option("Bez chmur", "Wyłącza renderowanie chmur", GameOptions::getCloudRenderMode, CloudRenderMode.OFF),
            option("Szybka grafika", "Tryb Fast zamiast Fancy", GameOptions::getGraphicsMode, GraphicsMode.FAST),
            option("Zasięg encji", "Zmniejsza odległość renderowania encji", GameOptions::getEntityDistanceScaling, 0.5),
            option("Bez cieni encji", "Wyłącza cienie pod mobami i graczami", GameOptions::getEntityShadows, false),
            option("Bez mieszania biomów", "Biome blend ustawiony na 0", GameOptions::getBiomeBlendRadius, 0)
        );
    }

    private static <T> Module option(String name, String desc, Function<GameOptions, SimpleOption<T>> getter, T boosted) {
        Object[] previous = {null};
        return new Module(name, desc, "FPS",
            () -> {
                SimpleOption<T> o = getter.apply(MinecraftClient.getInstance().options);
                previous[0] = o.getValue();
                o.setValue(boosted);
            },
            () -> {
                if (previous[0] == null) return;
                @SuppressWarnings("unchecked") T old = (T) previous[0];
                getter.apply(MinecraftClient.getInstance().options).setValue(old);
            });
    }
}
