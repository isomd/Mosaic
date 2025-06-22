package io.github.tml.mosaic.core.components.inspector;

import io.github.tml.mosaic.core.components.DeployComponent;
import io.github.tml.mosaic.core.components.DeployContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class SecurityChainComponent implements DeployComponent {

    List<SecurityChain> securityChains = new ArrayList<>();

    public SecurityChainComponent() {

        securityChains.add(new ClassFilterNode());
        
    }


    @Override
    public void execute(DeployContext context) {

    }
}