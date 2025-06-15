package io.github.tml.mosaic.entity.req;

import lombok.Data;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
@Data
public class HotSwapRequestDTO {

    private int line;
    private String className;
    private String pluginId;
    private String slotId;
    private String slotName;
    private String exPackageId;
    private String exPointId;
    private List<String> args;
    private int cmd;

}