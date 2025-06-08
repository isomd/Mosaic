package io.github.tml.mosaic.util;

import javax.tools.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
public class ChunkFileUtil {

    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /**
     * @param className 类名
     * @param sourceCode Java 源码字符串
     * @return 编译后 .class 文件的绝对路径
     */
    public static String compileAndWriteToFile(String className, String sourceCode) throws IOException {
        // 编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("系统找不到 Java 编译器，可能是运行在 JRE 而不是 JDK 上。");
        }

        // 创建输出目录
        Path outputPath = Paths.get(System.getProperty("user.dir"), "mosaic", "chunk", DateUtil.getCurrentDate());
        Files.createDirectories(outputPath);

        // 创建 Java 文件对象
        JavaFileObject file = new JavaSourceFromString(className, sourceCode);

        // 配置编译参数
        List<String> options = List.of("-d", outputPath.toAbsolutePath().toString());

        // 执行编译
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, options, null, List.of(file));

        boolean success = task.call();
        if (!success) {
            StringBuilder errorMsg = new StringBuilder("编译失败:\n");
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                errorMsg.append(diagnostic.getKind())
                        .append(": ")
                        .append(diagnostic.getMessage(null))
                        .append("\n");
            }
            throw new RuntimeException(errorMsg.toString());
        }

        // 拼接 .class 文件路径
        String classFileName = className.substring(className.lastIndexOf(".")+1) + ".class";
        Path classFile = outputPath.resolve(classFileName);

        if (!Files.exists(classFile)) {
            // 如果源码带 package，需要拼目录
            try (Stream<Path> stream = Files.walk(outputPath)) {
                Optional<Path> matched = stream
                        .filter(p -> p.getFileName().toString().equals(classFileName))
                        .findFirst();
                if (matched.isPresent()) {
                    classFile = matched.get();
                } else {
                    throw new FileNotFoundException(".class 文件未找到，可能源码中包含包名导致路径变化。");
                }
            }
        }

        return classFile.toAbsolutePath().toString();
    }

    public static String escapeForJson(String javaCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("\""); // 开始双引号

        for (char c : javaCode.toCharArray()) {
            switch (c) {
                case '\"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20 || c > 0x7E) {
                        // 控制字符或 Unicode 非 ASCII
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }

        sb.append("\""); // 结束双引号
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(escapeForJson("package com.agentscript.controller;\n" +
                "\n" +
                "import com.agentscript.entity.dto.OriginClassReplaceRequestDTO;\n" +
                "import com.agentscript.entity.dto.RefreshCubeRequestDTO;\n" +
                "import com.agentscript.util.ChunkFileUtil;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "\n" +
                "import java.io.File;\n" +
                "import java.io.IOException;\n" +
                "import java.lang.management.ManagementFactory;\n" +
                "\n" +
                "/**\n" +
                " * @author welsir\n" +
                " * @description :\n" +
                " * @date 2025/5/29\n" +
                " */\n" +
                "@RestController\n" +
                "@RequestMapping(\"/mosaic/chunkRefresh\")\n" +
                "public class ChunkLoaderAPI {\n" +
                "\n" +
                "\n" +
                "\n" +
                "    @PostMapping(\"/replace\")\n" +
                "    public String SetupCube(@RequestBody OriginClassReplaceRequestDTO requestDTO) {\n" +
                "        try {\n" +
                "            System.out.println(\"刘涛猛攻航天！\");\n" +
                "            return ChunkFileUtil.compileAndWriteToFile(requestDTO.getClassName(),requestDTO.getClassCode());\n" +
                "        }catch (IOException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping(\"/refresh\")\n" +
                "    public void refreshProject(RefreshCubeRequestDTO requestDTO){\n" +
                "        String pid = getCurrentPid();\n" +
                "        String agentAttacherJar = \"\\\"D:\\\\code\\\\forme\\\\agent\\\\demo\\\\agent-core\\\\target\\\\agent-core-0.0.1-SNAPSHOT.jar\\\"\";\n" +
                "        String agentJarPath = \"\\\"D:\\\\code\\\\forme\\\\agent\\\\demo\\\\target\\\\demo-0.0.1-SNAPSHOT.jar\\\"\";\n" +
                "\n" +
                "        try {\n" +
                "            String javaBin = System.getProperty(\"java.home\") + File.separator + \"bin\" + File.separator + \"java\";\n" +
                "            ProcessBuilder pb = new ProcessBuilder(\n" +
                "                    javaBin,\n" +
                "                    \"-cp\", agentAttacherJar,\n" +
                "                    \"com.agent.MainTest\",\n" +
                "                    pid,\n" +
                "                    agentJarPath,\n" +
                "                    \"{\\n\" +\n" +
                "                            \"  \\\"className\\\": \"+requestDTO.getClassName()+\",\\n\" +\n" +
                "                            \"  \\\"path\\\": \"+requestDTO.getClassPath() +\n" +
                "                            \"}\"\n" +
                "            );\n" +
                "            pb.inheritIO();\n" +
                "            Process process = pb.start();\n" +
                "        } catch (IOException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private String getCurrentPid() {\n" +
                "        String name = ManagementFactory.getRuntimeMXBean().getName(); // format: pid@hostname\n" +
                "        return name.split(\"@\")[0];\n" +
                "    }\n" +
                "\n" +
                "}"));
    }
}