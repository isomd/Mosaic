package io.github.tml.controller;

import io.github.tml.config.DynamicResourceRegistrar;
import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/test")
public class UserController {

    @Autowired
    private DynamicResourceRegistrar dynamicResourceRegistrar;

    @Autowired
    CubeContext context;

    @GetMapping("/hello")
    public String hello(@RequestParam("name") String pluginName) {
        dynamicResourceRegistrar.registerPluginResources(pluginName);
        return String.format("http://localhost:9000/%s/index.html", pluginName);
    }

    @GetMapping("/context")
    public String context() {
        Cube cube = context.getCube(new GUUID("12345"));
        return Optional.ofNullable(cube).map(Cube::toString).orElse("null");
    }

    @GetMapping("/logPlugin")
    public Object useLogPlugin(@RequestParam("msg") String msg, @RequestParam String packageId, @RequestParam String pointId) {

        long startTime = System.currentTimeMillis();
        Object result = GoldenShovel.slotBootStrap()
                .slotId("log")
                .exPackageId(packageId)
                .exPointId(pointId)
                .cubeId(new GUUID("system.log.cube"))
                .build()
                .executeBootStrap()
                .run(msg);
        System.out.println("执行时间:" + (System.currentTimeMillis() - startTime));
        return result;
    }
}
