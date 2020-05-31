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
    public static ArrayList<ArrayList<String>> read(String to){
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();

        try(FileReader file = new FileReader("src/function/messages/History/"+to+".json"))  {
            Object obj = parser.parse(file);
            jsonArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        for(Object jsonData: jsonArray){
            JSONObject tmpObj = new JSONObject((Map) jsonData);
            data.add(new ArrayList<>(){{
                add(tmpObj.get("date").toString());
                add(tmpObj.get("from").toString());
                add(tmpObj.get("message").toString());
            }});
        }
        return data;
    }

    public static void write(String from, String to , String message) throws IOException, ParseException{
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        JSONParser parser = new JSONParser();
        JSONArray historyArray = new JSONArray();

        try(FileReader file = new FileReader("src/function/messages/History/"+to+".json")){
            Object obj = parser.parse(file);
            JSONArray jsonArray = (JSONArray) obj;
            if(!jsonArray.isEmpty()) historyArray = jsonArray;
        } catch (FileNotFoundException e){
            System.out.println(to+".json will be created");
        }

        JSONObject obj = new JSONObject();
        obj.put("date",formatter.format(date));
        obj.put("from",from);
        obj.put("message",message);
        historyArray.add(obj);

        try(FileWriter file = new FileWriter("src/function/messages/History/"+to+".json",false)) {
            file.write(historyArray.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getContactList(){
        ArrayList<String> result = new ArrayList<>();
        File[] files = new File("src/function/messages/history").listFiles();
        for (File file: files) if (file.isFile()) result.add(file.getName().substring(0, file.getName().length() - 5));
        return result;
    }
}
