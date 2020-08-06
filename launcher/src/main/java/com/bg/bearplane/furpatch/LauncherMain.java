// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import java.awt.Dimension;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LauncherMain
{
    public static void main(final String[] args) {
        final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Odyssey Beta Patcher";
        cfg.width = 640;
        cfg.height = 480;

		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		cfg.x = dimension.width/2 - cfg.width/2;
		cfg.y = dimension.height/2 - cfg.height/2;
        cfg.resizable = false;
        cfg.addIcon("assets/bearnecessities/tod.png", Files.FileType.Local);
        final FurPatch sp = new FurPatch();
        new LwjglApplication((ApplicationListener)sp, cfg);
    }
}
