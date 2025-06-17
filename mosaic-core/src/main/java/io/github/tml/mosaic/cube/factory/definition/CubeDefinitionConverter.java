package io.github.tml.mosaic.cube.factory.definition;

import io.github.tml.mosaic.install.support.info.InfoContext;
import io.github.tml.mosaic.install.support.info.PointsResultInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CubeDefinitionConverter {

    public static List<CubeDefinition> convertToCubeDefinitions(InfoContext infoContext) {
        List<CubeDefinition> cubeDefinitions = new ArrayList<>();
        if (infoContext == null) {
            log.warn("infoContext is null");
            return cubeDefinitions;
        }
        for (InfoContext.CubeInfo cubeInfo : infoContext.getCubeInfoList()) {
            // 构建CubeDefinition
            CubeDefinition cubeDef = new CubeDefinition(
                    cubeInfo.getId(),
                    cubeInfo.getName(),
                    cubeInfo.getVersion(),
                    cubeInfo.getDescription(),
                    cubeInfo.getModel(),
                    cubeInfo.getClassName(),
                    infoContext.getClassLoader()
            );
            cubeDef.setCubeConfigInfo(cubeInfo.getCubeConfigInfo());
            
            // 处理扩展包
            for (InfoContext.ExtensionPackageInfo epInfo : cubeInfo.getExtensionPackages()) {
                ExtensionPackageDefinition epDef = new ExtensionPackageDefinition(
                        epInfo.getId(),
                        epInfo.getName(),
                        epInfo.getDescription(),
                        epInfo.getClassName(),
                        cubeInfo.getId()
                );
                
                // 处理扩展点
                for (InfoContext.ExtensionPointInfo pointInfo : epInfo.getExtensionPoints()) {
                    ExtensionPointDefinition epoint = new ExtensionPointDefinition(
                            pointInfo.getId(),
                            pointInfo.getMethodName(),
                            pointInfo.getExtensionName(),
                            pointInfo.getPriority(),
                            pointInfo.getDescription(),
                            pointInfo.isAsyncFlag(),
                            pointInfo.getReturnType(),
                            pointInfo.getParameterTypes()
                    );
                    epDef.addExtensionPoint(epoint);
                    PointsResultInfo pointsResultInfo = pointInfo.getPointsResultInfo();
                    if(pointsResultInfo != null){
                        epoint.setPointResultDefinitions(PointResultDefinition.convertByInfoContext(pointsResultInfo));
                    }
                }
                cubeDef.addExtensionPackage(epDef);
            }
            cubeDefinitions.add(cubeDef);
        }
        return cubeDefinitions;
    }
}