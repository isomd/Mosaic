package io.github.tml.mosaic.converter;

import io.github.tml.mosaic.cube.factory.definition.*;
import io.github.tml.mosaic.install.domian.info.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CubeDefinitionConverter {

    // CubeInfo -> CubeDefinition
    public static CubeDefinition convertCubeInfoToCubeDefinition(CubeInfo cubeInfo) {
        CubeDefinition cubeDef = new CubeDefinition(
                cubeInfo.getId(),
                cubeInfo.getName(),
                cubeInfo.getVersion(),
                cubeInfo.getDescription(),
                cubeInfo.getModel(),
                cubeInfo.getScope(),
                cubeInfo.getClassName(),
                cubeInfo.getClassLoader()
        );
        cubeDef.setConfigInfo(cubeInfo.getConfigInfo());

        // 处理扩展包
        for (ExtensionPackageInfo epInfo : cubeInfo.getExtensionPackages()) {
            ExtensionPackageDefinition epDef = new ExtensionPackageDefinition(
                    epInfo.getId(),
                    epInfo.getName(),
                    epInfo.getDescription(),
                    epInfo.getClassName(),
                    cubeInfo.getId()
            );

            // 处理扩展点
            for (ExtensionPointInfo pointInfo : epInfo.getExtensionPoints()) {
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

        // 处理监听器
        for (CubeListenerInfo cubeListener : cubeInfo.getCubeListeners()) {
            CubeListenerDefinition listenerDefinition = new CubeListenerDefinition();
            listenerDefinition.setClassName(cubeListener.getClassName());
            listenerDefinition.setName(cubeListener.getName());
            listenerDefinition.setClazz(cubeListener.getClazz());
            listenerDefinition.setCubeId(cubeListener.getCubeId());

            cubeDef.addCubeListener(listenerDefinition);
        }

        return cubeDef;
    }

    // List<CubeInfo> -> List<CubeDefinition>
    public static List<CubeDefinition> convertCubeInfoListToCubeDefinitionList(List<CubeInfo> cubeInfoList) {
        List<CubeDefinition> cubeDefList = new ArrayList<CubeDefinition>();
        for (CubeInfo cubeInfo : cubeInfoList) {
            CubeDefinition cubeDef = convertCubeInfoToCubeDefinition(cubeInfo);
            cubeDefList.add(cubeDef);
        }
        return cubeDefList;
    }
}