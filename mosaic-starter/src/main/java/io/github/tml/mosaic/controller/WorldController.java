package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.service.WorldService;
import io.github.tml.mosaic.util.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/world")
public class WorldController {
    @Resource
    private WorldService worldService;

    @PostMapping("/create")
    public R<?> createWorld(@RequestBody WorldContainerDTO worldContainerDTO){
        return R.success(worldService.createWorld(worldContainerDTO));
    }

    @GetMapping("/getAll")
    public R<?> getAllWorlds(){
        return R.success(worldService.getAllWorlds());
    }

    @GetMapping("/traverse")
    public R<?> traverseWorld(@RequestParam String uuid){
        return R.success(worldService.traverseWorld(uuid));
    }

    @GetMapping("/remove")
    public R<?> removeWorld(@RequestParam String uuid){
        return R.success(worldService.removeWorld(uuid));
    }

    @GetMapping("/getNowWorld")
    public R<?> getNowWorld(){
        return R.success(worldService.getNowWorld());
    }
}
