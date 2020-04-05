// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;

public class Button
{
    GameScreen screen;
    public int id;
    public boolean dialog;
    public boolean sel;
    public boolean disabled;
    public int x;
    public int y;
    public boolean click;
    public int width;
    public int height;
    public float fontSize;
    public boolean toggle;
    public boolean toggled;
    public boolean dontRepeat;
    public int interval;
    long stamp;
    public String text;
    
    public Button(final GameScreen screen, final int id, final int x, final int y, final int width, final int height, final String text, final boolean toggle) {
        this.id = 0;
        this.dialog = false;
        this.sel = false;
        this.disabled = false;
        this.x = 0;
        this.y = 0;
        this.click = false;
        this.width = 32;
        this.height = 32;
        this.fontSize = 1.3333334f;
        this.toggle = false;
        this.toggled = false;
        this.dontRepeat = false;
        this.interval = 0;
        this.stamp = 0L;
        this.text = "button";
        this.screen = screen;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.toggle = toggle;
        this.fontSize = height / 24.0f;
    }
    
    public Button(final GameScreen screen, final int id, final int x, final int y, final int width, final int height, final String text) {
        this(screen, id, x, y, width, height, text, false);
    }
    
    public static boolean inCenteredBox(final int x, final int y, final int centerX, final int centerY, final int width, final int height) {
        final int topY = centerY - height / 2;
        final int bottomY = centerY + height / 2;
        final int leftX = centerX - width / 2;
        final int rightX = centerX + width / 2;
        return x > leftX && x < rightX && y > topY && y < bottomY;
    }
    
    public void update(final long tick) {
        try {
            final int mX = Input.mouseX;
            final int mY = Input.mouseY;
            if (this.disabled) {
                return;
            }
            if (Input.mouseDown) {
                if (inCenteredBox(mX, mY, this.x, this.y, this.width, this.height)) {
                    if (Input.wasMouseJustClicked) {
                        Input.wasMouseJustClicked = false;
                        this.click();
                        this.stamp = tick + 500L;
                    }
                    else if (tick > this.stamp && this.interval > 0) {
                        this.click();
                    }
                }
                else {
                    this.click = false;
                }
            }
            else {
                this.click = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    void click() {
        try {
            if (!this.toggle) {
                this.click = true;
            }
            else {
                this.toggled = !this.toggled;
            }
            this.screen.patch.buttonPressed(this.id);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public void render() {
        try {
            this.x -= this.width / 2;
            this.y -= this.height / 2;
            final TextureRegion[][] button = AssetLoader.button;
            int p = 0;
            if (this.click || this.sel || this.disabled || (this.toggle && this.toggled)) {
                p = 1;
            }
            else {
                p = 0;
            }
            for (int a = 8; a < this.height - 8; a += 8) {
                this.screen.batcher.draw(AssetLoader.bg[(p == 1) ? 3 : 4], (float)(this.x + 4), (float)(this.y + 4), 0, 0, this.width - 8, this.height - 8);
            }
            this.screen.drawRegion(button[p][0], this.x, this.y, false, 0.0f, 1.0f);
            this.screen.drawRegion(button[p][1], this.x + this.width - 8, this.y, false, 0.0f, 1.0f);
            this.screen.drawRegion(button[p][2], this.x, this.y + this.height - 8, false, 0.0f, 1.0f);
            this.screen.drawRegion(button[p][3], this.x + this.width - 8, this.y + this.height - 8, false, 0.0f, 1.0f);
            for (int b = 8; b < this.height - 8; b += 8) {
                this.screen.drawRegion(button[p][4], this.x, this.y + b, false, 0.0f, 1.0f);
            }
            for (int b = 8; b < this.height - 8; b += 8) {
                this.screen.drawRegion(button[p][5], this.x + this.width - 8, this.y + b, false, 0.0f, 1.0f);
            }
            for (int b = 8; b < this.width - 8; b += 8) {
                this.screen.drawRegion(button[p][6], this.x + b, this.y, false, 0.0f, 1.0f);
            }
            for (int b = 8; b < this.width - 8; b += 8) {
                this.screen.drawRegion(button[p][7], this.x + b, this.y + this.height - 8, false, 0.0f, 1.0f);
            }
            this.x += this.width / 2;
            this.y += this.height / 2;
            Color c = Color.WHITE;
            if (this.disabled) {
                c = Color.GRAY;
            }
            this.screen.drawFont(0, this.x, this.y + 1, this.text, true, this.fontSize, c);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
