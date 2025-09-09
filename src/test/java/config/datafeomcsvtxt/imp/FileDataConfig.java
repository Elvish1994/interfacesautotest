// FileDataConfig.java
package config.datafeomcsvtxt.imp;

import java.io.InputStream;
import java.util.Properties;

/**
 * 文件数据配置管理类
 */
public class FileDataConfig {
    private static Properties properties = new Properties();

    static {
        try {
            InputStream input = FileDataConfig.class.getClassLoader().getResourceAsStream("filedata.properties");
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            // 如果配置文件不存在，使用默认配置
            properties.setProperty("file.data.directory", "testdata/");
            properties.setProperty("file.encoding", "UTF-8");
        }
    }

    public static String getDataDirectory() {
        return properties.getProperty("file.data.directory", "testdata/");
    }

    public static String getFileEncoding() {
        return properties.getProperty("file.encoding", "UTF-8");
    }

    public static String getFilePath(String fileName) {
        String directory = getDataDirectory();
        if (!directory.endsWith("/")) {
            directory += "/";
        }
        return directory + fileName;
    }
}
