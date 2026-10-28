package pl.kingvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import pl.kingvisuals.gui.KingScreen;

public class KingVisualsClient implements ClientModInitializer {
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kingvisuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.kingvisuals"));

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ModuleManager.load());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ModuleManager.save());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new KingScreen());
            }
        });

        HudRenderCallback.EVENT.register((ctx, tickCounter) -> HudOverlay.render(ctx));
    }
}
