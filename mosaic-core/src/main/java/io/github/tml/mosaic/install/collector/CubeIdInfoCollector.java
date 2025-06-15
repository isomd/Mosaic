package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;
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
        List<InfoContext.CubeInfo> cubeInfoList = infoContext.getCubeInfoList();
        if(CollectionUtils.isEmpty(cubeInfoList)){
            log.error("CubeIdInfoCollector need cubeInfoList, but it is empty");
            return;
        }
        for (InfoContext.CubeInfo cubeInfo : cubeInfoList) {
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
            List<InfoContext.ExtensionPackageInfo> extensionPackages = cubeInfo.getExtensionPackages();
            if(CollectionUtils.isEmpty(extensionPackages)){
                continue;
            }
            for (InfoContext.ExtensionPackageInfo extensionPackage : extensionPackages) {
                if(Objects.nonNull(extensionPackage.getClazz())){
                    try {
                        MosaicExtPackage<?> mosaicExtension = (MosaicExtPackage) extensionPackage.getClazz().getDeclaredConstructor().newInstance();
                        extensionPackage.setId(mosaicExtension.extPackageId());
                    }catch (Exception e){
                        log.error("newInstance extension error, extension class:{}",extensionPackage.getClazz().getName(),e);
                    }
                }
            }
        }
    }
}
