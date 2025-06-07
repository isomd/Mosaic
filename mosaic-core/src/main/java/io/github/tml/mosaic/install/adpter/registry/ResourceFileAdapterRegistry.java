package io.github.tml.mosaic.install.adpter.registry;

import io.github.tml.mosaic.install.adpter.ResourceFileAdapter;

public interface ResourceFileAdapterRegistry {
    void registerAdapter(String adapterType, ResourceFileAdapter installer);
    ResourceFileAdapter getAdapter(String adapterType);
}