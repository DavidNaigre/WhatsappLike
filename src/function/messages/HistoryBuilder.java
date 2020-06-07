package function.messages;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HistoryBuilder {
    public static ArrayList<ArrayList<String>> read(String contactID){
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try(FileReader file = new FileReader("src/function/messages/History/"+contactID+".json"))  {
            Object obj = parser.parse(file);
            JSONArray jsonArray = (JSONArray) obj;
            for(Object jsonData: jsonArray){
                JSONObject tmpObj = new JSONObject((Map) jsonData);
                data.add(new ArrayList<>(){{
                    add(tmpObj.get("date").toString());
                    add(tmpObj.get("from").toString());
                    add(tmpObj.get("message").toString());
                }});
            }
        } catch (IOException | ParseException e) {
            System.out.println("Could not load history file: illegal structure");
        }
        return data;
    }

    public static boolean write(String contactID, String from, String message) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        JSONArray historyArray = new JSONArray();
        try {
            JSONParser parser = new JSONParser();
            try (FileReader file = new FileReader("src/function/messages/History/" + contactID + ".json")) {
                Object obj = parser.parse(file);
                JSONArray jsonArray = (JSONArray) obj;
                if (!jsonArray.isEmpty()) historyArray = jsonArray;
            } catch (FileNotFoundException e) {
                System.out.println(contactID + ".json will be created");
            }

            JSONObject obj = new JSONObject();
            obj.put("date", formatter.format(date));
            obj.put("from", from);
            obj.put("message", message);
            historyArray.add(obj);

            try (FileWriter file = new FileWriter("src/function/messages/History/" + contactID + ".json", false)) {
                file.write(historyArray.toString());
                file.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<String> getContactList(){
        ArrayList<String> result = new ArrayList<>();
        File[] files = new File("src/function/messages/history").listFiles();
        for (File file: files != null ? files : new File[0] ) result.add(file.getName().substring(0, file.getName().length() - 5));
        return result;
    }
}
