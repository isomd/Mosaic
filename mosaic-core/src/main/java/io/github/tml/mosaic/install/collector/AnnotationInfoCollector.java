package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.cube.MCube;
import io.github.tml.mosaic.cube.MExtension;
import io.github.tml.mosaic.cube.MExtensionPackage;
import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * cube注解信息收集器
 * 收集：拓展包，cube metaInfo
 */
public class AnnotationInfoCollector implements CommonInfoCollector {

    @Override
    public void collect(InfoContext infoContext) {
        List<InfoContext.CubeInfo> cubeInfoList = infoContext.getCubeInfoList();
        for (InfoContext.CubeInfo cubeInfo : cubeInfoList) {
            scanCubeInfo(cubeInfo);
            scanExtensionPackages(cubeInfo);
        }
    }

    private void scanCubeInfo(InfoContext.CubeInfo cubeInfo){
        Class<?> clazz = cubeInfo.getClazz();
        if (clazz.isAnnotationPresent(MCube.class)) {
            MCube mCube = clazz.getAnnotation(MCube.class);
            cubeInfo.setInfoByMCube(mCube);
        }
    }

    private void scanExtensionPackages(InfoContext.CubeInfo cubeInfo) {

        List<InfoContext.ExtensionPackageInfo> extensionPackages = cubeInfo.getExtensionPackages();

        for (InfoContext.ExtensionPackageInfo extensionPackage : extensionPackages) {
            Class<?> clazz = extensionPackage.getClazz();
            if (!clazz.isAnnotationPresent(MExtensionPackage.class)) {
                continue;
            }
            MExtensionPackage pkgAnno = clazz.getAnnotation(MExtensionPackage.class);
            if (pkgAnno != null && pkgAnno.cubeId().equals(cubeInfo.getId())) {
                extensionPackage.setInfoByMExtensionPackage(pkgAnno);
                // 扫描扩展点
                scanExtensionPoints(clazz, extensionPackage);
            }
        }
    }

    private void scanExtensionPoints(Class<?> pkgClass, InfoContext.ExtensionPackageInfo packageInfo) {
        for (Method method : pkgClass.getDeclaredMethods()) {
            MExtension extension = method.getAnnotation(MExtension.class);
            if (extension != null) {
                InfoContext.ExtensionPointInfo epd = new InfoContext.ExtensionPointInfo();
                epd.setInfoByMExtensionPoint(extension);
                epd.setInfoByMethod(method);
                packageInfo.addExtensionPoint(epd);
            }
        }
    }
}
