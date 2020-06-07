package function;

import java.util.Map;

public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
            result.append("&");
        }
//        System.out.println(result.toString());
        return result.toString().charAt(result.toString().length()-1) == '&' ? result.toString().substring(0,result.toString().length()-1) : result.toString();
    }
}
