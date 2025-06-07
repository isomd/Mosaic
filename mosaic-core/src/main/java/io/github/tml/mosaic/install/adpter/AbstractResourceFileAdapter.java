package io.github.tml.mosaic.install.adpter;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.collector.CommonInfoCollector;
import io.github.tml.mosaic.install.collector.InfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 资源收集适配器
 */
@Slf4j
public abstract class AbstractResourceFileAdapter implements ResourceFileAdapter{

    private List<InfoCollector> collectorList;

    public AbstractResourceFileAdapter(List<InfoCollector> collectorList) {
        this.collectorList = collectorList;
    }

    @Override
    public InfoContext adapter(Resource resource) {
        InfoContext infoContext = new InfoContext();

        Optional.ofNullable(resource).orElseThrow(()->new CubeException("resource not exist"));

        infoContext.setResource(resource);
        infoContext.setResourceType(resourceFileType());

        try {
            Optional.ofNullable(collectorList)
                    .orElse(Collections.emptyList())
                    .forEach(collector -> collector.collect(infoContext));
        }catch (Exception e){
            log.error("adapter collect info error :{}",e.getMessage());
            return null;
        }

        return infoContext;
    }
}
