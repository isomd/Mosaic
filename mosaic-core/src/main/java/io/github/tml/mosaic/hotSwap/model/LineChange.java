package io.github.tml.mosaic.hotSwap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
@Setter
@Getter
public class LineChange {

    int lineNumber;
    String before;
    String after;
}