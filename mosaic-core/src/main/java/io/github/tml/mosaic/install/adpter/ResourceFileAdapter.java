package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.support.InfoContext;
import io.github.tml.mosaic.install.support.ResourceFileType;

/**
 * 资源文件适配器
 */
public interface ResourceFileAdapter {

    /**
     * 获取资源解析器
     */
    ResourceLoader getResourceLoader();

    ResourceFileType resourceFileType();

    InfoContext adapter(Resource resource);

    InfoContext adapter(String location);
}
