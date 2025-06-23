package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;

import java.util.List;

public class DefaultWorldConstruct extends WorldConstruct{

    private List<ComponentReplace> replaceList;

    public DefaultWorldConstruct(List<ComponentReplace> replaceList) {
        this.replaceList = replaceList;
    }

    public DefaultWorldConstruct(List<ComponentReplace> replaceList, WorldContainer runningWorldContainer) {
        super(runningWorldContainer);
        this.replaceList = replaceList;
    }
    @Override
    public void constructWorld() {
        // TODO: 热加载

        // 更新spring bean
        for (ComponentReplace replace : replaceList){

        }
    }

    @Override
    public Boolean saveWorld() {
        return Boolean.TRUE;
    }
}
