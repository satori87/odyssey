// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen
{
    OrthographicCamera cam;
    ShapeRenderer shapeRenderer;
    SpriteBatch batcher;
    boolean screenLoaded;
    Input input;
    boolean rendering;
    public Patch patch;
    long tick;
    
    public GameScreen() {
        this.screenLoaded = false;
        this.rendering = false;
        this.patch = new Patch(this);
        this.tick = 0L;
        (this.cam = new OrthographicCamera()).setToOrtho(true, 640.0f, 480.0f);
        (this.batcher = new SpriteBatch()).setProjectionMatrix(this.cam.combined);
        this.input = new Input(this);
        Gdx.input.setInputProcessor((InputProcessor)this.input);
        this.screenLoaded = true;
    }
    
    public void render(final float delta) {
        while (this.accessRenderState(false, false)) {}
        this.accessRenderState(true, true);
        this.tick = System.currentTimeMillis();
        if (AssetLoader.loaded && this.screenLoaded) {
            this.patch.update(this.tick);
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            Gdx.gl.glClear(16384);
            this.batcher.enableBlending();
            this.batcher.begin();
            this.patch.render();
            this.batcher.end();
        }
        this.accessRenderState(true, false);
    }
    
    public synchronized boolean accessRenderState(final boolean changeIt, final boolean changeTo) {
        if (changeIt) {
            this.rendering = changeTo;
        }
        return this.rendering;
    }
    
    void drawRegion(final TextureRegion region, int X, int Y, final boolean centered, final float rotation, final float scale) {
        if (region == null) {
            return;
        }
        final int width = region.getRegionWidth();
        final int height = region.getRegionHeight();
        if (centered) {
            X -= width / 2;
            Y -= height / 2;
        }
        if (centered) {
            this.batcher.draw(region, (float)X, (float)Y, (float)(width / 2), (float)(height / 2), (float)width, (float)height, scale, scale, rotation);
        }
        else {
            this.batcher.draw(region, (float)X, (float)Y, 0.0f, 0.0f, (float)width, (float)height, scale, scale, rotation);
        }
    }
    
    boolean inBox(final int x, final int y, final int centerX, final int centerY, final int width, final int height) {
        final int topY = centerY - height / 2;
        final int bottomY = centerY + height / 2;
        final int leftX = centerX - width / 2;
        final int rightX = centerX + width / 2;
        return x > leftX && x < rightX && y > topY && y < bottomY;
    }
    
    void checkClick() {
        final int x = Input.mouseX;
        final int y = Input.mouseY;
        if (Input.mouseDown && Input.wasMouseJustClicked) {
            Input.wasMouseJustClicked = false;
            this.inBox(x, y, 320, 240, 96, 48);
        }
    }
    
    void drawFrame(final int x, final int y, final int width, final int height, final boolean useBackground) {
        final TextureRegion[] frame = AssetLoader.frame;
        if (useBackground) {
            for (int a = 0; a < height; a += 32) {
                for (int b = 0; b < width; b += 32) {}
            }
        }
        this.drawRegion(frame[0], x, y, false, 0.0f, 1.0f);
        this.drawRegion(frame[1], x + width - 32, y, false, 0.0f, 1.0f);
        this.drawRegion(frame[2], x, y + height - 32, false, 0.0f, 1.0f);
        this.drawRegion(frame[3], x + width - 32, y + height - 32, false, 0.0f, 1.0f);
        for (int b2 = 32; b2 <= height - 32; b2 += 32) {
            this.drawRegion(frame[4], x, y + b2, false, 0.0f, 1.0f);
        }
        for (int b2 = 32; b2 <= height - 32; b2 += 32) {
            this.drawRegion(frame[5], x + width - 32, y + b2, false, 0.0f, 1.0f);
        }
        for (int b2 = 32; b2 <= width - 32; b2 += 32) {
            this.drawRegion(frame[6], x + b2, y, false, 0.0f, 1.0f);
        }
        for (int b2 = 32; b2 <= width - 32; b2 += 32) {
            this.drawRegion(frame[7], x + b2, y + height - 32, false, 0.0f, 1.0f);
        }
    }
    
    void drawFont(final int type, final int X, final int Y, final String s, final boolean centered, final float scale, final Color col) {
        float curX = (float)X;
        final float padding = 0.0f * scale;
        final float spacing = 1.0f * scale;
        float total = 0.0f;
        float oX;
        float oY;
        if (centered) {
            total = AssetLoader.getStringWidth(s, scale, padding, spacing);
            oX = (float)Math.round(-total / 2.0f);
            oY = (float)Math.round(scale * -16.0f / 2.0f);
        }
        else {
            oX = 0.0f;
            oY = 0.0f;
        }
        final Color cur = this.batcher.getColor();
        this.batcher.setColor(col);
        char[] charArray;
        for (int length = (charArray = s.toCharArray()).length, i = 0; i < length; ++i) {
            final int ascii;
            final char c = (char)(ascii = charArray[i]);
            if (AssetLoader.fontWidth[ascii] > 0) {
                this.drawRegion(AssetLoader.font[type][ascii], Math.round(curX + padding + oX), Math.round(Y + oY), false, 0.0f, scale);
                curX += AssetLoader.fontWidth[ascii] * scale + padding * 2.0f + spacing;
            }
        }
        this.batcher.setColor(cur);
    }
    
    public void resize(final int width, final int height) {
    }
    
    public void show() {
    }
    
    public void hide() {
    }
    
    public void pause() {
    }
    
    public void resume() {
    }
    
    public void dispose() {
    }
}
