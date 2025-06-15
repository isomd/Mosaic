package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.install.adpter.core.AbstractResourceFileAdapter;
import io.github.tml.mosaic.install.collector.AnnotationInfoCollector;
import io.github.tml.mosaic.install.collector.CubeIdInfoCollector;
import io.github.tml.mosaic.install.collector.CubeModuleInfoCollector;
import io.github.tml.mosaic.install.collector.LocalProjectClassCollector;
import io.github.tml.mosaic.install.collector.core.InfoCollector;
import io.github.tml.mosaic.install.support.ResourceFileType;

import java.util.List;

/**
 * 本地代码资源收集器
 */
public class CodeResourceFileAdapter extends AbstractResourceFileAdapter {

    public CodeResourceFileAdapter() {
        super(createCollectorChain(), new DefaultResourceLoader());
    }

    private static List<InfoCollector> createCollectorChain() {
        return List.of(
                new LocalProjectClassCollector(),
                new CubeModuleInfoCollector(),
                new CubeIdInfoCollector(),
                new AnnotationInfoCollector()
        );
    }

    @Override
    public ResourceFileType resourceFileType() {
        return ResourceFileType.CODE;
    }
}
