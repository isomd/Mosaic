package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.WorldContainer;

public class DefaultWorldConstruct extends WorldConstruct{

    public DefaultWorldConstruct() {
    }

    public DefaultWorldConstruct(WorldContainer runningWorldContainer) {
        super(runningWorldContainer);
    }

    @Override
    public void constructWorld() {
        // TODO: 热加载

        // 更新spring bean

    }

    @Override
    public Boolean saveWorld() {
        return Boolean.TRUE;
    }
}
