package io.github.tml.controller;

import io.github.tml.mosaic.controller.WorldController;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.domain.WorldDomain;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@RestController
@RequestMapping("/xiaochun")
public class XiaochunController extends WorldController implements ComponentReplace {

    @Resource
    private CubeContext cubeContext;

    @Override
    public void replace(WorldComponentManager worldComponentManager) {
        this.cubeContext = (CubeContext) worldComponentManager.getComponent("cubeContext");
    }

    @GetMapping("/getCubeContext")
    public String getCubeContext() {
        return cubeContext.toString();
    }
}
