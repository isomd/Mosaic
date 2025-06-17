package io.github.tml.mosaic.install.installer.core;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.install.domian.InfoContext;

import java.util.List;

/**
 * 描述: 从configLocations加载出来List InfoContext
 * @author suifeng
 * 日期: 2025/6/7 
 */
public interface InfoContextInstaller {

    List<InfoContext> installCubeInfoContext() throws CubeException;

    List<InfoContext> installCubeInfoContext(String[] configLocations) throws CubeException;
}
