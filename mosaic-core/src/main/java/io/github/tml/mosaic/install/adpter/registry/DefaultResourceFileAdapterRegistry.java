package io.github.tml.mosaic.install.adpter.registry;

import io.github.tml.mosaic.install.adpter.core.ResourceFileAdapter;
import io.github.tml.mosaic.install.support.ResourceFileType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultResourceFileAdapterRegistry implements ResourceFileAdapterRegistry {

    private final Map<ResourceFileType, ResourceFileAdapter> adapterMap = new ConcurrentHashMap<>();

    @Override
    public void registerAdapter(ResourceFileType adapterType, ResourceFileAdapter adapter) {
        adapterMap.put(adapterType, adapter);
    }

    @Override
    public ResourceFileAdapter getAdapter(ResourceFileType adapterType) {
        return adapterMap.get(adapterType);
    }
}