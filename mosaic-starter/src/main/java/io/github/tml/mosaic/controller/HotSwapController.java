package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.service.HotSwapService;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        return R.success(hotSwapService.getClassStrByClassFullName(classFullName));
    }

    @PostMapping("/create/point")
    public R<?> createPoint(@RequestBody HotSwapPointRequest hotSwapPointRequest){
        CreateHotSwapPointResp resp = hotSwapService.createHotSwapPoint(hotSwapPointRequest);
        if(StringUtils.isEmpty(resp.getErrorMsg())){
            return R.success(resp);
        }
        return R.error(resp.getErrorMsg());
    }

}