package io.github.tml.mosaic.hotSwap.async;

import io.github.tml.mosaic.core.components.DeployContext;

public interface AsyncNotifier {
    void notifyAsync(DeployContext context);
}