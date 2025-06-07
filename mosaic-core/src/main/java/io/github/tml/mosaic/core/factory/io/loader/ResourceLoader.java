package io.github.tml.mosaic.core.factory.io.loader;

import io.github.tml.mosaic.core.factory.io.resource.Resource;

/**
 * 描述: 资源加载器
 * @author suifeng
 * 日期: 2025/5/29
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);
}
