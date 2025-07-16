package io.github.tml.mosaic.domain.cube;

import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.cube.constant.CubeModelType;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AngleCubeDomain {

    @Autowired
    private CubeDomain cubeDomain;

    @Autowired
    private CubeActuatorProxy cubeActuatorProxy;

    // 存放anglecube对应的槽id
    private final Map<String, String> angleCubeSlotMap = new ConcurrentHashMap<>();

    /**
     * 验证是否为有效的Angel Cube
     */
    private CubeDTO validateAndGetAngelCube(String cubeId) {
        // 检查Cube是否存在
        Optional<CubeDTO> cubeOpt = cubeDomain.getCubeById(cubeId);

        cubeOpt.orElseThrow(()->new IllegalArgumentException("cube not exist，ID: " + cubeId));
        CubeDTO cube = cubeOpt.get();

        // 检查是否为angle类型
        if (!CubeModelType.ANGLE_TYPE.equals(cube.getModel())) {
            throw new IllegalArgumentException("只有angle类型的Cube才支持状态管理，当前类型: " + cube.getModel());
        }

        log.debug("Angel Cube validation passed for ID: {}", cubeId);
        return cube;
    }

    /**
     * 更新Angel Cube状态
     * @param cubeId Cube标识
     * @param action 执行动作
     * @return 操作结果
     */
    public boolean updateAngelCubeStatus(String cubeId, AngelCubeStatusUpdateReq.AngelCubeAction action) {
        log.debug("Domain: Updating Angel Cube status for ID: {} with action: {}", cubeId, action);

        try {
            // 1. 验证Cube是否存在且为function类型
            CubeDTO cube = validateAndGetAngelCube(cubeId);
            String slotId;
            if (StringUtils.isBlank(slotId = angleCubeSlotMap.get(cubeId))) {

                slotId = angleCubeSlotMap.get(cubeId);
            }
            cubeActuatorProxy.execute(new DotNotationId(slotId));
            return true;

        } catch (Exception e) {
            log.error("Domain: Failed to update Angel Cube status for ID: {}", cubeId, e);
            throw new RuntimeException("更新Angel Cube状态失败: " + e.getMessage(), e);
        }
    }

    private static String generateSecureRandomCode(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }


    private String buildAngelCubeSlotId(String cubeId) {
        return cubeId+".slot."+ generateSecureRandomCode(6);
    }
}
