package io.github.tml.controller;

import io.github.tml.mosaic.XiaochunBean;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/xiaochun")
public class XiaoChunController implements ComponentReplace {
    @Resource
    CubeContext cubeContext;

//    @GetMapping("/getRes")
//    public String getRes() {
//        return ;
//    }
}
