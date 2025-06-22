package io.github.tml.mosaic.cube.factory.definition;

import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Data;

/**
 * 描述: CubeDefinition注册结果实体
 */
@Data
public class CubeRegistrationResult {

    private final GUID cubeId;
    private final boolean success;
    private final String message;

    public CubeRegistrationResult(GUID cubeId, boolean success, String message) {
        this.cubeId = cubeId;
        this.success = success;
        this.message = message;
    }
}
