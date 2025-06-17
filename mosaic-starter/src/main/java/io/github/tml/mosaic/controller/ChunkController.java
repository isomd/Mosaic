package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.core.chunk.ChunkManager;
import io.github.tml.mosaic.service.ChunkService;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author welsir
 * @description :
 * @date 2025/6/16
 */
@Slf4j
@RestController
@ResponseBody
@RequestMapping("/mosaic/chunk")
public class ChunkController {

    @Resource
    ChunkService chunkService;

    @GetMapping("getClassStr")
    public R<?> classString(@RequestParam(value = "fullName") String fullName){
        return chunkService.getClassStrByClassFullName(fullName);
    }

}