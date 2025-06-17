package io.github.tml.mosaic.cube.factory.io.loader;

import io.github.tml.mosaic.cube.factory.io.resource.Resource;

/**
 * 描述: 资源加载器
 * @author suifeng
 * 日期: 2025/5/29
 */
public interface ResourceLoader {

    Resource getResource(String location);
}
