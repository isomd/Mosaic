package io.github.tml.mosaic.hotSwap.model;

import java.util.Collections;
import java.util.List;

/**
 * @author welsir
 * @description : 热部署每次更新所对应的内存实体对象
 * @date 2025/6/21
 */
public class ChangeRecord {
    private final String className;
    private final String methodName;
    private final ChangeType changeType; // ADDED, MODIFIED, DELETED
    private final List<LineChange> lineChanges;

    public ChangeRecord(String className, String methodName, ChangeType changeType, List<LineChange> lineChanges) {
        this.className = className;
        this.methodName = methodName;
        this.changeType = changeType;
        this.lineChanges = Collections.unmodifiableList(lineChanges);
    }

    public enum ChangeType {
        INSERT,UPDATE_ASSIGN_RIGHT
    }
}