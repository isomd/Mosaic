package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.install.support.ResourceFileType;

import java.util.List;

/**
 * 本地代码资源收集器
 */
public class CodeResourceFileAdapter extends AbstractResourceFileAdapter{

    public CodeResourceFileAdapter() {
        super(List.of(), new DefaultResourceLoader());
    }

    @Override
    public ResourceFileType resourceFileType() {
        return ResourceFileType.CODE;
    }
}
