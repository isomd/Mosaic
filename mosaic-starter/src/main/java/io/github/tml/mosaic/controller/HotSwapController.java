package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.service.HotSwapService;
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
@RequestMapping("/mosaic/hotSwap")
public class HotSwapController {

    @Resource
    HotSwapService hotSwapService;

    /**
     * 根据全限定类名获取类字符串源码
     * @param classFullName 全限定类名
     * @return
     */
    @GetMapping("getClassStr")
    public R<?> classString(@RequestParam(value = "className") String classFullName){
        return hotSwapService.getClassStrByClassFullName(classFullName);
    }

}