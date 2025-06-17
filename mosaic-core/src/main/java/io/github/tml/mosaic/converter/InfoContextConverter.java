package io.github.tml.mosaic.converter;

import io.github.tml.mosaic.install.domian.InfoContext;
import io.github.tml.mosaic.install.domian.info.CubeInfo;

import java.util.ArrayList;
import java.util.List;

public class InfoContextConverter {

    //  List<InfoContext> -> List<CubeInfo>
    public static List<CubeInfo> convertInfoContextsToCubeInfoList(List<InfoContext> infoContexts) {
        List<CubeInfo> cubeInfos = new ArrayList<>();
        for (InfoContext infoContext : infoContexts) {
            cubeInfos.addAll(infoContext.getCubeInfoList());
        }
        return cubeInfos;
    }
}
