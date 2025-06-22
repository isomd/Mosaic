package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.dto.JarPkgDTO;
import io.github.tml.mosaic.entity.vo.JarInstallResult;
import io.github.tml.mosaic.entity.vo.JarOperationResult;
import io.github.tml.mosaic.entity.vo.JarUploadResult;
import io.github.tml.mosaic.service.JarPkgService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * JAR包管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/mosaic/jar")
@RequiredArgsConstructor
public class JarPkgController {

    private final JarPkgService jarPkgService;

    // ==================== 文件管理接口 ====================

    @PostMapping("/batch-upload")
    public R<List<JarUploadResult>> batchUploadJarPackages(@RequestParam("files") MultipartFile[] files) {
        return jarPkgService.batchUploadJarPackages(files);
    }

    @PostMapping("/list")
    public R<List<JarPkgDTO>> listJarPackages() {
        return jarPkgService.listJarPackages();
    }

    @PostMapping("/{jarId}/rename")
    public R<JarOperationResult> renameJarPackage(@PathVariable String jarId, @RequestParam String newFilename) {
        return jarPkgService.renameJarPackage(jarId, newFilename);
    }

    @PostMapping("/batch-delete")
    public R<List<JarOperationResult>> batchDeleteJarPackages(@RequestBody List<String> jarIds) {
        return jarPkgService.batchDeleteJarPackages(jarIds);
    }

    // ==================== Cube定义相关接口 ====================

    @PostMapping("/batch-install")
    public R<List<JarInstallResult>> batchInstallJarPackages(@RequestBody List<String> jarIds) {
        return jarPkgService.batchInstallJarPackages(jarIds);
    }
}