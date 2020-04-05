// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.InputProcessor;

public class Input implements InputProcessor
{
    public static GameScreen screen;
    public static List<Integer> keyPress;
    public static boolean[] keyDown;
    public static boolean mouseDown;
    public static boolean wasMouseJustClicked;
    public static boolean wasMouseJustReleased;
    public static int mouseDownX;
    public static int mouseDownY;
    public static int mouseUpX;
    public static int mouseUpY;
    public static int mouseX;
    public static int mouseY;
    
    static {
        Input.mouseDown = false;
        Input.wasMouseJustClicked = false;
        Input.wasMouseJustReleased = false;
        Input.mouseDownX = 0;
        Input.mouseDownY = 0;
        Input.mouseUpX = 0;
        Input.mouseUpY = 0;
        Input.mouseX = 0;
        Input.mouseY = 0;
    }
    
    public Input(final GameScreen screen) {
        Input.screen = screen;
        Input.keyPress = new ArrayList<Integer>();
        Input.keyDown = new boolean[256];
        for (int i = 0; i < 256; ++i) {
            Input.keyDown[i] = false;
        }
    }
    
    public boolean keyDown(final int keycode) {
        Input.keyDown[keycode] = true;
        Input.keyPress.add(keycode);
        return true;
    }
    
    public boolean keyUp(final int keycode) {
        Input.keyDown[keycode] = false;
        return true;
    }
    
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        Input.mouseDown = true;
        Input.wasMouseJustClicked = true;
        Input.mouseDownX = screenX;
        Input.mouseDownY = screenY;
        return true;
    }
    
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        Input.wasMouseJustReleased = true;
        Input.mouseUpX = screenX;
        Input.mouseUpY = screenY;
        Input.mouseDown = false;
        return true;
    }
    
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        Input.mouseX = screenX;
        Input.mouseY = screenY;
        return true;
    }
    
    public boolean mouseMoved(final int screenX, final int screenY) {
        Input.mouseX = screenX;
        Input.mouseY = screenY;
        return true;
    }
    
    public boolean scrolled(final int amount) {
        return false;
    }
    
    public boolean keyTyped(final char character) {
        return false;
    }
}
