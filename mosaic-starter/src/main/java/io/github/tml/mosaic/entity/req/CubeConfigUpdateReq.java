package io.github.tml.mosaic.entity.req;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

/**
 * Cube配置更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CubeConfigUpdateReq {

    private String cubeId;

    private Map<String, Object> configurations;
}