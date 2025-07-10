package io.github.tml.mosaic.entity.req;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 描述: Angel Cube状态更新请求
 * @author suifeng
 * 日期: 2025/7/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AngelCubeStatusUpdateReq {

    private String cubeId;

    private AngelCubeAction action;
    
    public enum AngelCubeAction {
        START("启动"),
        STOP("停止");
        
        private final String description;
        
        AngelCubeAction(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}