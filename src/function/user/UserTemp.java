package function.user;

import function.ProcessRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserTemp {
    private static String identifiant, mail, id;

    public static boolean checkParameters(String newid) {
        Map<String, String> hm = new HashMap<>();
        hm.put( "identifiant",newid);
        String serv = ProcessRequest.start(hm,"information");

        JSONObject response = new JSONObject(serv);
        try {
            String param_identite = response.getString("identite");
            String param_mail = response.getString("mail");
            String param_id = response.getString("identifiant");
            if (param_id.equals(newid) && param_identite.equals(identifiant) && param_mail.equals(mail)){
                id = param_id;
                return true;
            } else return false;
        } catch (JSONException e) {
            System.out.println("Identifiant saisie incorrect");
        }
        return false;
    }

    public static boolean setParameters(String newid) {
        Map<String, String> hm = new HashMap<>();
        hm.put( "identifiant",newid);
        String serv = ProcessRequest.start(hm,"information");

        JSONObject response = new JSONObject(serv);
        try {
            id = response.getString("identite");
            mail = response.getString("mail");
            identifiant = response.getString("identifiant");
            return true;

        } catch (JSONException e) {
            System.out.println("Identifiant saisie incorrect");
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
