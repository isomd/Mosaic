package io.github.tml.mosaic.install.domian.info;

import io.github.tml.mosaic.cube.external.MExtensionPackage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 扩展包信息
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtensionPackageInfo {
    private String id;
    private String name;
    private String description;
    private String className;
    private Class<?> clazz;
    private String cubeId;
    private List<ExtensionPointInfo> extensionPoints = new ArrayList<>();

    public ExtensionPackageInfo(String cubeId, List<ExtensionPointInfo> extensionPoints, String description, String name, String id) {
        this.cubeId = cubeId;
        this.extensionPoints = extensionPoints;
        this.description = description;
        this.name = name;
        this.id = id;
    }

    public void addExtensionPoint(ExtensionPointInfo extensionPointInfo) {
        extensionPoints.add(extensionPointInfo);
    }

    public void setInfoByMExtensionPackage(MExtensionPackage pkgAnno) {
        this.setCubeId(pkgAnno.cubeId());
        this.setDescription(pkgAnno.description());
        this.setName(pkgAnno.name());
        this.setExtensionPoints(new ArrayList<>());
    }
}