package io.github.tml.mosaic.entity.dto;

import lombok.Data;

/**
 * @author welsir
 * @description :
 * @date 2025/5/29
 */
@Data
public class OriginClassReplaceRequestDTO {

    /**
     * 类名
     */
    private String className;
    /**
     * 源码字符串
     */
    private String classCode;

}