// AdvancedFileDataProvider.java
package config.datafeomcsvtxt.imp;


import config.datafeomcsvtxt.FileDataProviderFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 高级文件数据提供者，支持更多功能
 */
public class AdvancedFileDataProvider implements FileDataProviderFactory {

    private static final String DEFAULT_CSV_DELIMITER = ",";
    private static final String DEFAULT_TXT_DELIMITER = "\\|";
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Override
    public List<Map<String, Object>> readFromCSV(String fileName) {
        return readFromCSV(fileName, DEFAULT_CSV_DELIMITER);
    }

    @Override
    public List<Map<String, Object>> readFromTXT(String fileName) {
        return readFromTXT(fileName, DEFAULT_TXT_DELIMITER);
    }

    @Override
    public List<Map<String, Object>> readFromCSV(String fileName, String delimiter) {
        return readFile(fileName, delimiter, true, DEFAULT_ENCODING);
    }

    @Override
    public List<Map<String, Object>> readFromTXT(String fileName, String delimiter) {
        return readFile(fileName, delimiter, true, DEFAULT_ENCODING);
    }

    @Override
    public List<Map<String, Object>> readFromFile(String fileName) {
        return readFromFile(fileName, DEFAULT_ENCODING);
    }

    /**
     * 从文件读取数据（指定编码）
     * @param fileName 文件名
     * @param encoding 文件编码
     * @return 数据列表
     */
    public List<Map<String, Object>> readFromFile(String fileName, String encoding) {
        if (fileName.endsWith(".csv")) {
            return readFromCSV(fileName, DEFAULT_CSV_DELIMITER, encoding);
        } else if (fileName.endsWith(".txt")) {
            return readFromTXT(fileName, DEFAULT_TXT_DELIMITER, encoding);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    /**
     * 从CSV文件读取数据（指定分隔符和编码）
     * @param fileName 文件名
     * @param delimiter 分隔符
     * @param encoding 编码
     * @return 数据列表
     */
    public List<Map<String, Object>> readFromCSV(String fileName, String delimiter, String encoding) {
        return readFile(fileName, delimiter, true, encoding);
    }

    /**
     * 从TXT文件读取数据（指定分隔符和编码）
     * @param fileName 文件名
     * @param delimiter 分隔符
     * @param encoding 编码
     * @return 数据列表
     */
    public List<Map<String, Object>> readFromTXT(String fileName, String delimiter, String encoding) {
        return readFile(fileName, delimiter, true, encoding);
    }

    /**
     * 读取文件的核心方法
     * @param fileName 文件名
     * @param delimiter 分隔符
     * @param hasHeader 是否包含表头
     * @param encoding 文件编码
     * @return 数据列表
     */
    private List<Map<String, Object>> readFile(String fileName, String delimiter, boolean hasHeader, String encoding) {
        List<Map<String, Object>> result = new ArrayList<>();
        InputStream inputStream = null;

        try {
            // 首先尝试从classpath加载
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            // 如果classpath中没有，则尝试从文件系统加载
            if (inputStream == null) {
                inputStream = new FileInputStream(fileName);
            }

            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(encoding)));
            String line;
            String[] headers = null;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 跳过空行和注释行（以#开头）
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                // 解析行数据
                String[] values = splitLine(line, delimiter);

                // 处理表头
                if (hasHeader && lineNumber == 1) {
                    headers = values;
                    continue;
                }

                // 构建数据映射
                Map<String, Object> rowData = new HashMap<>();
                if (headers != null) {
                    // 使用表头作为键
                    for (int i = 0; i < headers.length; i++) {
                        String key = headers[i].trim();
                        String value = i < values.length ? values[i].trim() : "";
                        rowData.put(key, convertValue(value));
                    }
                } else {
                    // 使用索引作为键
                    for (int i = 0; i < values.length; i++) {
                        rowData.put("column" + i, convertValue(values[i].trim()));
                    }
                }

                result.add(rowData);
            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                }
            }
        }

        return result;
    }

    /**
     * 分割行数据（处理引号包围的字段）
     * @param line 行数据
     * @param delimiter 分隔符
     * @return 分割后的字段数组
     */
    private String[] splitLine(String line, String delimiter) {
        // 如果分隔符是正则表达式，直接使用split
        if (Pattern.compile("[\\[\\](){}|+?.*^$\\\\]").matcher(delimiter).find()) {
            return line.split(delimiter, -1);
        }

        // 处理CSV格式的引号包围字段
        if (",".equals(delimiter)) {
            return parseCSVLine(line);
        }

        // 其他情况使用普通分割
        return line.split(Pattern.quote(delimiter), -1);
    }

    /**
     * 解析CSV行（处理引号）
     * @param line CSV行
     * @return 字段数组
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 双引号转义
                    currentField.append('"');
                    i++; // 跳过下一个引号
                } else {
                    // 切换引号状态
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 字段分隔符
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * 转换值类型
     * @param value 原始值
     * @return 转换后的值
     */
    private Object convertValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // 处理引号包围的值
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
            // 处理双引号转义
            value = value.replace("\"\"", "\"");
        }

        // 处理特殊值
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }

        if ("empty".equalsIgnoreCase(value)) {
            return "";
        }

        // 尝试转换为数字
        if (isNumeric(value)) {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return Long.parseLong(value);
                }
            }
        }

        // 尝试转换为布尔值
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        // 默认返回字符串
        return value;
    }

    /**
     * 判断字符串是否为数字
     * @param str 字符串
     * @return 是否为数字
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
