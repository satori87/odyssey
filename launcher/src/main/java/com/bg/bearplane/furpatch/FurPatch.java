// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

public class FurPatch extends Game
{
    GameScreen screen;
    
    public void create() {
        AssetLoader.load(true);
        this.setScreen((Screen)(this.screen = new GameScreen()));
    }
    
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}
