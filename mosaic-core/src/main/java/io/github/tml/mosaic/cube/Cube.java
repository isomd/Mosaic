package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.core.tools.param.ConfigurableEntity;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.api.CubeApi;
import io.github.tml.mosaic.cube.external.MosaicCube;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 方块抽象基类 - 所有插件必须继承此类
 */
@Slf4j
public class Cube extends ConfigurableEntity implements CubeApi {

    @Getter
    private MetaData metaData;

    @Getter
    @Setter
    private MosaicCube mosaicCube;

    protected volatile boolean initialized = false;

    public Cube(GUID id) {
        super(id);
        this.metaData = new MetaData();
    }

    @Override
    public boolean init() {
        return mosaicCube.init();
    }

    @Override
    public boolean destroy() {
       return mosaicCube.destroy();
    }

    @Override
    public String cubeId() {
        return Optional.ofNullable(this.id).map(GUID::toString).orElse(null);
    }

    public GUID getCubeId() {
        return getId();
    }

    @Data
    public static class MetaData {

        private String name;
        private String version;
        private String description;
        private String model;

        // 扩展包元数据
        private final List<ExtensionPackage> extensionPackages = new CopyOnWriteArrayList<>();
        private final Map<GUID, ExtensionPackage> extensionPackageMap = new ConcurrentHashMap<>();
    }

    public void addExtensionPackage(ExtensionPackage extensionPackage) {
        getMetaData().extensionPackages.add(extensionPackage);
        getMetaData().extensionPackageMap.put(extensionPackage.getId(), extensionPackage);
    }

    public ExtensionPackage findExPackage(GUID packageId) {
        return getMetaData().extensionPackageMap.get(packageId);
    }
}