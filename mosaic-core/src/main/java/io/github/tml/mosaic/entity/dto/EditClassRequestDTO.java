package io.github.tml.mosaic.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/7
 */
@Data
public class EditClassRequestDTO {

    private int line;
    private String slotName;
    private List<String> params;
    private String className;
    private int cmd;
}