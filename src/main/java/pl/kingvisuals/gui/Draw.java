package pl.kingvisuals.gui;

import net.minecraft.client.gui.DrawContext;

/** Proste rysowanie zaokraglonych prostokatow i gradientow (DrawContext nie ma tego wbudowanego). */
public final class Draw {
    private Draw() {}

    public static int lerp(int a, int b, float t) {
        int ar = (a >> 24) & 255, rr = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int br = (b >> 24) & 255, r2 = (b >> 16) & 255, g2 = (b >> 8) & 255, b2 = b & 255;
        return ((int) (ar + (br - ar) * t) << 24)
             | ((int) (rr + (r2 - rr) * t) << 16)
             | ((int) (ag + (g2 - ag) * t) << 8)
             | (int) (ab + (b2 - ab) * t);
    }

    private static int inset(int d, int r) {
        if (d >= r) return 0;
        double dy = r - d - 0.5;
        return (int) Math.ceil(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
    }

    public static void round(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        for (int row = 0; row < h; row++) {
            int in = inset(Math.min(row, h - 1 - row), r);
            ctx.fill(x + in, y + row, x + w - in, y + row + 1, color);
        }
    }

    /** Zaokraglony prostokat z gradientem poziomym (od lewej do prawej). */
    public static void roundGradient(DrawContext ctx, int x, int y, int w, int h, int r, int left, int right) {
        r = Math.min(r, Math.min(w, h) / 2);
        for (int col = 0; col < w; col++) {
            int in = inset(Math.min(col, w - 1 - col), r);
            ctx.fill(x + col, y + in, x + col + 1, y + h - in, lerp(left, right, col / (float) Math.max(1, w - 1)));
        }
    }

    public static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
