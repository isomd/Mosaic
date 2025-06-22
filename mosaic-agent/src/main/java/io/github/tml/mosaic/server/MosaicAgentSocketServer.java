package io.github.tml.mosaic.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.tml.mosaic.MosaicAgent;
import io.github.tml.mosaic.core.components.DeployContextHolder;
import io.github.tml.mosaic.entity.req.AgentServerRequestDTO;
import io.github.tml.mosaic.util.AgentUtil;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author welsir
 * @description :
 * @date 2025/6/7
 */
public class MosaicAgentSocketServer {

    public void start(int port) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                int length = dis.readInt();
                byte[] jsonBytes = new byte[length];
                dis.readFully(jsonBytes);

                String json = new String(jsonBytes, StandardCharsets.UTF_8);

                Class<?> req = AgentUtil.getClassByInst("io.github.tml.mosaic.entity.req.AgentServerRequestDTO");
                ClassLoader targetClassLoader = req.getClassLoader();

                Thread.currentThread().setContextClassLoader(targetClassLoader);

                ParserConfig parserConfig = new ParserConfig();
                parserConfig.setDefaultClassLoader(targetClassLoader);

                Object reqObj = JSON.parseObject(json, req, parserConfig, Feature.SupportAutoType);
                Method getClassNameMethod = req.getMethod("getClassName");
                String className = (String) getClassNameMethod.invoke(reqObj);

                Method getClassCodeMethod = req.getMethod("getClassCode");
                String classCode = (String) getClassCodeMethod.invoke(reqObj);

                DeployContextHolder.set(Map.of("code", classCode));

                //热替换
                AgentUtil.instrumentation
                        .retransformClasses(AgentUtil.getClassByInst(className));

                DeployContextHolder.clear();
                System.out.println("Mosaic agent proxy finished...");
            }
        } catch (InvocationTargetException | UnmodifiableClassException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}