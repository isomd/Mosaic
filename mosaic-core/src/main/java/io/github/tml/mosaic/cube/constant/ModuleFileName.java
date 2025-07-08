package io.github.tml.mosaic.cube.constant;

public enum ModuleFileName {

    CUBE("cube"),
    API("api"),
    CONFIG("config"),
    VIEW("view"),
    LISTENER("listener");

    private String packageName;

    ModuleFileName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }
}
