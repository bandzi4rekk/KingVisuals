package pl.kingvisuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public final class HudOverlay {
    public static final int PURPLE = 0xFFA855F7;
    public static final int BLUE = 0xFF3B82F6;

    private HudOverlay() {}

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.currentScreen != null || mc.player == null) return;

        int right = ctx.getScaledWindowWidth() - 4;
        int y = 4;

        if (ModuleManager.WATERMARK.enabled) {
            String title = "KingVisuals";
            ctx.drawText(mc.textRenderer, title, right - mc.textRenderer.getWidth(title), y, PURPLE, true);
            y += 11;
        }

        StringBuilder line = new StringBuilder();
        if (ModuleManager.SHOW_FPS.enabled) line.append("FPS: ").append(mc.getCurrentFps());
        if (ModuleManager.SHOW_PING.enabled) {
            if (line.length() > 0) line.append("  ");
            PlayerListEntry entry = mc.getNetworkHandler() == null ? null
                : mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            line.append("Ping: ").append(entry == null ? "-" : entry.getLatency() + "ms");
        }
        if (line.length() > 0) {
            String s = line.toString();
            ctx.drawText(mc.textRenderer, s, right - mc.textRenderer.getWidth(s), y, 0xFFE9E4FF, true);
        }
    }
}
