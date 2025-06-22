package io.github.tml.mosaic.core.watcher;

import io.github.tml.mosaic.core.components.DeployComponent;
import io.github.tml.mosaic.core.components.DeployContext;
import io.github.tml.mosaic.core.components.codeProcessor.CodeProxyComponent;
import io.github.tml.mosaic.core.components.inspector.SecurityChainComponent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class MosaicAgentWatcher implements ClassFileTransformer {

    private final List<DeployComponent> components = new ArrayList<>();

    public MosaicAgentWatcher() {
        // 组件收集
        components.add(new SecurityChainComponent());
        components.add(new CodeProxyComponent());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> targetClass,
                            ProtectionDomain protectionDomain,
                            byte[] classBytes) {
        if (targetClass == null) {
            return classBytes; // 只处理重定义 忽略首次加载
        }

        // 创建上下文
        DeployContext context = new DeployContext(loader, className, classBytes);

        try {
            components.forEach(component -> {component.execute(context);});
            // 返回转换后的字节码
            return context.getProxyBytes();

        } catch (Exception e) {
            // 返回原始字节码
            return context.getOriginalBytes();
        }
    }

}