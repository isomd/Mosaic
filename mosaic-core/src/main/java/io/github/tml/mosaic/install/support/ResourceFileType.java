package io.github.tml.mosaic.install.support;

/**
 * 资源文件类型
 */
public enum ResourceFileType {
    JAR("jar"), // jar包
    WAR("war"),
    CODE("code"); // 本地编码

    private String name;

    ResourceFileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ResourceFileType fromName(String name) {
        for (ResourceFileType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown resource file type: " + name);
    }
}
