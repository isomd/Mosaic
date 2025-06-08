package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 描述: 扩展包抽象基类
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class ExtensionPackage<T extends Cube> extends UniqueEntity {

    protected T cube; // 关联的Cube实例

    @Getter
    private MetaData exPackageMetaData;


    public ExtensionPackage(T cube, GUID guid) {
        super(guid);
        this.cube = cube;
    }

    public void addExtensionPoint(ExtensionPoint extensionPoint) {
        this.exPackageMetaData.extensionPoints.add(extensionPoint);
        this.exPackageMetaData.extensionPointMap.put(extensionPoint.getExtensionId(), extensionPoint);
    }

    public ExtensionPoint findExPoint(GUID extensionPointId) {
        return this.exPackageMetaData.extensionPointMap.get(extensionPointId);
    }


    @AllArgsConstructor
    public static class MetaData{
        private final String name;
        private final String description;
        private final String version;

        // 扩展点元数据
        private final List<ExtensionPoint> extensionPoints = new CopyOnWriteArrayList<>();
        private final Map<GUID, ExtensionPoint> extensionPointMap = new ConcurrentHashMap<>();
    }

    public void setMetaData(MetaData metaData) {
        if(this.exPackageMetaData == null){
            this.exPackageMetaData = metaData;
        }
    }
}