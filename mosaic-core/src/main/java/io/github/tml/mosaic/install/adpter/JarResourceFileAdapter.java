package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.install.collector.AnnotationInfoCollector;
import io.github.tml.mosaic.install.collector.CubeModuleInfoCollector;
import io.github.tml.mosaic.install.collector.JarClassLoaderAllClassCollector;
import io.github.tml.mosaic.install.support.ResourceFileType;

import java.util.List;

/**
 * jar包资源文件适配器
 */
public class JarResourceFileAdapter extends AbstractResourceFileAdapter{

    @Override
    public ResourceFileType resourceFileType() {
        return ResourceFileType.JAR;
    }

    public JarResourceFileAdapter() {
        super(
                List.of(
                        new JarClassLoaderAllClassCollector(),
                        new CubeModuleInfoCollector(),
                        new AnnotationInfoCollector()
                ), new DefaultResourceLoader()
        );
    }
}
