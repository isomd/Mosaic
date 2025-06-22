package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.hotSwap.model.ChangeRecord;
import lombok.Data;

/**
 * @author welsir
 * @description :
 * @date 2025/6/23
 */
@Data
public class HotSwapPointDTO {

    private String className;
    private HotSwapContext.InsertType changeType;
    private int lineNumber;
    private String methodName;
}