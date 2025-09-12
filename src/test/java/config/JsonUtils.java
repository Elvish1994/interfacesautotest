package config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON 工具类，用于 Java 对象与 JSON 字符串之间的转换
 */
public class JsonUtils {
    // 使用单例模式或静态实例
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将 Java 对象序列化为 JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定的 Java 对象（基础类型，如 User.class）
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为复杂类型，如 List<User>、Map<String, Object> 等
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转泛型对象失败", e);
        }
    }

    /**
     * 验证 JSON 字符串是否有效
     */
    public static boolean isValidJson(String json) {
        try {
            mapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
