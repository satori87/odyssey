// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LauncherMain
{
    public static void main(final String[] args) {
        final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Odyssey: Dreams of Yore Patcher";
        cfg.width = 640;
        cfg.height = 480;
        cfg.resizable = false;
        cfg.addIcon("assets/bearnecessities/tod.png", Files.FileType.Local);
        final FurPatch sp = new FurPatch();
        new LwjglApplication((ApplicationListener)sp, cfg);
    }
}
