package io.github.tml.mosaic.entity.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Cube过滤条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeFilterDTO {
    
    private String name;
    private String model;
    private String version;

    public boolean hasName() {
        return name != null && !name.trim().isEmpty();
    }
    
    public boolean hasModel() {
        return model != null && !model.trim().isEmpty();
    }
    
    public boolean hasVersion() {
        return version != null && !version.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("CubeFilterDTO{name='%s', model='%s', version='%s'}", name, model, version);
    }
}