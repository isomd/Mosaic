package io.github.tml.mosaic.entity.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Cube概览统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeOverviewDTO {
    
    private Integer totalCubes;
    private Integer totalExtensionPackages;
    private Integer totalExtensionPoints;
    private Map<String, Long> cubesByModel;
}