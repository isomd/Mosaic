package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import lombok.Data;

import java.util.List;

@Data
public class JarUploadResult {
    private String filename;
    private boolean fileUploadSuccess;
    private List<CubeRegistrationResult> registrationResults;
    private String errorMsg;
}
