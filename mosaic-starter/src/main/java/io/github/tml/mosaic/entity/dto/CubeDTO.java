package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CubeDTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CubeDTO extends CubeDefinition {

    private CubeStatus status;
    private LocalDateTime registeredTime;
    private LocalDateTime lastUpdatedTime;

    public CubeDTO(String id, String name, String version, 
                   String description, String model, String scope,
                   String className, ClassLoader classLoader) {
        super(id, name, version, description, model, scope, className, classLoader);
        this.status = determineStatus();
        this.registeredTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    /**
     * 判断Cube是否已激活
     */
    public boolean isActive() {
        return this.status == CubeStatus.ACTIVE;
    }

    /**
     * 判断Cube是否配置完整
     */
    public boolean isConfigComplete() {
        return Objects.nonNull(this.getId()) && 
               Objects.nonNull(this.getName()) && 
               Objects.nonNull(this.getClassName()) &&
               !this.getExtensionPackages().isEmpty();
    }

    /**
     * 获取扩展点总数
     */
    public int getTotalExtensionPointCount() {
        return this.getExtensionPackages().stream()
                .mapToInt(pkg -> pkg.getExtensionPoints().size())
                .sum();
    }

    /**
     * 确定Cube状态
     */
    private CubeStatus determineStatus() {
        if (this.getClassLoader() != null) {
            return CubeStatus.ACTIVE;
        }
        return CubeStatus.INACTIVE;
    }

    public enum CubeStatus {
        ACTIVE, INACTIVE
    }
}