package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.definition.ExtensionPackageDefinition;
import io.github.tml.mosaic.core.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.MCube;
import io.github.tml.mosaic.cube.MExtension;
import io.github.tml.mosaic.cube.MExtensionPackage;
import io.github.tml.mosaic.install.support.InfoContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * cube注解信息收集器
 * 收集：拓展包，cube metaInfo
 */
public class AnnotationInfoCollector implements CommonInfoCollector{

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
        MCube mCube = clazz.getAnnotation(MCube.class);
        String id = mCube.value();
        String name = mCube.name().isEmpty() ? clazz.getSimpleName() : mCube.name();

        cubeInfo.setId(new GUUID(id));
        cubeInfo.setName(name);
        cubeInfo.setVersion(mCube.version());
        cubeInfo.setDescription(mCube.description());
        cubeInfo.setModel(mCube.model());
    }

    private void scanExtensionPackages(InfoContext.CubeInfo cubeInfo) {

        List<InfoContext.ExtensionPackageInfo> extensionPackages = cubeInfo.getExtensionPackages();

        for (InfoContext.ExtensionPackageInfo extensionPackage : extensionPackages) {
            Class<?> clazz = extensionPackage.getClazz();
            MExtensionPackage pkgAnno = clazz.getAnnotation(MExtensionPackage.class);
            if (pkgAnno != null && pkgAnno.cubeId().equals(cubeInfo.getId().toString())) {
                // 创建扩展包定义
                InfoContext.ExtensionPackageInfo pkgDef = new InfoContext.ExtensionPackageInfo(
                        pkgAnno.cubeId(),
                        new ArrayList<>(),
                        pkgAnno.version(),
                        pkgAnno.description(),
                        pkgAnno.name(),
                        pkgAnno.value()
                );

                // 扫描扩展点
                scanExtensionPoints(clazz, pkgDef);
            }
        }
    }

    private void scanExtensionPoints(Class<?> pkgClass, InfoContext.ExtensionPackageInfo packageInfo) {
        for (Method method : pkgClass.getDeclaredMethods()) {
            MExtension extension = method.getAnnotation(MExtension.class);
            if (extension != null) {
                InfoContext.ExtensionPointInfo epd = new InfoContext.ExtensionPointInfo(
                        extension.value(),
                        method.getName(),
                        extension.name(),
                        extension.priority(),
                        extension.description(),
                        false,
                        method.getReturnType(),
                        method.getParameterTypes()
                );
                packageInfo.addExtensionPoint(epd);
            }
        }
    }
}
