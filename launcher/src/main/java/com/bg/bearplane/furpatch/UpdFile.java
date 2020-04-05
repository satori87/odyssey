// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

public class UpdFile
{
    public String name;
    public String version;
    public long size;
    
    public UpdFile() {
        this.name = "";
        this.version = "";
        this.size = 0L;
    }
    
    public UpdFile(final String name, final String version, final long size) {
        this.name = "";
        this.version = "";
        this.size = 0L;
        this.name = name;
        this.version = version;
        this.size = size;
    }
}
