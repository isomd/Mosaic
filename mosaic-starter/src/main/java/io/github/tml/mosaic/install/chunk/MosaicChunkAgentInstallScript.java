package io.github.tml.mosaic.install.chunk;

import com.sun.tools.attach.VirtualMachine;


public class MosaicChunkAgentInstallScript {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("参数不正确");
        }
        String pid = args[0];
        String agentJar = args[1];
        String bindPort = args[2];
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentJar,bindPort);
        vm.detach();

    }
}