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

        String classPath = AgentUtil.generateClassPathByEnvironment(context.getClassLoader());
        String replace = className.replace("/", ".");
        byte[] bytes = AgentUtil.compile(replace, DeployContextHolder.get().get("code"), classPath);
        context.setProxyBytes(bytes);
    }
}