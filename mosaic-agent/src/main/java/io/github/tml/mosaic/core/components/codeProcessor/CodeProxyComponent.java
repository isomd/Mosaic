package io.github.tml.mosaic.core.components.codeProcessor;

import io.github.tml.mosaic.core.components.DeployComponent;
import io.github.tml.mosaic.core.components.DeployContext;
import io.github.tml.mosaic.core.components.DeployContextHolder;
import io.github.tml.mosaic.util.AgentUtil;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class CodeProxyComponent implements DeployComponent {

    @Override
    public void execute(DeployContext context) {

        String className = context.getClassName();
        Class<?> aClass = AgentUtil.getClassByInst(className);

        String classPath = AgentUtil.generateClassPathByEnvironment(aClass);

        byte[] bytes = AgentUtil.compile(className, DeployContextHolder.get().get("code"), classPath);
        context.setProxyBytes(bytes);
    }
}