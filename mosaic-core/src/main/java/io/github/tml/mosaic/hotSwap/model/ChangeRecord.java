package io.github.tml.mosaic.hotSwap.model;

import io.github.tml.mosaic.hotSwap.HotSwapContext;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author welsir
 * @description : 热部署每次更新所对应的内存实体对象
 * @date 2025/6/21
 */
@Data
public class ChangeRecord {
    private final String className;
    private final String methodName;
    private final HotSwapContext.InsertType changeType;
    private final List<LineChange> lineChanges;

    public ChangeRecord(String className, String methodName, HotSwapContext.InsertType changeType, List<LineChange> lineChanges) {
        this.className = className;
        this.methodName = methodName;
        this.changeType = changeType;
        this.lineChanges = Collections.unmodifiableList(lineChanges);
    }
}