package io.github.tml.mosaic.controller;
import io.github.tml.mosaic.entity.vo.CubeInfoVO;
import io.github.tml.mosaic.entity.vo.CubeOverviewVO;
import io.github.tml.mosaic.service.impl.CubeInfoService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private final CubeInfoService cubeInfoService;

    /**
     * 获取所有Cube插件信息
     */
    @GetMapping("/list")
    public R<List<CubeInfoVO>> getAllCubes() {
        log.info("Received request to get all cube information");
        List<CubeInfoVO> cubeInfos = cubeInfoService.getAllCubeInfos();
        log.info("Successfully returned {} cube information records", cubeInfos.size());
        return R.success(cubeInfos);
    }

    /**
     * 根据条件过滤Cube插件
     */
    @GetMapping("/search")
    public R<List<CubeInfoVO>> searchCubes(@RequestParam(required = false) String name, @RequestParam(required = false) String model, @RequestParam(required = false) String version) {
        log.info("Received cube search request with criteria - name: {}, model: {}, version: {}", name, model, version);
        CubeInfoService.CubeFilterCriteria criteria = new CubeInfoService.CubeFilterCriteria();
        criteria.setName(name);
        criteria.setModel(model);
        criteria.setVersion(version);
        List<CubeInfoVO> cubeInfos = cubeInfoService.getCubeInfosByFilter(criteria);
        log.info("Search completed, found {} matching cubes", cubeInfos.size());
        return R.success(cubeInfos);
    }

    /**
     * 获取指定Cube详细信息
     */
    @GetMapping("/{cubeId}")
    public R<CubeInfoVO> getCubeById(@PathVariable String cubeId) {
        log.info("Received request to get cube information for ID: {}", cubeId);
        CubeInfoVO cubeInfo = cubeInfoService.getCubeInfoById(cubeId);
        if (cubeInfo == null) {
            log.warn("Cube not found for ID: {}", cubeId);
            return R.error("Cube not found for ID: " + cubeId);
        }
        log.info("Successfully returned cube information for ID: {}", cubeId);
        return R.success(cubeInfo);
    }

    /**
     * 获取Cube概览统计
     */
    @GetMapping("/overview")
    public R<CubeOverviewVO> getCubeOverview() {
        log.info("Received request to get cube overview statistics");
        CubeOverviewVO overview = cubeInfoService.getCubeOverview();
        log.info("Successfully returned cube overview statistics");
        return R.success(overview);
    }

    /**
     * 检查Cube是否存在
     */
    @GetMapping("/{cubeId}/exists")
    public R<Boolean> checkCubeExists(@PathVariable String cubeId) {
        log.debug("Checking existence of cube with ID: {}", cubeId);
        boolean exists = cubeInfoService.existsCube(cubeId);
        log.debug("Cube {} existence check result: {}", cubeId, exists);
        return R.success(exists);
    }
}