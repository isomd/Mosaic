package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import io.github.tml.mosaic.cube.api.ExtensionPackageApi;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 描述: 扩展包抽象基类
 * @author suifeng
 * 日期: 2025/6/7
 */
@Getter
public class ExtensionPackage extends UniqueEntity implements ExtensionPackageApi {

    // 关联的Cube实例
    private MetaData exPackageMetaData;

    @Setter
    private MosaicExtPackage<?> mosaicExtPackage;

    public ExtensionPackage(GUID guid) {
        super(guid);
    }

    public void addExtensionPoint(ExtensionPoint extensionPoint) {
        this.exPackageMetaData.extensionPoints.add(extensionPoint);
        this.exPackageMetaData.extensionPointMap.put(extensionPoint.getExtensionId(), extensionPoint);
    }

    public List<ExtensionPoint> getExtensionPoints(){
        return this.exPackageMetaData.extensionPoints;
    }

    public ExtensionPoint findExPoint(GUID extensionPointId) {
        return this.exPackageMetaData.extensionPointMap.get(extensionPointId);
    }

    @Override
    public String extPackageId() {
        return Optional.ofNullable(this.getId()).map(GUID::toString).orElse("");
    }

    @Override
    public void initCube(MosaicCube mosaicCube) {
        this.mosaicExtPackage.initCube(mosaicCube);
    }


    @AllArgsConstructor
    public static class MetaData{
        private final String name;
        private final String description;

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