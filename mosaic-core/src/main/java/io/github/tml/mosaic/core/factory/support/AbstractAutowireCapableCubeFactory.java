package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.*;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.definition.ExtensionPackageDefinition;
import io.github.tml.mosaic.core.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.core.factory.config.InstantiationStrategy;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 描述: 用于实例化Cube的类
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowireCapableCubeFactory extends AbstractCubeFactory {

    private final InstantiationStrategy instantiationStrategy = new DefaultInstantiationStrategy();

    @Override
    protected Cube createCube(GUID cubeId, CubeDefinition cubeDefinition, Object[] args) throws CubeException {
        Cube cube = null;
        try {
            cube = createCubeInstance(cubeDefinition, cubeId, args);
            // 给 Cube 填充属性
            applyPropertyValues(cube, cubeDefinition);
            // 执行 Cube 的初始化方法
            cube = initializeCube(cube, cubeDefinition);
        } catch (Exception e) {
            throw new CubeException("Instantiation of cube failed", e);
        }

        // 根据Cube模式进行不同的注册策略
        String model = cubeDefinition.getModel();
        if ("singleton".equals(model)) {
            // 单例模式：注册到单例池
            addSingleton(cubeId, cube);
            log.info("✓ singleton Cube register | CubeId: {}", cubeId);
        } else {
            // 多例模式：注册到管理器
            GUID instanceId = registerCube(cubeId, cube);
            log.info("✓ property Cube register | CubeId: {} | instanceId: {}", cubeId, instanceId);
        }

        return cube;
    }

    private Cube initializeCube(Cube cube, CubeDefinition cubeDefinition) {
        try {
            if (cube.init()) {
                log.info("✓ Cube init success | CubeId: {}, CubeName:{}", cube.getCubeId(), cube.getMetaData().getName());
                return cube;
            }else {
                throw new CubeException("Cube init failed, cubeId:" + cube.getCubeId());
            }
        }catch (Exception e){
            throw new CubeException("Cube init failed, error:" + e.getMessage());
        }
    }

    // 内部实例化方法
    protected Cube createCubeInstance(CubeDefinition cubeDefinition, GUID cubeId, Object[] args) {
        return instantiationStrategy.instantiate(cubeDefinition, cubeId, args);
    }

    /**
     * Cube 属性填充
     */
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
                        pkgDef.getName(), pkgDef.getDescription(), pkgDef.getVersion()
                );

                // 实例化扩展包
                ExtensionPackage extensionPackage = new ExtensionPackage(new DotNotationId(pkgDef.getId()));
                MosaicExtPackage<?> mosaicExtPackage = (MosaicExtPackage<?>) mosaicExtPkgClazz.getDeclaredConstructor().newInstance();
                mosaicExtPackage.initCube(cube.getMosaicCube());
                extensionPackage.setMosaicExtPackage(mosaicExtPackage);
                extensionPackage.setMetaData(metaData);

                // 注册扩展包到Cube元数据
                cube.addExtensionPackage(extensionPackage);

                // 注册扩展点
                for (ExtensionPointDefinition epd : pkgDef.getExtensionPoints()) {
                    ExtensionPoint extensionPoint = new ExtensionPoint(new DotNotationId(epd.getId()));
                    extensionPoint.setExtensionName(epd.getExtensionName());
                    extensionPoint.setDescription(epd.getDescription());
                    extensionPoint.setMethodName(epd.getMethodName());
                    extensionPoint.setParameterTypes(epd.getParameterTypes());
                    extensionPoint.setReturnType(epd.getReturnType());
                    // 注册扩展点到扩展包
                    extensionPackage.addExtensionPoint(extensionPoint);
                }
                log.info("extPackage init success | Cube: {} | extPackage: {} | extPointNum: {}", cube.getCubeId(), pkgDef.getName(), extensionPackage.getExtensionPoints().size());
            } catch (Exception e) {
                log.error("extPackage init fail | Cube: {} | extPackage: {} | error: {}",
                        cube.getCubeId(), pkgDef.getName(), e.getMessage());
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
        log.debug("填充Cube元数据 | ID: {} | 名称: {} | 版本: {}",
                cube.getCubeId(), metaData.getName(), metaData.getVersion());
    }
}
