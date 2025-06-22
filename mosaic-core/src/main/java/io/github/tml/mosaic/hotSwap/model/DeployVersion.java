package io.github.tml.mosaic.hotSwap.model;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
@Data
public class DeployVersion {

    private final GUID versionId;
    private final LocalDateTime createdAt;
    private final ChangeRecord changeRecords;

    public DeployVersion(GUID versionId, LocalDateTime createdAt, ChangeRecord changeRecords) {
        this.versionId = versionId;
        this.createdAt = createdAt;
        this.changeRecords = changeRecords;
    }

}