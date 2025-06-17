package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.domian.info.ExtensionPackageInfo;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 注解id收集器
 */
@Slf4j
public class CubeIdInfoCollector implements CommonInfoCollector {
    @Override
    public void collect(InfoContext infoContext) {
        List<CubeInfo> cubeInfoList = infoContext.getCubeInfoList();
        if(CollectionUtils.isEmpty(cubeInfoList)){
            log.error("CubeIdInfoCollector need cubeInfoList, but it is empty");
            return;
        }
        for (CubeInfo cubeInfo : cubeInfoList) {
            Class<?> clazz = cubeInfo.getClazz();
            if(Objects.nonNull(clazz)){
                try {
                    MosaicCube mosaicCube = (MosaicCube) clazz.getDeclaredConstructor().newInstance();
                    cubeInfo.setId(mosaicCube.cubeId());
                }catch (Exception e){
                    log.error("newInstance cube error, cube class:{}",clazz.getName(),e);
                    continue;
                }
            }
            List<ExtensionPackageInfo> extensionPackages = cubeInfo.getExtensionPackages();
            if(CollectionUtils.isEmpty(extensionPackages)){
                continue;
            }
            for (ExtensionPackageInfo extensionPackage : extensionPackages) {
                if(Objects.nonNull(extensionPackage.getClazz())){
                    try {
                        MosaicExtPackage<?> mosaicExtension = (MosaicExtPackage) extensionPackage.getClazz().getDeclaredConstructor().newInstance();
                        extensionPackage.setId(mosaicExtension.extPackageId());
                    }catch (Exception e){
                        log.error("newInstance extension error, extension class:{}",extensionPackage.getClazz().getName(),e);
                    }
                }
            }


            // TODO 设置类加载器
            cubeInfo.setClassLoader(infoContext.getClassLoader());
        }
    }
}
