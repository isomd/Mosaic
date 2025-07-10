package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import io.github.tml.mosaic.cube.constant.ModuleFileName;
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.domian.info.CubeListenerInfo;
import io.github.tml.mosaic.install.domian.info.ExtensionPackageInfo;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static io.github.tml.mosaic.cube.constant.CubeModelType.DEFAULT_TYPE;
import static io.github.tml.mosaic.cube.constant.CubeModelType.ANGLE_TYPE;
import static java.lang.reflect.Modifier.isAbstract;

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

        Map<String, CubeInfo> cubeInfoMap = new HashMap<>();
        Map<String, List<ExtensionPackageInfo>> extensionPackageInfoHashMap = new HashMap<>();
        Map<String, List<CubeListenerInfo>> cubeListenerInfoHashMap = new HashMap<>();

        for (Class<?> clazz : allClazz) {
            if (isValidCubeClass(clazz)) {
                CubeInfo cubeInfo = new CubeInfo();
                infoContext.addCubeInfo(cubeInfo);

                cubeInfo.setClazz(clazz);
                cubeInfo.setClassName(clazz.getName());

                cubeInfoMap.put(getRootPackageName(clazz, ModuleFileName.CUBE.getPackageName()), cubeInfo);

                if (AngelCube.class.isAssignableFrom(clazz)) {
                    cubeInfo.setModel(ANGLE_TYPE);
                } else {
                    cubeInfo.setModel(DEFAULT_TYPE);
                }

            } else if(isValidExtensionPackageApiClass(clazz)){

                ExtensionPackageInfo extensionPackageInfo = new ExtensionPackageInfo();
                extensionPackageInfo.setClazz(clazz);
                extensionPackageInfo.setClassName(clazz.getName());
                extensionPackageInfoHashMap.computeIfAbsent(
                        getRootPackageName(clazz, ModuleFileName.API.getPackageName()), (k)->new ArrayList<>()
                ).add(extensionPackageInfo);

            } else if (isValidCubeListener(clazz)) {
                CubeListenerInfo cubeListenerInfo = new CubeListenerInfo();
                cubeListenerInfo.setClazz(clazz);
                cubeListenerInfo.setClassName(clazz.getName());
                cubeListenerInfoHashMap.computeIfAbsent(
                        getRootPackageName(clazz, ModuleFileName.LISTENER.getPackageName()), (k)->new ArrayList<>()
                ).add(cubeListenerInfo);
            }
        }

        cubeInfoMap.forEach((packageName, cubeInfo)->{
            List<ExtensionPackageInfo> packageInfos = extensionPackageInfoHashMap.getOrDefault(packageName, Collections.emptyList());
            cubeInfo.setExtensionPackages(packageInfos);

            List<CubeListenerInfo> cubeListenerInfos = cubeListenerInfoHashMap.getOrDefault(packageName, Collections.emptyList());
            cubeInfo.setCubeListeners(cubeListenerInfos);
        });
    }


    private String getRootPackageName(Class<?> clazz, String lastPackageName) {
        return clazz.getPackage().getName().split("." + lastPackageName)[0];
    }

    /**
     * 验证是否是有效的Cube类
     */
    private boolean isValidCubeClass(Class<?> clazz) {
        // 必须实现Cube接口
        if (!MosaicCube.class.isAssignableFrom(clazz)) {
            return false;
        }

        // 不能是抽象类或接口
        return !clazz.isInterface() && !isAbstract(clazz.getModifiers());
    }

    /**
     * 验证是否是有效的拓展包类
     */
    private boolean isValidExtensionPackageApiClass(Class<?> clazz) {
        // 必须实现Cube接口
        if (!MosaicExtPackage.class.isAssignableFrom(clazz)) {
            return false;
        }

        // 不能是抽象类或接口
        return !clazz.isInterface() && !isAbstract(clazz.getModifiers());
    }

    /**
     * 验证是否是有效的Cube Listener
     */
    private boolean isValidCubeListener(Class<?> clazz) {
        if (!MosaicEventListener.class.isAssignableFrom(clazz)) {
            return false;
        }

        // 不能是抽象类或接口
        return !clazz.isInterface() && !isAbstract(clazz.getModifiers());
    }
}
