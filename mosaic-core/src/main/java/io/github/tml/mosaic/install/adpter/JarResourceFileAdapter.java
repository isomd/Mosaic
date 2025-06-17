package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.cube.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.cube.factory.io.resource.Resource;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.install.adpter.core.AbstractResourceFileAdapter;
import io.github.tml.mosaic.install.collector.*;
import io.github.tml.mosaic.install.support.info.InfoContext;
import io.github.tml.mosaic.install.support.ResourceFileType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * jar包资源文件适配器
 */
@Slf4j
public class JarResourceFileAdapter extends AbstractResourceFileAdapter {

    @Override
    public ResourceFileType resourceFileType() {
        return ResourceFileType.JAR;
    }

    public JarResourceFileAdapter() {
        super(
                List.of(
                        new JarClassLoaderAllClassCollector(),
                        new CubeModuleInfoCollector(),
                        new CubeIdInfoCollector(),
                        new CubeConfigInfoCollector(),
                        new AnnotationInfoCollector()
                ), new DefaultResourceLoader()
        );
    }

    @Override
    protected InfoContext buildInfoContext(InstallationItem item) {
        InfoContext infoContext = super.buildInfoContext(item);
        Resource resource = null;
        if (item.getLocation() != null && !item.getLocation().isEmpty()) {
            resource = getResourceLoader().getResource(item.getLocation());
        }
        infoContext.setResource(resource);
        return infoContext;
    }
}
