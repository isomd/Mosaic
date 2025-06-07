package io.github.tml.mosaic.install.adpter.registry;

import io.github.tml.mosaic.install.adpter.ResourceFileAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultResourceFileAdapterRegistry implements ResourceFileAdapterRegistry {

    private final Map<String, ResourceFileAdapter> adapterMap = new ConcurrentHashMap<>();

    @Override
    public void registerAdapter(String adapterType, ResourceFileAdapter adapter) {
        adapterMap.put(adapterType, adapter);
    }

    @Override
    public ResourceFileAdapter getAdapter(String adapterType) {
        return adapterMap.get(adapterType);
    }
}