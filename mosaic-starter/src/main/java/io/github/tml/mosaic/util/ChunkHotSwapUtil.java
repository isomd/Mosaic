package io.github.tml.mosaic.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.tml.mosaic.core.chunk.ChunkManager;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author welsir
 * @description :
 * @date 2025/6/13
 */
public class ChunkHotSwapUtil {

    private static final JavaParser parser = new JavaParser();

    private static String decompileClassToString(String classFilePath) {
        StringBuilder result = new StringBuilder();

        OutputSinkFactory mySink = new OutputSinkFactory() {
            @Override
            public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                return Collections.singletonList(SinkClass.STRING);
            }

            @Override
            public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                return (T content) -> {
                    if (sinkType == SinkType.JAVA && sinkClass == SinkClass.STRING) {
                        result.append(content).append(System.lineSeparator());
                    }
                };
            }

        };

        CfrDriver driver = new CfrDriver.Builder()
                .withOutputSink(mySink)
                .build();

        driver.analyse(Collections.singletonList(classFilePath));
        return result.toString();
    }

    public static String decompileClassFromClassName(String className) {
        try {
            // 将类名转换为路径
            String classResourcePath = className.replace('.', '/') + ".class";
            byte[] classBytes = getBytes(className, classResourcePath);

            // 写入临时 class 文件
            File tempFile = File.createTempFile("tmp-", ".class");
            tempFile.deleteOnExit();
            Files.write(tempFile.toPath(), classBytes);

            // 调用已有的反编译工具
            return decompileClassToString(tempFile.getAbsolutePath()).replaceFirst("(?s)^/\\*.*?\\*/\\s*", "");

        } catch (Exception e) {
            throw new RuntimeException("Decompile failed: " + className, e);
        }
    }

    private static byte[] getBytes(String className, String classResourcePath) throws IOException {
        ClassLoader loader = ChunkHotSwapUtil.class.getClassLoader();
        InputStream inputStream = loader.getResourceAsStream(classResourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot find class file for: " + className);
        }

        // 读取字节流
        byte[] classBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            classBytes = baos.toByteArray();
        }
        return classBytes;
    }

    private static CompilationUnit parseSourceCode(String sourceCode) {
        ParseResult<CompilationUnit> result = parser.parse(sourceCode);
        return result.getResult().orElseThrow(() -> new IllegalArgumentException("无法解析源码"));
    }

    private static void addImports(CompilationUnit cu, Set<String> importsToAdd) {
        if (importsToAdd == null) return;
        for (String imp : importsToAdd) {
            cu.addImport(imp);
        }
    }

    private static void processMethodStatements(
            CompilationUnit cu,
            int targetLine,
            ChunkManager.InsertType operation,
            Supplier<String> codeSupplier
    ) {
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            method.getBody().ifPresent(body -> {
                NodeList<Statement> stmts = body.getStatements();
                for (int i = 0; i < stmts.size(); i++) {
                    Statement stmt = stmts.get(i);
                    if (stmt.getBegin().isEmpty() || stmt.getBegin().get().line != targetLine) continue;

                    applyOperation(stmts, i, stmt, operation, codeSupplier);
                }
            });
        });
    }

    private static void applyOperation(
            NodeList<Statement> stmts,
            int index,
            Statement stmt,
            ChunkManager.InsertType operation,
            Supplier<String> codeSupplier
    ) {
        String newCode = codeSupplier.get().trim();
        try {
            switch (operation) {
                case INSERT_AFTER : {
                    BlockStmt block = parser.parseBlock("{" + ensureSemicolon(newCode) + "}")
                            .getResult().orElseThrow(() -> new RuntimeException("插入代码解析失败"));
                    stmts.addAll(index + 1, block.getStatements());
                    break;
                }
                case REPLACE_ASSIGN_RIGHT :
                    replaceAssignmentRight(stmt, newCode);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException("操作失败，代码内容:\n" + newCode, e);
        }
    }

    private static void replaceAssignmentRight(Statement stmt, String newCode) {
        if (!stmt.isExpressionStmt()) throw new RuntimeException("目标行非表达式语句");

        ExpressionStmt exprStmt = stmt.asExpressionStmt();
        Expression expr = exprStmt.getExpression();

        Expression newRightExpr = parser.parseExpression(newCode).getResult()
                .orElseThrow(() -> new RuntimeException("新右边表达式解析失败"));

        if (expr.isVariableDeclarationExpr()) {
            var varDecl = expr.asVariableDeclarationExpr();
            if (varDecl.getVariables().size() != 1) {
                throw new RuntimeException("变量声明包含多个变量，不支持");
            }
            varDecl.getVariable(0).setInitializer(newRightExpr);
        } else if (expr.isAssignExpr()) {
            expr.asAssignExpr().setValue(newRightExpr);
        } else {
            throw new RuntimeException("当前语句不是赋值或变量声明语句");
        }
    }

    private static String ensureSemicolon(String code) {
        code = code.trim();
        return code.endsWith(";") ? code : code + ";";
    }

    public static String modify(
            String sourceCode,
            int targetLine,
            ChunkManager.InsertType operation,
            Supplier<String> codeSupplier,
            Set<String> importsToAdd
    ) {
        CompilationUnit cu = parseSourceCode(sourceCode);
        addImports(cu, importsToAdd);
        processMethodStatements(cu, targetLine, operation, codeSupplier);
        return cu.toString();
    }
}