package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.cube.factory.definition.PointResultDefinition;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.cube.*;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.definition.ExtensionPackageDefinition;
import io.github.tml.mosaic.cube.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;

/**
 * 描述: 第二步：用于实例化后属性填充
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowirePropertyCubeFactory extends AbstractAutowireCapableCubeFactory {

    /**
     * Cube 属性填充
     */
    @Override
    protected void applyPropertyValues(Cube cube, CubeDefinition cubeDefinition) {
        // 填充Cube核心元数据
        populateCubeMetadata(cube, cubeDefinition);
        // 实例化并注入扩展包
        populateExtensionPackages(cube, cubeDefinition);
    }

    private void populateExtensionPackages(Cube cube, CubeDefinition cubeDefinition) {
        for (ExtensionPackageDefinition pkgDef : cubeDefinition.getExtensionPackages()) {
            try {
                // 加载扩展包类
                Class<?> mosaicExtPkgClazz = cubeDefinition.getClassLoader().loadClass(pkgDef.getClassName());

                // 创建扩展包元数据对象
                ExtensionPackage.MetaData metaData = new ExtensionPackage.MetaData(
                        pkgDef.getName(), pkgDef.getDescription()
                );

                // 实例化扩展包
                ExtensionPackage extensionPackage = new ExtensionPackage(new DotNotationId(pkgDef.getId()));
                MosaicExtPackage<?> mosaicExtPackage = (MosaicExtPackage<?>) mosaicExtPkgClazz.getDeclaredConstructor().newInstance();
                mosaicExtPackage.initCube(cube.getMosaicCube());
                extensionPackage.setMosaicExtPackage(mosaicExtPackage);
                extensionPackage.setMetaData(metaData);

                // 注册扩展包到Cube元数据
                cube.addExtensionPackage(extensionPackage);
                List<ExtensionPointDefinition> extensionPoints = pkgDef.getExtensionPoints();
                if(CollectionUtils.isEmpty(extensionPoints)){
                    return;
                }
                // 注册扩展点
                for (ExtensionPointDefinition epd : extensionPoints) {
                    ExtensionPoint extensionPoint = ExtensionPoint.convertByDefinition(epd);

                    // 注册出参说明
                    ExtPointResult extPointResult = new ExtPointResult();
                    extensionPoint.setReturnResult(extPointResult);

                    PointResultDefinition pointResultDefinitions = epd.getPointResultDefinitions();
                    if(pointResultDefinitions != null){
                        for (PointResultDefinition.PointResultItemDefinition itemDefinition : pointResultDefinitions.getPointsResultInfoList()) {
                            ExtPointResult.ExtPointResultItem item = ExtPointResult.ExtPointResultItem.convertByDefinition(itemDefinition);
                            extPointResult.addResultItem(item);
                        }
                    }
                    // 注册扩展点到扩展包
                    extensionPackage.addExtensionPoint(extensionPoint);
                }
                log.info("extPackage init success | Cube: {} | extPackage: {} | extPointNum: {}", cube.getCubeId(), pkgDef.getName(), extensionPackage.getExtensionPoints().size());
            } catch (Exception e) {
                log.error("extPackage init fail | Cube: {} | extPackage: {} | error: {}", cube.getCubeId(), pkgDef.getName(), e.getMessage());
            }
        }
    }

    /**
     * 填充Cube核心元数据
     */
    private void populateCubeMetadata(Cube cube, CubeDefinition cubeDefinition) {
        Cube.MetaData metaData = cube.getMetaData();
        // 设置基础元数据
        metaData.setName(cubeDefinition.getName());
        metaData.setVersion(cubeDefinition.getVersion());
        metaData.setDescription(cubeDefinition.getDescription());
        metaData.setModel(cubeDefinition.getModel());
        // 日志记录元数据填充
        log.debug("填充Cube元数据 | ID: {} | 名称: {} | 版本: {}", cube.getCubeId(), metaData.getName(), metaData.getVersion());
    }
}
