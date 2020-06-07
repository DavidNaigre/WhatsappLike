package function.user;

import function.ProcessRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public static boolean getParameters(String id) {
        Map<String, String> hm = new HashMap<>();
        hm.put( "identifiant",id);
        String serv = ProcessRequest.start(hm,"information");

        JSONObject response = new JSONObject(serv);
        try {
            String param_identite = response.getString("identite");
            String param_mail2 = response.getString("mail");
            String param_id2 = response.getString("identifiant");
            User.setIdentifiant(param_identite);
            User.setMail(param_mail2);
            User.setId(param_id2);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
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

    public static boolean delRelation(String email) {
        Map<String, String> hm = new HashMap<>();
        try {
            hm.put("identifiant", User.getId());
            hm.put("mail", email);
            String serv = ProcessRequest.start(hm, "delier");
            JSONObject response = new JSONObject(serv);

            String param_message = response.getJSONObject("etat").getString("message");
            return param_message.contains("OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
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

    public static Boolean sendMessage(String relation_id, String message) {
        Map<String, String> hm = new HashMap<>();
        try {
            message = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            hm.put("identifiant", User.getId());
            hm.put("relation", relation_id);
            hm.put("message", message);
            String serv = ProcessRequest.start(hm, "ecrire");
            JSONObject response = new JSONObject(serv);

            String param_message = response.getJSONObject("etat").getString("message");
            return param_message.contains("OK");
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
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
            for( Iterator<Object> i = responseArray.iterator(); i.hasNext();){
                JSONObject item = (JSONObject) i.next();
                messageArray.add(new ArrayList<>(){{
                    add(item.getString("identite"));
                    add(URLDecoder.decode(item.getString("message"), StandardCharsets.UTF_8.toString()));
                }});
            }
//            Collections.reverse(messageArray);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return messageArray;
    }
}
