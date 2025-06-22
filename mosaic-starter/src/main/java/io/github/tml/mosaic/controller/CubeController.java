package io.github.tml.mosaic.controller;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.service.CubeService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: Cube插件管理控制器
 * @author suifeng
 * 日期: 2025/6/7
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mosaic/cube")
public class CubeController {

    private final CubeService cubeService;

    /**
     * 获取所有Cube插件信息
     */
    @GetMapping("/list")
    public R<?> getAllCubes() {
        return cubeService.getCubeList();
    }

    /**
     * 根据条件搜索Cube插件
     */
    @GetMapping("/search")
    public R<?> searchCubes(@RequestBody CubeFilterReq cubeFilterReq) {
        return cubeService.getCubesByFilter(cubeFilterReq);
    }

    /**
     * 获取指定Cube详细信息
     */
    @GetMapping("/{cubeId}")
    public R<?> getCubeById(@PathVariable String cubeId) {
        return cubeService.getCubeById(cubeId);
    }

    /**
     * 获取Cube概览统计
     */
    @GetMapping("/overview")
    public R<?> getCubeOverview() {
        return cubeService.getCubeOverview();
    }

    /**
     * 检查Cube是否存在
     */
    @GetMapping("/{cubeId}/exists")
    public R<?> checkCubeExists(@PathVariable String cubeId) {
        return cubeService.checkCubeExists(cubeId);
    }

    /**
     * 获取指定Cube的配置信息
     */
    @GetMapping("/{cubeId}/getConfiguration")
    public R<?> getCubeConfiguration(@PathVariable String cubeId) {
        return cubeService.getCubeConfiguration(cubeId);
    }

    /**
     * 更新指定Cube的配置信息
     */
    @PostMapping("/updateConfiguration")
    public R<?> updateCubeConfiguration(@RequestBody CubeConfigUpdateReq configReq) {
        return cubeService.updateCubeConfiguration(configReq);
    }
}