package io.github.tml.mosaic.controller;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.service.CubeConfigService;
import io.github.tml.mosaic.service.CubeService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: Cube配置管理控制器
 * @author suifeng
 * 日期: 2025/6/7
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mosaic/cube")
public class CubeConfigController {

    private final CubeConfigService cubeConfigService;

    /**
     * 获取指定Cube的配置信息
     */
    @GetMapping("/{cubeId}/getConfiguration")
    public R<?> getCubeConfiguration(@PathVariable String cubeId) {
        return cubeConfigService.getCubeConfiguration(cubeId);
    }

    /**
     * 更新指定Cube的配置信息
     */
    @PostMapping("/updateConfiguration")
    public R<?> updateCubeConfiguration(@RequestBody CubeConfigUpdateReq configReq) {
        return cubeConfigService.updateCubeConfiguration(configReq);
    }
}