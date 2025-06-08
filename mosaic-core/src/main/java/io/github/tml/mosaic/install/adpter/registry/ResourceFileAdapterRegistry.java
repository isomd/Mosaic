package io.github.tml.mosaic.install.adpter.registry;

import io.github.tml.mosaic.install.adpter.ResourceFileAdapter;
import io.github.tml.mosaic.install.support.ResourceFileType;

public interface ResourceFileAdapterRegistry {
    void registerAdapter(ResourceFileType adapterType, ResourceFileAdapter installer);
    ResourceFileAdapter getAdapter(ResourceFileType adapterType);
}