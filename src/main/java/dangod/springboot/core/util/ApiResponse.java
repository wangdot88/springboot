package dangod.springboot.core.util;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse extends HashMap<String, Object> {
    
    public static ApiResponse success() {
        ApiResponse response = new ApiResponse();
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    public static ApiResponse success(Object data) {
        ApiResponse response = success();
        response.put("data", data);
        return response;
    }
    
    public static ApiResponse success(String message, Object data) {
        ApiResponse response = success(data);
        response.put("message", message);
        return response;
    }
    
    public static ApiResponse error(String message) {
        ApiResponse response = new ApiResponse();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    public ApiResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}