package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;

import java.util.List;

public abstract class GenerateWorldConstruct extends WorldConstruct{

    public GenerateWorldConstruct() {
    }

    public GenerateWorldConstruct(WorldContainer runningWorldContainer) {
        super(runningWorldContainer);
    }
    @Override
    public void constructWorld() {
        hotReplaceCode();

        replaceBeans();
    }

    protected abstract void replaceBeans();

    protected abstract void hotReplaceCode();

    @Override
    public Boolean saveWorld() {
        return Boolean.TRUE;
    }
}
