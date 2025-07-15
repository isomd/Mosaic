package io.github.tml.mosaic.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Cube多配置更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CubeMultiConfigUpdateReq {

    private String cubeId;
    
    private String configId;

    private Map<String, Object> configurations;
}