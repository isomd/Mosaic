package io.github.tml.mosaic.install.adpter.core;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.context.json.InstallationItem;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.collector.core.InfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 资源收集适配器
 */
@Slf4j
public abstract class AbstractResourceFileAdapter implements ResourceFileAdapter {

    private final ResourceLoader resourceLoader;

    private List<InfoCollector> collectorList;

    public AbstractResourceFileAdapter(List<InfoCollector> collectorList, ResourceLoader resourceLoader) {
        this.collectorList = collectorList;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public InfoContext adapter(InstallationItem item) {
        InfoContext infoContext = new InfoContext();

        Resource resource = null;
        if (item.getLocation() != null && !item.getLocation().isEmpty()) {
            resource = getResourceLoader().getResource(item.getLocation());
        }
        if (item.getPackageName() != null && !item.getPackageName().isEmpty()) {
            infoContext.setPackageName(item.getPackageName());
            if (resource == null) {
                resource = getResourceLoader().getResource("classpath:");
            }
        }

        Optional.ofNullable(resource).orElseThrow(()->new CubeException("resource not exist"));

        infoContext.setResource(resource);
        infoContext.setResourceType(resourceFileType());

        try {
            Optional.ofNullable(collectorList)
                    .orElse(Collections.emptyList())
                    .forEach(collector -> collector.collect(infoContext));
        } catch (Exception e){
            log.error("adapter collect info error :{}",e.getMessage());
            return null;
        }

        return infoContext;
    }
}
