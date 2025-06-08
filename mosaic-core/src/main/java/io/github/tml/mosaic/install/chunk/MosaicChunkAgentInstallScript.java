package io.github.tml.mosaic.install.chunk;

import com.sun.tools.attach.VirtualMachine;

public class MosaicChunkAgentInstallScript {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java MosaicChunkAgentInstall <pid> <agentJarPath>");
            return;
        }
        String pid = args[0];
        String agentJar = args[1];

        System.out.println("Attaching to JVM PID: " + pid);
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentJar);
        vm.detach();
        System.out.println("Agent attached.");
    }
}