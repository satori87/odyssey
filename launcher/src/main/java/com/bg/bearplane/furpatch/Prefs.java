// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

public class Prefs
{
    public static final int WINDOWWIDTH = 640;
    public static final int WINDOWHEIGHT = 480;
    public static final String WINDOWNAME = "Odyssey Beta Patcher";
    public static String hostName;
    public static final int HTTPTIMEOUT = 1000;
    
    static {
        Prefs.hostName = "patch.bearable.games";
    }
}
