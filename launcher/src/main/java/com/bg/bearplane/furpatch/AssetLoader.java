// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

public class AssetLoader
{
    public static Texture texture;
    public static Texture wallTex;
    public static TextureRegion[] wall;
    public static TextureRegion[] frame;
    public static Texture[] bg;
    public static TextureRegion[][] button;
    public static TextureRegion[][] font;
    public static int[] fontWidth;
    public static int[] fontX;
    public static boolean loaded;
    public static Texture bannerTex;
    
    static {
        AssetLoader.bg = new Texture[20];
        AssetLoader.loaded = false;
    }
    
    public static void load(final boolean firstRun) {
        loadTextures(firstRun);
        AssetLoader.loaded = true;
    }
    
    public static TextureRegion newTR(final Texture tex, final int x, final int y, final int w, final int h) {
        final TextureRegion t = new TextureRegion(tex, x, y, w, h);
        fix(t, false, true);
        return t;
    }
    
    public static void loadTextures(final boolean firstRun) {
        int i;
        for (i = 0, i = 0; i < 5; ++i) {
            loadTexture("bg" + i);
            AssetLoader.bg[i] = AssetLoader.texture;
        }
        loadTexture("font");
        AssetLoader.font = new TextureRegion[2][256];
        AssetLoader.fontWidth = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 5, 6, 9, 9, 10, 10, 3, 5, 5, 9, 7, 3, 8, 3, 7, 7, 5, 7, 7, 8, 7, 7, 7, 7, 7, 3, 3, 8, 6, 8, 7, 9, 7, 7, 7, 8, 7, 7, 7, 7, 5, 8, 7, 7, 9, 8, 7, 7, 8, 8, 7, 7, 7, 7, 9, 8, 7, 7, 5, 7, 5, 10, 7, 5, 7, 7, 7, 7, 7, 6, 7, 7, 5, 5, 7, 4, 9, 7, 7, 7, 8, 7, 7, 7, 7, 7, 9, 7, 7, 7, 6, 3, 6, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        AssetLoader.fontX = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 17, 25, 36, 47, 59, 71, 76, 83, 90, 101, 110, 115, 125, 130, 139, 148, 155, 164, 173, 183, 192, 201, 210, 219, 228, 233, 238, 248, 256, 266, 275, 286, 295, 304, 313, 323, 332, 341, 350, 359, 366, 376, 385, 394, 405, 415, 424, 433, 443, 453, 462, 471, 480, 489, 500, 510, 519, 528, 535, 544, 551, 563, 572, 579, 588, 597, 606, 615, 624, 632, 641, 650, 657, 664, 673, 679, 690, 699, 708, 717, 727, 736, 745, 754, 763, 772, 783, 792, 801, 810, 818, 823, 831, 841, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for (i = 0; i < 256; ++i) {
            if (AssetLoader.fontWidth[i] > 0) {
                final int[] fontWidth = AssetLoader.fontWidth;
                final int n = i;
                ++fontWidth[n];
                for (int t = 0; t < 2; ++t) {
                    fix(AssetLoader.font[t][i] = new TextureRegion(AssetLoader.texture, AssetLoader.fontX[i], t * 16, AssetLoader.fontWidth[i], 16), false, true);
                }
            }
        }
        loadTexture("frame");
        AssetLoader.frame = new TextureRegion[19];
        for (i = 0; i < 8; ++i) {
            AssetLoader.frame[i] = newTR(AssetLoader.texture, i * 32, 0, 32, 32);
        }
        AssetLoader.frame[8] = newTR(AssetLoader.texture, 0, 56, 8, 8);
        AssetLoader.frame[9] = newTR(AssetLoader.texture, 200, 42, 2, 22);
        AssetLoader.frame[10] = newTR(AssetLoader.texture, 214, 42, 2, 22);
        AssetLoader.frame[11] = newTR(AssetLoader.texture, 78, 56, 16, 16);
        AssetLoader.frame[12] = newTR(AssetLoader.texture, 78, 72, 16, 16);
        AssetLoader.frame[13] = newTR(AssetLoader.texture, 62, 56, 16, 16);
        AssetLoader.frame[14] = newTR(AssetLoader.texture, 62, 72, 16, 16);
        AssetLoader.frame[15] = newTR(AssetLoader.texture, 94, 72, 12, 16);
        AssetLoader.frame[16] = newTR(AssetLoader.texture, 94, 56, 12, 16);
        AssetLoader.frame[17] = newTR(AssetLoader.texture, 106, 56, 13, 13);
        AssetLoader.frame[18] = newTR(AssetLoader.texture, 106, 69, 13, 13);
        AssetLoader.button = new TextureRegion[2][10];
        for (int b = 0; b < 2; ++b) {
            for (i = 0; i < 8; ++i) {
                fix(AssetLoader.button[b][i] = new TextureRegion(AssetLoader.texture, 119 + i * 8, 56 + b * 8, 8, 8), false, true);
            }
            fix(AssetLoader.button[b][8] = new TextureRegion(AssetLoader.texture, 183, 56 + b * 8, 8, 8), false, true);
            fix(AssetLoader.button[b][9] = new TextureRegion(AssetLoader.texture, 191, 56 + b * 8, 8, 8), false, true);
        }
        loadTexture("wall");
        AssetLoader.wallTex = AssetLoader.texture;
        (AssetLoader.wall = new TextureRegion[2])[0] = newTR(AssetLoader.texture, 0, 0, 480, 63);
        AssetLoader.wall[1] = newTR(AssetLoader.texture, 0, 63, 480, 63);
        loadBanner();
    }
    
    public static void loadBanner() {
        loadTexture("banner");
        AssetLoader.bannerTex = AssetLoader.texture;
    }
    
    public static void loadTexture(final String name) {
        (AssetLoader.texture = new Texture(Gdx.files.local("assets/bearnecessities/" + name + ".png"))).setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    
    public static void fix(final TextureRegion tex, final boolean flipX, final boolean flipY) {
        fixBleeding(tex);
        tex.flip(flipX, flipY);
    }
    
    public static void fixBleeding(final TextureRegion region) {
        final float fix = 0.01f;
        final float x = (float)region.getRegionX();
        final float y = (float)region.getRegionY();
        final float width = (float)region.getRegionWidth();
        final float height = (float)region.getRegionHeight();
        final float invTexWidth = 1.0f / region.getTexture().getWidth();
        final float invTexHeight = 1.0f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight);
    }
    
    public static void dispose() {
        if (AssetLoader.texture != null) {
            AssetLoader.texture.dispose();
        }
    }
    
    public static float getStringWidth(final String s, final float scale, final float padding, final float spacing) {
        float total = 0.0f;
        if (s == null) {
            return 0.0f;
        }
        if (s.length() < 1) {
            return 0.0f;
        }
        char[] charArray;
        for (int length = (charArray = s.toCharArray()).length, i = 0; i < length; ++i) {
            final int ascii;
            final char c = (char)(ascii = charArray[i]);
            total += AssetLoader.fontWidth[ascii] * scale + padding * 2.0f + spacing;
        }
        return total;
    }
}
