package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.support.InfoContext;
import io.github.tml.mosaic.install.support.ResourceFileType;

/**
 * 资源文件适配器
 */
public interface ResourceFileAdapter {

    ResourceFileType resourceFileType();

    InfoContext adapter(Resource resource);

}
