package pl.kingvisuals.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pl.kingvisuals.Module;
import pl.kingvisuals.ModuleManager;

import java.util.List;

public class KingScreen extends Screen {
    private static final int PURPLE = 0xFFA855F7;
    private static final int BLUE = 0xFF3B82F6;
    private static final int GRAY = 0xFF8E89A8;
    private static final int ROW_H = 28;
    private static final int ROW_STEP = 32;

    private static String tab = "HUD";
    private String search = "";
    private boolean searchFocused = false;
    private int scroll = 0;

    // uklad (liczony w computeLayout)
    private int px, py, pw, ph, tabY, listTop, listBottom;

    public KingScreen() {
        super(Text.literal("KingVisuals"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void computeLayout() {
        pw = Math.min(width - 40, 620);
        ph = Math.min(height - 30, 340);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        tabY = py + 36;
        listTop = py + 64;
        listBottom = py + ph - 26;
    }

    private List<Module> visible() {
        String q = search.toLowerCase();
        return ModuleManager.MODULES.stream()
            .filter(m -> m.category.equals(tab))
            .filter(m -> q.isEmpty() || m.name.toLowerCase().contains(q))
            .toList();
    }

    private int maxScroll(int count) {
        return Math.max(0, count * ROW_STEP - (listBottom - listTop));
    }

    private int tabX(int index) {
        int x = px + 16;
        for (int i = 0; i < index; i++) x += textRenderer.getWidth(ModuleManager.TABS[i]) + 22 + 6;
        return x;
    }

    private int searchX() { return px + pw - 16 - 150; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        computeLayout();
        super.render(ctx, mouseX, mouseY, delta); // tlo + rozmycie
        List<Module> list = visible();
        scroll = Math.max(0, Math.min(scroll, maxScroll(list.size())));

        // panel
        Draw.round(ctx, px, py, pw, ph, 10, 0xB0120E24);

        // naglowek
        ctx.getMatrices().push();
        ctx.getMatrices().scale(1.5f, 1.5f, 1f);
        int kw = textRenderer.getWidth("King");
        ctx.drawText(textRenderer, "King", (int) ((px + 16) / 1.5f), (int) ((py + 10) / 1.5f), 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, "Visuals", (int) ((px + 16) / 1.5f) + kw, (int) ((py + 10) / 1.5f), PURPLE, true);
        ctx.getMatrices().pop();
        int titleEnd = px + 16 + (int) ((kw + textRenderer.getWidth("Visuals")) * 1.5f);
        ctx.drawText(textRenderer, "v1.0.0", titleEnd + 6, py + 20, GRAY, false);
        ctx.fill(px + 12, py + 31, px + pw - 12, py + 32, 0x22FFFFFF);

        // zakladki
        for (int i = 0; i < ModuleManager.TABS.length; i++) {
            String name = ModuleManager.TABS[i];
            int tx = tabX(i);
            int tw = textRenderer.getWidth(name) + 22;
            boolean selected = name.equals(tab);
            boolean hover = Draw.inside(mouseX, mouseY, tx, tabY, tw, 18);
            if (selected) Draw.roundGradient(ctx, tx, tabY, tw, 18, 9, PURPLE, BLUE);
            else if (hover) Draw.round(ctx, tx, tabY, tw, 18, 9, 0x22FFFFFF);
            ctx.drawText(textRenderer, name, tx + 11, tabY + 5, selected ? 0xFFFFFFFF : 0xFFB8B3CC, false);
        }

        // wyszukiwarka
        int sx = searchX();
        Draw.round(ctx, sx, tabY, 150, 18, 9, searchFocused ? 0x40FFFFFF : 0x28FFFFFF);
        if (search.isEmpty() && !searchFocused) {
            ctx.drawText(textRenderer, "Szukaj modułów...", sx + 10, tabY + 5, GRAY, false);
        } else {
            String shown = textRenderer.trimToWidth(search, 128);
            boolean blink = searchFocused && (System.currentTimeMillis() / 500) % 2 == 0;
            ctx.drawText(textRenderer, shown + (blink ? "_" : ""), sx + 10, tabY + 5, 0xFFFFFFFF, false);
        }

        // lista modulow
        ctx.enableScissor(px, listTop, px + pw, listBottom);
        int rx = px + 14;
        int rw = pw - 28;
        for (int i = 0; i < list.size(); i++) {
            Module m = list.get(i);
            int ry = listTop + i * ROW_STEP - scroll;
            if (ry + ROW_H < listTop || ry > listBottom) continue;
            boolean hover = Draw.inside(mouseX, mouseY, rx, ry, rw, ROW_H)
                && mouseY >= listTop && mouseY < listBottom;

            Draw.round(ctx, rx, ry, rw, ROW_H, 7, hover ? 0x38FFFFFF : 0x28FFFFFF);
            if (m.enabled) {
                Draw.round(ctx, rx, ry, rw, ROW_H, 7, 0x22A855F7);
                ctx.fillGradient(rx + 3, ry + 6, rx + 5, ry + ROW_H - 6, PURPLE, BLUE);
            }
            ctx.drawText(textRenderer, m.name, rx + 14, ry + 10, m.enabled ? 0xFFFFFFFF : 0xFFB8B3CC, true);
            String desc = textRenderer.trimToWidth(m.description, rw - 120 - 60);
            ctx.drawText(textRenderer, desc, rx + 120, ry + 10, GRAY, false);

            int sw = rx + rw - 42;
            int sy = ry + 7;
            if (m.enabled) Draw.roundGradient(ctx, sw, sy, 28, 14, 7, PURPLE, BLUE);
            else Draw.round(ctx, sw, sy, 28, 14, 7, 0x40FFFFFF);
            Draw.round(ctx, m.enabled ? sw + 28 - 12 : sw + 2, sy + 2, 10, 10, 5, 0xFFFFFFFF);
        }
        ctx.disableScissor();

        if (list.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, "Brak modułów", px + pw / 2, listTop + 20, GRAY);
        }

        // podpowiedz na dole
        ctx.drawCenteredTextWithShadow(textRenderer, "LPM: włącz/wyłącz   PPM: ustawienia", px + pw / 2, py + ph - 17, GRAY);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        computeLayout();

        // zakladki
        for (int i = 0; i < ModuleManager.TABS.length; i++) {
            int tw = textRenderer.getWidth(ModuleManager.TABS[i]) + 22;
            if (Draw.inside(mx, my, tabX(i), tabY, tw, 18)) {
                tab = ModuleManager.TABS[i];
                scroll = 0;
                return true;
            }
        }

        // wyszukiwarka
        searchFocused = Draw.inside(mx, my, searchX(), tabY, 150, 18);
        if (searchFocused) return true;

        // moduly
        if (my >= listTop && my < listBottom) {
            List<Module> list = visible();
            for (int i = 0; i < list.size(); i++) {
                int ry = listTop + i * ROW_STEP - scroll;
                if (Draw.inside(mx, my, px + 14, ry, pw - 28, ROW_H)) {
                    if (button == 0) {
                        list.get(i).toggle();
                        ModuleManager.save();
                    }
                    // button == 1 (PPM): ustawienia modulu - dojda razem z efektami
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        scroll -= (int) (vertical * 16);
        scroll = Math.max(0, Math.min(scroll, maxScroll(visible().size())));
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32 && search.length() < 24) {
            search += chr;
            scroll = 0;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchFocused) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
                search = search.substring(0, search.length() - 1);
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                searchFocused = false;
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
