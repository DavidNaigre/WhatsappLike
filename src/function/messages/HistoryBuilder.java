package function.messages;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryBuilder {

    public static void write(String to, Integer Personne, String message){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/function/messages/History/"+to+".csv",true))){
//            StringBuilder sb = new StringBuilder();
//            String[] data = {Personne.toString(),formatter.format(date), message};
//            for(String index : data){
//                sb.append(index);
//                sb.append("; ");
//            }
//            sb.append("\n");
            String data = Personne.toString()+";"+formatter.format(date)+";"+message+" \n";
            writer.write(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String>> read(String to){
        int i = 0;
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        try(BufferedReader buf_read = new BufferedReader(new InputStreamReader(new FileInputStream("src/function/messages/History/"+to+".csv")))){
            String line;
            while ((line = buf_read.readLine()) != null){
                data.add(new ArrayList<>());
                String[] FillColumn = line.split("\t");
                for ( String s : FillColumn ) data.get(i).add(s);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
