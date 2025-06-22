package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.hotSwap.HotSwapContext;
import lombok.*;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotSwapDTO {

    String fullName;
    int targetLine;
    HotSwapContext.InsertType type;

    String proxyCode;

}