package io.github.tml.mosaic.hotSwap.model;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
@Data
public class HotSwapPoint {

    private final String versionId;
    private final LocalDateTime createdAt;
    private final String className;
    private final HotSwapContext.InsertType changeType;
    private final ChangeMethodRecord changeRecord;

    public HotSwapPoint(String versionId, LocalDateTime createdAt, String className, HotSwapContext.InsertType changeType, ChangeMethodRecord changeRecord) {
        this.versionId = versionId;
        this.createdAt = createdAt;
        this.className = className;
        this.changeType = changeType;
        this.changeRecord = changeRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotSwapPoint that = (HotSwapPoint) o;
        return Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt);
    }

}