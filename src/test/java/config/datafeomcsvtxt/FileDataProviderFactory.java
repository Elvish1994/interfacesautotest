// FileDataProviderFactory.java
package config.datafeomcsvtxt;

import java.util.List;
import java.util.Map;

/**
 * 文件数据提供者接口
 */
public interface FileDataProviderFactory {
    /**
     * 从CSV文件读取测试数据
     * @param fileName CSV文件名
     * @return 测试数据列表
     */
    List<Map<String, Object>> readFromCSV(String fileName);
    
    /**
     * 从TXT文件读取测试数据
     * @param fileName TXT文件名
     * @return 测试数据列表
     */
    List<Map<String, Object>> readFromTXT(String fileName);
    
    /**
     * 从CSV文件读取测试数据（带自定义分隔符）
     * @param fileName CSV文件名
     * @param delimiter 分隔符
     * @return 测试数据列表
     */
    List<Map<String, Object>> readFromCSV(String fileName, String delimiter);
    
    /**
     * 从TXT文件读取测试数据（带自定义分隔符）
     * @param fileName TXT文件名
     * @param delimiter 分隔符
     * @return 测试数据列表
     */
    List<Map<String, Object>> readFromTXT(String fileName, String delimiter);
    
    /**
     * 根据文件扩展名自动选择读取方法
     * @param fileName 文件名
     * @return 测试数据列表
     */
    List<Map<String, Object>> readFromFile(String fileName);
}
