package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.chunk.ChunkManager;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("getClassStr")
    public R<?> classString(@RequestParam(value = "fullName") String fullName){
        return R.success(ChunkManager.getProxyCode(fullName));
    }

}