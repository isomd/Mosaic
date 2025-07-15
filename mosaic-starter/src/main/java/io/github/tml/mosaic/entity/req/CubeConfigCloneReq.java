package io.github.tml.mosaic.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cube配置克隆请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CubeConfigCloneReq {

    private String cubeId;
    
    private String sourceConfigId; // 可选，为空则克隆默认配置
}