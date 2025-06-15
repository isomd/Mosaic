package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.*;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.mosaic.cube.module.ModuleFileName;
import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 收集 Cube类和MosaicExtension信息
 */
@Slf4j
public class CubeModuleInfoCollector implements CommonInfoCollector {

    @Override
    public void collect(InfoContext infoContext) {
        List<Class<?>> allClazz = infoContext.getAllClazz();
        if(CollectionUtils.isEmpty(allClazz)){
            log.error("CubeModuleInfoCollector need class list, but it is empty");
            return;
        }

        Map<String, InfoContext.CubeInfo> cubeInfoMap = new HashMap<>();
        Map<String, List<InfoContext.ExtensionPackageInfo>> extensionPackageInfoHashMap = new HashMap<>();
        for (Class<?> clazz : allClazz) {
            if (isValidCubeClass(clazz)) {
                InfoContext.CubeInfo cubeInfo = new InfoContext.CubeInfo();
                infoContext.addCubeInfo(cubeInfo);

                cubeInfo.setClazz(clazz);
                cubeInfo.setClassName(clazz.getName());

                cubeInfoMap.put(getRootPackageName(clazz, ModuleFileName.CUBE.getPackageName()), cubeInfo);
            }else if(isValidExtensionPackageApiClass(clazz)){

                InfoContext.ExtensionPackageInfo extensionPackageInfo = new InfoContext.ExtensionPackageInfo();

                extensionPackageInfo.setClazz(clazz);
                extensionPackageInfo.setClassName(clazz.getName());

                extensionPackageInfoHashMap.computeIfAbsent(
                        getRootPackageName(clazz, ModuleFileName.API.getPackageName()), (k)->new ArrayList<>()
                ).add(extensionPackageInfo);
            }

        }

        cubeInfoMap.forEach((packageName, cubeInfo)->{
            List<InfoContext.ExtensionPackageInfo> packageInfos = extensionPackageInfoHashMap.getOrDefault(packageName, Collections.emptyList());
            cubeInfo.setExtensionPackages(packageInfos);
        });
    }


    private String getRootPackageName(Class<?> clazz, String lastPackageName) {
        return clazz.getPackage().getName().split("." + lastPackageName)[0];
    }
    /**
     * 验证是否是有效的Cube类
     */
    private boolean isValidCubeClass(Class<?> clazz) {
        // 1. 必须实现Cube接口
        if (!MosaicCube.class.isAssignableFrom(clazz)) {
            return false;
        }

        // 3. 不能是抽象类或接口
        if (clazz.isInterface() ||
                java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        return true;
    }

    /**
     * 验证是否是有效的拓展包类
     */
    private boolean isValidExtensionPackageApiClass(Class<?> clazz) {
        // 1. 必须实现Cube接口
        if (!MosaicExtPackage.class.isAssignableFrom(clazz)) {
            return false;
        }

        // 3. 不能是抽象类或接口
        if (clazz.isInterface() ||
                java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        return true;
    }
}
