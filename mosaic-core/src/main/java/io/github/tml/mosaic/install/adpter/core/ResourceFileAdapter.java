package io.github.tml.mosaic.install.adpter.core;

import io.github.tml.mosaic.install.domian.install.InstallationItem;
import io.github.tml.mosaic.cube.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.install.domian.InfoContext;
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

    InfoContext adapter(InstallationItem item);
}
