package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.hotSwap.HotSwapContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */
@NoArgsConstructor
@AllArgsConstructor
public class HotSwapDTO {

    @Getter
    @Setter
    String fullName;
    @Getter
    @Setter
    int targetLine;
    @Getter
    @Setter
    HotSwapContext.InsertType type;
    @Getter
    @Setter
    String proxyCode;

}