package io.github.tml.mosaic.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Cube多配置添加请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CubeConfigAddReq {

    private String cubeId;

    private Map<String, Object> configurations;
}