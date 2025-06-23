package io.github.tml.mosaic.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.tml.mosaic.MosaicAgent;
import io.github.tml.mosaic.core.components.DeployContextHolder;
import io.github.tml.mosaic.entity.req.AgentServerRequestDTO;
import io.github.tml.mosaic.entity.resp.AgentServerResp;
import io.github.tml.mosaic.util.AgentUtil;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.ByteBuffer;
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

    public void start(Socket socket) {
        AgentServerResp resp = new AgentServerResp();
        try (
                socket;
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            // 读取请求
            int length = dis.readInt();
            byte[] jsonBytes = new byte[length];
            dis.readFully(jsonBytes);
            String json = new String(jsonBytes, StandardCharsets.UTF_8);

            // 加载请求类 & 反序列化
            Class<?> req = AgentUtil.getClassByInst("io.github.tml.mosaic.entity.req.AgentServerRequestDTO");
            ClassLoader targetClassLoader = req.getClassLoader();
            Thread.currentThread().setContextClassLoader(targetClassLoader);

            ParserConfig parserConfig = new ParserConfig();
            parserConfig.setDefaultClassLoader(targetClassLoader);
            Object reqObj = JSON.parseObject(json, req, parserConfig, Feature.SupportAutoType);

            // 获取参数
            Method getClassNameMethod = req.getMethod("getClassName");
            String className = (String) getClassNameMethod.invoke(reqObj);
            Method getClassCodeMethod = req.getMethod("getClassCode");
            String classCode = (String) getClassCodeMethod.invoke(reqObj);

            DeployContextHolder.set(Map.of("code", classCode));

            // 热替换逻辑
            AgentUtil.instrumentation.retransformClasses(AgentUtil.getClassByInst(className));
            DeployContextHolder.clear();

            // 构造成功响应
            resp.setIsSuccess(Boolean.TRUE);
            resp.setErrorMessage("Class :" + className + " 更新成功");

            String responseJson = JSON.toJSONString(resp);
            byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
            dos.write(bytes);
            dos.flush();

        } catch (InvocationTargetException | UnmodifiableClassException | NoSuchMethodException |
                 IllegalAccessException | IOException e) {
            resp.setIsSuccess(Boolean.FALSE);
            resp.setErrorMessage("更新失败：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}