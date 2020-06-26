package function;

import java.util.Map;

public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for ( Map.Entry<String, String> entry : params.entrySet() ) {
            String s = entry.getKey() + "=" + entry.getValue() + "&";
            sb.append(s);
        }
        String result = sb.toString();
        return (result.charAt(result.length() - 1) == '&') ? result.substring(0, result.length() - 1) : result;
    }
}
