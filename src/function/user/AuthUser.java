package function.user;
import function.appPath;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class AuthUser {
    private static final String filePath = appPath.getAuthDIRECTORY()+"auth.json";

    public static boolean register (String id, String name, String mail){
        JSONObject obj = new JSONObject();
        obj.put("id",id);
        obj.put("name",name);
        obj.put("mail",mail);
        JSONArray userInfo = new JSONArray();
        userInfo.put(obj);
        boolean r = false;
        try(FileWriter file = new FileWriter(filePath)) {
            file.write(obj.toString());
            file.flush();
            User.setId(id);
            User.setIdentifiant(name);
            User.setMail(mail);
            r = true;
        }
        finally {
            return r;
        }
    }
    public static boolean register (){
        return register(UserTemp.getId(), UserTemp.getIdentifiant(), UserTemp.getMail());
    }

    public static boolean isAuth(){
        JSONParser parser = new JSONParser();
        boolean r = false;
        try(FileReader file = new FileReader(filePath)){
            Object obj = parser.parse(file);
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.isEmpty()) return false;
            else{
                User.setId((String) jsonObject.get("id"));
                User.setIdentifiant((String) jsonObject.get("name"));
                User.setMail((String) jsonObject.get("mail"));
                r = true;
            }
        }finally {
            return r;
        }
    }
}
