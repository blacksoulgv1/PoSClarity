package com.gerardgv.posclarity.update;

public class AppVersion {
    
    public static final String DEFAULT_VERSION = "1.0.1";
    
    private AppVersion(){
        
    }
    
    public static String getCurrentVersion() {

        Package pkg = AppVersion.class.getPackage();

        String version = pkg.getImplementationVersion();

        if (version == null || version.isBlank()) {
            return DEFAULT_VERSION;
        }

        return version;
    }
}
