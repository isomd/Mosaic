package io.github.tml.mosaic.install.support;

import io.github.tml.mosaic.core.factory.definition.ExtensionPackageDefinition;
import io.github.tml.mosaic.core.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InfoContext {

    private Resource resource;

    /**
     * 来源信息，用于描述当前信息的来源
     */
    private ResourceFileType resourceType;

    /**
     * 外部来源插件中的所有java类
     */
    private List<Class<?>> allClazz = new ArrayList<>();

    private List<CubeInfo> cubeInfoList = new ArrayList<>();

    /**
     * 类加载器
     */
    private transient ClassLoader classLoader;

    public void addCubeInfo(CubeInfo cubeInfo){
        this.cubeInfoList.add(cubeInfo);
    }
    // 方块信息
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CubeInfo{
        private GUID id;
        private String name;
        private String version;
        private String description;
        private String model;
        private String className;
        private Class<?> clazz;
        private List<ExtensionPackageInfo> extensionPackages = new ArrayList<>();

        public void addExtensionPackage(ExtensionPackageInfo extensionPackageInfo){
            extensionPackages.add(extensionPackageInfo);
        }
    }

    // 扩展包信息
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ExtensionPackageInfo{
        private String id;
        private String name;
        private String description;
        private String version;
        private String className;
        private Class<?> clazz;
        private String cubeId;
        private List<ExtensionPointInfo> extensionPoints = new ArrayList<>();

        public ExtensionPackageInfo(String cubeId, List<ExtensionPointInfo> extensionPoints, String version, String description, String name, String id) {
            this.cubeId = cubeId;
            this.extensionPoints = extensionPoints;
            this.version = version;
            this.description = description;
            this.name = name;
            this.id = id;
        }

        public void addExtensionPoint(ExtensionPointInfo extensionPointInfo){
            extensionPoints.add(extensionPointInfo);
        }
    }



    // 扩展点信息
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ExtensionPointInfo{
        private String id;
        private String methodName;
        private String extensionName;
        private int priority;
        private String description;
        private boolean asyncFlag;
        private Class<?> returnType;
        private Class<?>[] parameterTypes;
    }
}
