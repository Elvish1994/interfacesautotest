package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 根据 JSON 字符串自动生成 Java POJO 类文件，并保存到指定目录
 */
public class JsonToJavaGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    // 控制是否生成特定方法的标志
    private static boolean generateToString = true;
    private static boolean generateEqualsAndHashCode = true;

    // 存储所有需要生成的类定义
    private static Map<String, JsonNode> classDefinitions = new HashMap<>();
    private static Set<String> processedClasses = new HashSet<>();

    /**
     * 设置是否生成toString方法
     * @param generate true表示生成，false表示不生成
     */
    public static void setGenerateToString(boolean generate) {
        generateToString = generate;
    }

    /**
     * 设置是否生成equals和hashCode方法
     * @param generate true表示生成，false表示不生成
     */
    public static void setGenerateEqualsAndHashCode(boolean generate) {
        generateEqualsAndHashCode = generate;
    }

    /**
     * 主入口：根据 JSON 字符串生成 Java 类并保存到指定目录
     *
     * @param json          JSON 字符串
     * @param className     生成的主类名
     * @param packageName   包名
     * @param outputDir     输出目录
     */
    public static void generate(String json, String className, String packageName, String outputDir) {
        try {
            JsonNode rootNode = mapper.readTree(json);

            // 重置状态
            classDefinitions.clear();
            processedClasses.clear();

            // 收集所有类定义
            collectClassDefinitions(className, rootNode);

            // 生成所有类文件
            for (Map.Entry<String, JsonNode> entry : classDefinitions.entrySet()) {
                String clsName = entry.getKey();
                JsonNode node = entry.getValue();

                StringBuilder code = new StringBuilder();

                // 生成 package 和 import
                code.append("package ").append(packageName).append(";\n\n");

                // 根据需要添加导入语句
                boolean needListImport = hasListField(node);
                boolean needObjectsImport = generateEqualsAndHashCode;

                if (needListImport) {
                    code.append("import java.util.List;\n");
                }
                if (needObjectsImport) {
                    code.append("import java.util.Objects;\n");
                }
                if (needListImport || needObjectsImport) {
                    code.append("\n");
                }

                // 生成类
                generateClass(code, clsName, node);

                // 创建文件并写入
                String fileName = clsName + ".java";
                String fullPackageNamePath = packageName.replace('.', '/');
                Path dirPath = Paths.get(outputDir, fullPackageNamePath);
                Path filePath = dirPath.resolve(fileName);

                // 创建目录（如果不存在）
                Files.createDirectories(dirPath);

                // 写入文件
                try (PrintWriter writer = new PrintWriter(filePath.toFile(), "UTF-8")) {
                    writer.print(code.toString());
                }

                System.out.println("✅ Java 类已生成: " + filePath.toAbsolutePath());
            }

        } catch (IOException e) {
            throw new RuntimeException("生成 Java 类失败", e);
        }
    }

    /**
     * 检查是否有List类型的字段
     */
    private static boolean hasListField(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode valueNode = field.getValue();
            if (valueNode.isArray()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 收集所有需要生成的类定义
     */
    private static void collectClassDefinitions(String className, JsonNode node) {
        if (processedClasses.contains(className)) {
            return;
        }

        processedClasses.add(className);
        classDefinitions.put(className, node);

        // 遍历所有字段，收集嵌套对象
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode valueNode = field.getValue();

            if (valueNode.isObject() && !valueNode.isArray()) {
                String nestedClassName = capitalize(fieldName);
                if (!processedClasses.contains(nestedClassName)) {
                    collectClassDefinitions(nestedClassName, valueNode);
                }
            } else if (valueNode.isArray() && valueNode.size() > 0 && valueNode.get(0).isObject()) {
                JsonNode firstElement = valueNode.get(0);
                String listClassName = capitalize(fieldName);
                if (!processedClasses.contains(listClassName)) {
                    collectClassDefinitions(listClassName, firstElement);
                }
            }
        }
    }

    /**
     * 生成单个类定义
     */
    private static void generateClass(StringBuilder code, String className, JsonNode node) {
        code.append("public class ").append(className).append(" {\n\n");

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        List<String> fieldNamesList = new ArrayList<>();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode valueNode = field.getValue();

            String javaType = getJavaType(valueNode, capitalize(fieldName));
            String javaFieldName = sanitizeFieldName(fieldName);

            fieldNamesList.add(javaFieldName);

            // 字段
            code.append("    private ").append(javaType).append(" ").append(javaFieldName).append(";\n\n");

            // Getter
            String returnType = javaType;
            String methodName = "get" + capitalize(javaFieldName);
            if (javaType.equals("boolean") || javaType.equals("Boolean")) {
                methodName = "is" + capitalize(javaFieldName);
            }

            code.append(String.format("    public %s %s() {\n", returnType, methodName));
            code.append("        return this.").append(javaFieldName).append(";\n");
            code.append("    }\n\n");

            // Setter
            code.append("    public void set").append(capitalize(javaFieldName)).append("( ").append(javaType).append(" ").append(javaFieldName).append(" ) {\n");
            code.append("        this.").append(javaFieldName).append(" = ").append(javaFieldName).append(";\n");
            code.append("    }\n\n");
        }

        // equals和hashCode方法
        if (generateEqualsAndHashCode) {
            // equals方法
            code.append("    @Override\n");
            code.append("    public boolean equals(Object o) {\n");
            code.append("        if (this == o) return true;\n");
            code.append("        if (!(o instanceof ").append(className).append(")) return false;\n");
            code.append("        ").append(className).append(" that = (").append(className).append(") o;\n");

            if (!fieldNamesList.isEmpty()) {
                StringBuilder equalsBuilder = new StringBuilder();
                for (int i = 0; i < fieldNamesList.size(); i++) {
                    if (i > 0) equalsBuilder.append(" && ");
                    equalsBuilder.append("Objects.equals(").append(fieldNamesList.get(i)).append(", that.").append(fieldNamesList.get(i)).append(")");
                }
                code.append("        return ").append(equalsBuilder.toString()).append(";\n");
            } else {
                code.append("        return true;\n");
            }
            code.append("    }\n\n");

            // hashCode方法
            code.append("    @Override\n");
            code.append("    public int hashCode() {\n");
            if (!fieldNamesList.isEmpty()) {
                code.append("        return Objects.hash(");
                for (int i = 0; i < fieldNamesList.size(); i++) {
                    if (i > 0) code.append(", ");
                    code.append(fieldNamesList.get(i));
                }
                code.append(");\n");
            } else {
                code.append("        return 1;\n");
            }
            code.append("    }\n\n");
        }

        // toString方法
        if (generateToString) {
            code.append("    @Override\n");
            code.append("    public String toString() {\n");
            code.append("        return \"").append(className).append("{\" +\n");
            for (int i = 0; i < fieldNamesList.size(); i++) {
                String fname = fieldNamesList.get(i);
                code.append("                \"").append(fname).append("='\" + ").append(fname).append(" + \"'");
                if (i < fieldNamesList.size() - 1) {
                    code.append(" + \", \"");
                }
                code.append(" +\n");
            }
            code.append("                \"}\";\n");
            code.append("    }\n\n");
        }

        code.append("}\n");
    }

    /**
     * 推断 Java 类型
     */
    private static String getJavaType(JsonNode node, String fallbackName) {
        if (node.isNull()) return "Object";

        if (node.isTextual()) return "String";
        if (node.isBoolean()) return "boolean";
        if (node.isInt()) return "int";
        if (node.isLong()) return "long";
        if (node.isDouble()) return "double";
        if (node.isFloat()) return "float";

        if (node.isArray()) {
            if (node.size() == 0) return "List<Object>";
            JsonNode first = node.get(0);
            if (first.isObject()) {
                return "List<" + capitalize(fallbackName) + ">";
            } else {
                String elementType = getJavaType(first, fallbackName + "Item");
                return "List<" + elementType + ">";
            }
        }

        if (node.isObject()) {
            return capitalize(fallbackName);
        }

        return "Object";
    }

    // 工具方法
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String sanitizeFieldName(String fieldName) {
        // 简单处理关键字冲突
        if ("class".equals(fieldName) || "package".equals(fieldName)) {
            return "_" + fieldName;
        }
        return fieldName.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}
