package function;

import java.util.Map;
import java.util.stream.Collectors;

public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) {
        String result = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue() + "&").collect(Collectors.joining());
        return (result.charAt(result.length() - 1) == '&') ? result.substring(0, result.length() - 1) : result;
    }
}
