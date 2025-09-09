package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties;

    public ConfigLoader(String env) {
        properties = new Properties();
        String fileName = env + ".properties";
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/" + fileName)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("无法加载配置文件: " + fileName, e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}