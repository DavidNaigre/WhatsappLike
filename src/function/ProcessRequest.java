package function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ProcessRequest {
    private static HttpURLConnection con;

    public synchronized static String start (Map<String, String> parameters, String action) {
        String urlParameters = ParameterStringBuilder.getParamsString(parameters);
        String inputLine;
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL("https://trankillprojets.fr/wal/wal.php?" + action + "&" + urlParameters);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();
            if(status > 299){
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            }
            return content.toString();
        }
        catch (IOException e) {
            return String.valueOf(e);
        }
        finally {
            con.disconnect();
        }
    }
}
