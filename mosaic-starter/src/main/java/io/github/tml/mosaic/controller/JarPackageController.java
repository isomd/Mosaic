package io.github.tml.mosaic.controller;


import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.entity.JarPackageInfo;
import io.github.tml.mosaic.service.JarPackageService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * JAR包管理控制器
 * @author suifeng
 * @date 2025/6/15
 */
@Slf4j
@RestController
@RequestMapping("/mosaic/jar")
@RequiredArgsConstructor
public class JarPackageController {

    private final JarPackageService jarPackageService;

    /**
     * 上传JAR包
     */
    @PostMapping("/uploadJar")
    public R<String> uploadJarPackage(@RequestParam("file") MultipartFile file) {
        try {
            String filename = jarPackageService.uploadJarPackage(file);

            log.info("JAR包上传成功: {}", filename);
            return R.success("JAR包上传成功", filename);

        } catch (IllegalArgumentException e) {
            log.warn("JAR包上传参数错误: {}", e.getMessage());
            return R.error(e.getMessage());

        } catch (Exception e) {
            log.error("JAR包上传失败", e);
            return R.error("JAR包上传失败，请稍后重试");
        }
    }

    /**
     * 获取JAR包列表
     */
    @PostMapping("/getJarList")
    public R<List<JarPackageInfo>> listJarPackages() {
        try {
            List<JarPackageInfo> jarPackages = jarPackageService.listJarPackages();
            log.debug("获取JAR包列表成功，共{}个文件", jarPackages.size());
            return R.success("获取JAR包列表成功", jarPackages);

        } catch (Exception e) {
            log.error("获取JAR包列表失败", e);
            return R.error("获取JAR包列表失败，请稍后重试");
        }
    }

    /**
     * 重命名JAR包
     */
    @PostMapping("/renameJar")
    public R<Void> renameJarPackage(@RequestParam String oldFilename, @RequestParam String newFilename) {
        try {
            jarPackageService.renameJarPackage(oldFilename, newFilename);
            log.info("JAR包重命名成功: {} -> {}", oldFilename, newFilename);
            return R.success("JAR包重命名成功");

        } catch (IllegalArgumentException e) {
            log.warn("JAR包重命名参数错误: {}", e.getMessage());
            return R.error(e.getMessage());

        } catch (Exception e) {
            log.error("JAR包重命名失败: {} -> {}", oldFilename, newFilename, e);
            return R.error("JAR包重命名失败，请稍后重试");
        }
    }

    /**
     * 删除JAR包
     */
    @PostMapping("/deleteJar")
    public R<Void> deleteJarPackage(@RequestParam String filename) {
        try {
            jarPackageService.deleteJarPackage(filename);
            log.info("JAR包删除成功: {}", filename);
            return R.success("JAR包删除成功");

        } catch (IllegalArgumentException e) {
            log.warn("JAR包删除参数错误: {}", e.getMessage());
            return R.error(e.getMessage());

        } catch (Exception e) {
            log.error("JAR包删除失败: {}", filename, e);
            return R.error("JAR包删除失败，请稍后重试");
        }
    }
}