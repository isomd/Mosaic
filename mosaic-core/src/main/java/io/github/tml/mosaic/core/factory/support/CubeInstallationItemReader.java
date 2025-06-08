package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.context.json.InstallationConfig;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;

/**
 * 描述: Cube 安装配置项 读取接口
 * @author suifeng
 * 日期: 2025/6/7 
 */
public interface CubeInstallationItemReader {

    ResourceLoader getResourceLoader();
    InstallationConfig loadCubeInstallationItem(Resource resource) throws CubeException;
    InstallationConfig loadCubeInstallationItem(Resource... resources) throws CubeException;
    InstallationConfig loadCubeInstallationItem(String location) throws CubeException;
    InstallationConfig loadCubeInstallationItem(String... locations) throws CubeException;
}
