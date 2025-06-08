package io.github.tml.mosaic.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
public class ASMUtil {

    public enum CodeOp {
        REPLACE, INSERT_AFTER, REPLACE_ASSIGN_RIGHT
    }

    public static String modify(
            String sourceCode,
            int targetLine,
            CodeOp operation,
            Supplier<String> codeSupplier,
            Set<String> importsToAdd
    ) {
        JavaParser parser = new JavaParser();

        ParseResult<CompilationUnit> parseResult = parser.parse(sourceCode);
        if (!parseResult.getResult().isPresent()) {
            throw new IllegalArgumentException("无法解析源码");
        }

        CompilationUnit cu = parseResult.getResult().get();

        if (importsToAdd != null) {
            for (String imp : importsToAdd) {
                cu.addImport(imp);
            }
        }

        cu.findAll(MethodDeclaration.class).forEach(method -> {
            method.getBody().ifPresent(body -> {
                NodeList<Statement> stmts = body.getStatements();
                for (int i = 0; i < stmts.size(); i++) {
                    Statement stmt = stmts.get(i);
                    if (stmt.getBegin().isEmpty()) continue;
                    if (stmt.getBegin().get().line != targetLine) continue;

                    String newCode = codeSupplier.get().trim();
                    try {
                        switch (operation) {
                            case REPLACE :
                                Statement newStmt = parser.parseStatement(ensureSemicolon(newCode))
                                        .getResult().orElseThrow(() -> new RuntimeException("替换代码解析失败"));
                                stmts.set(i, newStmt);
                                break;
                            case INSERT_AFTER :
                                BlockStmt block = parser.parseBlock("{" + ensureSemicolon(newCode) + "}")
                                        .getResult().orElseThrow(() -> new RuntimeException("插入代码解析失败"));
                                stmts.addAll(i + 1, block.getStatements());
                                break;
                            case REPLACE_ASSIGN_RIGHT :
                                // 重点替换赋值语句等号右边
                                if (stmt.isExpressionStmt()) {
                                    ExpressionStmt exprStmt = stmt.asExpressionStmt();
                                    Expression expr = exprStmt.getExpression();
                                    if (expr.isVariableDeclarationExpr()) {
                                        // int a = 1;
                                        var varDecl = expr.asVariableDeclarationExpr();
                                        if (varDecl.getVariables().size() == 1) {
                                            var varDeclarator = varDecl.getVariable(0);
                                            // 替换右边表达式
                                            Expression newRightExpr = parser.parseExpression(newCode).getResult()
                                                    .orElseThrow(() -> new RuntimeException("新右边表达式解析失败"));
                                            varDeclarator.setInitializer(newRightExpr);
                                        } else {
                                            throw new RuntimeException("变量声明包含多个变量，不支持");
                                        }
                                    } else if (expr.isAssignExpr()) {
                                        // a = 1;
                                        AssignExpr assignExpr = expr.asAssignExpr();
                                        Expression newRightExpr = parser.parseExpression(newCode).getResult()
                                                .orElseThrow(() -> new RuntimeException("新右边表达式解析失败"));
                                        assignExpr.setValue(newRightExpr);
                                    } else {
                                        throw new RuntimeException("当前语句不是赋值或变量声明语句");
                                    }
                                } else {
                                    throw new RuntimeException("目标行非表达式语句");
                                }
                            }
                            break;
                    } catch (Exception e) {
                        throw new RuntimeException("操作失败，代码内容:\n" + newCode, e);
                    }
                }
            });
        });

        return cu.toString();
    }

    private static String ensureSemicolon(String code) {
        code = code.trim();
        if (!code.endsWith(";") && !code.endsWith("}") && !code.endsWith("{")) {
            code += ";";
        }
        return code;
    }
}