package function.user;

import function.ProcessRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserTemp {
    private static String identifiant, mail, id;

    public static boolean checkParameters(String newid) throws IOException {
        Map<String, String> hm = new HashMap<>();
        hm.put( "identifiant",newid);
        String serv = ProcessRequest.start(hm,"information");

        JSONObject response = new JSONObject(serv);
        try {
            String param_identite = response.getString("identite");
            String param_mail = response.getString("mail");
            String param_id = response.getString("identifiant");
            if (param_id.equals(newid) && param_identite.equals(identifiant) && param_mail.equals(mail)){
                identifiant = param_id;
                return true;
            } else return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setId(String id) { UserTemp.id = id; }
    public static void setIdentifiant(String identifiant) {
        UserTemp.identifiant = identifiant;
    }
    public static void setMail(String mail) {
        UserTemp.mail = mail;
    }
    public static String getId() {
        return id;
    }
    public static String getIdentifiant() {
        return identifiant;
    }
    public static String getMail() {
        return mail;
    }
}
