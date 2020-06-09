package function.user;

import function.ProcessRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAction {
    public static boolean createAccount(String identifiant, String email){
        Map<String, String> hm = new HashMap<>();
        hm.put( "identite",identifiant);
        hm.put( "mail",email);
        try {
            String serv = ProcessRequest.start(hm,"inscription");
            JSONObject response = new JSONObject(serv);
            String param_message = response.getJSONObject("etat").getString("message");
            return param_message.contains("mail");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean newRelation(String email) {
        Map<String, String> hm = new HashMap<>();
        try {
            hm.put("identifiant", User.getId());
            hm.put("mail", email);
            String serv = ProcessRequest.start(hm, "lier");

            JSONObject response = new JSONObject(serv);

            String param_message = response.getJSONObject("etat").getString("message");
            return param_message.contains("OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void delRelation(String email) {
        Map<String, String> hm = new HashMap<>();
        try {
            hm.put("identifiant", User.getId());
            hm.put("mail", email);
            String serv = ProcessRequest.start(hm, "delier");
            JSONObject response = new JSONObject(serv);

            String param_message = response.getJSONObject("etat").getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getListRelation(){
        Map<String, String> hm = new HashMap<>();
        Map<String, String> relationMap = new HashMap<>();
        try {
            hm.put("identifiant", User.getId());
            String serv = ProcessRequest.start(hm, "relations");
            JSONObject response = new JSONObject(serv);

            JSONArray responseArray = response.getJSONArray("relations");
            for ( int i = 0; i < responseArray.length(); i++ ) {
                JSONObject relation = responseArray.getJSONObject(i);
                String relation_id = String.valueOf(relation.getInt("relation"));
                String relation_identite = relation.getString("identite");
                relationMap.put(relation_id, relation_identite);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return relationMap;
    }

    public static void sendMessage(String relation_id, String message) {
        Map<String, String> hm = new HashMap<>();
        try {
            message = URLEncoder.encode(ProcessRequest.encode(message), StandardCharsets.UTF_8);
            hm.put("identifiant", User.getId());
            hm.put("relation", relation_id);
            hm.put("message", message);
            String serv = ProcessRequest.start(hm, "ecrire");
            JSONObject response = new JSONObject(serv);
            String param_message = response.getJSONObject("etat").getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String>> readMessage(String contactID) {
        Map<String, String> hm = new HashMap<>();
        ArrayList<ArrayList<String>> messageArray = new ArrayList<>();
        try {
            hm.put("identifiant", User.getId());
            hm.put("relation", contactID);
            String serv = ProcessRequest.start(hm, "lire");
            JSONObject response = new JSONObject(serv);
            JSONArray responseArray = response.getJSONArray("messages");
            for ( Object o : responseArray ) {
                JSONObject item = (JSONObject) o;
                messageArray.add(new ArrayList<>() {{
                    add(item.getString("identite"));
                    add(URLDecoder.decode(ProcessRequest.decode(item.getString("message")), StandardCharsets.UTF_8));
                }});
            }
//            Collections.reverse(messageArray);
        } catch (JSONException  e) {
            e.printStackTrace();
        }
        return messageArray;
    }
}
