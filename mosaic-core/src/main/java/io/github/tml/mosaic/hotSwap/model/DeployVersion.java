package io.github.tml.mosaic.hotSwap.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class DeployVersion {
    private final VersionId versionId;
    private final LocalDateTime createdAt;
    private final List<ChangeRecord> changeRecords;

    public DeployVersion(VersionId versionId, LocalDateTime createdAt) {
        this.versionId = versionId;
        this.createdAt = createdAt;
        this.changeRecords = new ArrayList<>();
    }

    public void addChangeRecord(ChangeRecord change) {
        this.changeRecords.add(change);
    }

    public VersionId getVersionId() {
        return versionId;
    }

    public List<ChangeRecord> getChangeRecords() {
        return Collections.unmodifiableList(changeRecords);
    }
}