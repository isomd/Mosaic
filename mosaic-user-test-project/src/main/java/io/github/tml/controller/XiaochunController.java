package io.github.tml.controller;

import io.github.tml.mosaic.controller.WorldController;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.domain.WorldDomain;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@RestController
@RequestMapping("/xiaochun")
public class XiaochunController extends WorldController implements ComponentReplace {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MosaicWorld mosaicWorld;

    @Override
    public void replace(WorldComponentManager worldComponentManager) {

    }

    @GetMapping("/getNowBean")
    public String getNowBean() {
        return applicationContext.getBean(mosaicWorld.getRunningWorldContainer().getComponentName(CubeContext.class)).toString();
    }
}
