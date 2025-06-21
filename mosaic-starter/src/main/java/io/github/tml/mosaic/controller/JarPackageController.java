package io.github.tml.mosaic.controller;


import io.github.tml.mosaic.entity.JarPackageInfo;
import io.github.tml.mosaic.entity.vo.JarUploadResult;
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

    @PostMapping("/uploadJar")
    public R<JarUploadResult> uploadJarPackage(@RequestParam("file") MultipartFile file) {
        return jarPackageService.uploadJarPackage(file);
    }

    @PostMapping("/batchUploadJars")
    public R<List<JarUploadResult>> batchUploadJarPackages(@RequestParam("files") MultipartFile[] files) {
        return jarPackageService.batchUploadJarPackages(files);
    }

    @PostMapping("/getJarList")
    public R<List<JarPackageInfo>> listJarPackages() {
        return jarPackageService.listJarPackages();
    }

    @PostMapping("/renameJar")
    public R<Void> renameJarPackage(@RequestParam String oldFilename, @RequestParam String newFilename) {
        return jarPackageService.renameJarPackage(oldFilename, newFilename);
    }

    @PostMapping("/deleteJar")
    public R<Void> deleteJarPackage(@RequestParam String filename) {
        return jarPackageService.deleteJarPackage(filename);
    }
}