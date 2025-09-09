// FileDataProvider.java
package config.datafeomcsvtxt.imp;


import config.datafeomcsvtxt.FileDataProviderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件数据提供者实现类
 */
public class FileDataProvider implements FileDataProviderFactory {

    private static final String DEFAULT_CSV_DELIMITER = ",";
    private static final String DEFAULT_TXT_DELIMITER = "\\|"; // 默认使用竖线分隔

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
        return readFile(fileName, delimiter, true);
    }

    @Override
    public List<Map<String, Object>> readFromTXT(String fileName, String delimiter) {
        return readFile(fileName, delimiter, true);
    }

    @Override
    public List<Map<String, Object>> readFromFile(String fileName) {
        if (fileName.endsWith(".csv")) {
            return readFromCSV(fileName);
        } else if (fileName.endsWith(".txt")) {
            return readFromTXT(fileName);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    /**
     * 读取文件的核心方法
     * @param fileName 文件名
     * @param delimiter 分隔符
     * @param hasHeader 是否包含表头
     * @return 数据列表
     */
    private List<Map<String, Object>> readFile(String fileName, String delimiter, boolean hasHeader) {
        List<Map<String, Object>> result = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new RuntimeException("File not found: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String[] headers = null;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 跳过空行
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 解析行数据
                String[] values = line.split(delimiter, -1); // -1保留空值

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

        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }

        return result;
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
