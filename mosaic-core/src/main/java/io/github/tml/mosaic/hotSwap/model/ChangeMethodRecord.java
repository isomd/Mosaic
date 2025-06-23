package io.github.tml.mosaic.hotSwap.model;

import io.github.tml.mosaic.hotSwap.HotSwapContext;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
@Data
public class ChangeMethodRecord {
    private final String methodName;
    private final String oldSourceCode;
    private final String newSourceCode;

    public ChangeMethodRecord(String methodName, String oldSourceCode, String newSourceCode) {
        this.methodName = methodName;
        this.oldSourceCode = oldSourceCode;
        this.newSourceCode = newSourceCode;
    }
}