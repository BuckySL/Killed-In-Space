package game.component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom JSON parser implementation to avoid external library dependencies
 */
public class CustomJsonParser {
    
    /**
     * Parse a simple JSON string and extract key-value pairs
     * @param jsonStr The JSON string to parse
     * @return A Map containing the extracted key-value pairs
     */
    public static Map<String, Object> parseJson(String jsonStr) {
        Map<String, Object> result = new HashMap<>();
        
        // Remove the outer braces and whitespace
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{")) {
            jsonStr = jsonStr.substring(1);
        }
        if (jsonStr.endsWith("}")) {
            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        }
        
        // Pattern to match string values (key:"value")
        Pattern stringPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
        Matcher stringMatcher = stringPattern.matcher(jsonStr);
        
        while (stringMatcher.find()) {
            String key = stringMatcher.group(1);
            String value = stringMatcher.group(2);
            result.put(key, value);
        }
        
        // Pattern to match numeric values (key:123)
        Pattern numPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(-?\\d+(\\.\\d+)?)");
        Matcher numMatcher = numPattern.matcher(jsonStr);
        
        while (numMatcher.find()) {
            String key = numMatcher.group(1);
            String valueStr = numMatcher.group(2);
            
            // Convert to appropriate numeric type
            if (valueStr.contains(".")) {
                result.put(key, Double.parseDouble(valueStr));
            } else {
                result.put(key, Integer.parseInt(valueStr));
            }
        }
        
        // Pattern to match boolean values (key:true or key:false)
        Pattern boolPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(true|false)");
        Matcher boolMatcher = boolPattern.matcher(jsonStr);
        
        while (boolMatcher.find()) {
            String key = boolMatcher.group(1);
            boolean value = Boolean.parseBoolean(boolMatcher.group(2));
            result.put(key, value);
        }
        
        return result;
    }
    
    /**
     * Create a JSON string from a Map
     * @param data The Map containing key-value pairs
     * @return A JSON formatted string
     */
    public static String createJson(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            
            Object value = entry.getValue();
            sb.append("\"").append(entry.getKey()).append("\":");
            
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                // Numbers, booleans don't need quotes
                sb.append(value);
            }
            
            first = false;
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Get a string value from the parsed JSON map
     * @param json The parsed JSON map
     * @param key The key to retrieve
     * @return The string value or empty string if not found
     */
    public static String getString(Map<String, Object> json, String key) {
        Object value = json.get(key);
        return value != null ? value.toString() : "";
    }
    
    /**
     * Get an integer value from the parsed JSON map
     * @param json The parsed JSON map
     * @param key The key to retrieve
     * @return The integer value or 0 if not found
     */
    public static int getInt(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Get a boolean value from the parsed JSON map
     * @param json The parsed JSON map
     * @param key The key to retrieve
     * @return The boolean value or false if not found
     */
    public static boolean getBoolean(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }
    
    /**
     * Get a double value from the parsed JSON map
     * @param json The parsed JSON map
     * @param key The key to retrieve
     * @return The double value or 0.0 if not found
     */
    public static double getDouble(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}