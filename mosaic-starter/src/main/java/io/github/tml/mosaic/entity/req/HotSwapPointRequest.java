package io.github.tml.mosaic.entity.req;

import lombok.Data;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */

@Data
public class HotSwapPointRequest {

    private String slotId;

    private String cubeId;

    private String exPackageId;

    private String exPointId;

    private String resName;

    private boolean setupFlag = false;

    //----------------------------------------

    private String className;
    private int targetLine;
    private String changeType;
    private List<String> args;
}