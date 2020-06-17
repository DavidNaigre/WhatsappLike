import function.allMatches;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String sentence = "Un message avec en _gras_ un autre qui ne l'est pas _testons_ c'est pas fini";
        Pattern regexKey = Pattern.compile("_(?!\\s)[a-zA-Z0-9 ëËêÊéÉÈèâÂäÄàÀ&\\/^#$,:.;|?!§*\\+\\-\\(){}]+_");

        HashMap<Integer,Integer> foundMatch = new HashMap<>();
        HashMap<Integer,Integer> cuttingMap = new HashMap<>();
        HashMap<Integer, Text> textMap = new HashMap<>();
        ArrayList<ArrayList<String>> al = new ArrayList<>();

//        System.out.println(match.group() + " at " + match.start()+ " end " + match.end());
        for (MatchResult match : allMatches.allMatches(Pattern.compile(String.valueOf(regexKey)), sentence) ) foundMatch.put(match.start(), match.end());

        int startval=0;
        for(HashMap.Entry<Integer,Integer> submap: foundMatch.entrySet()) {
            if (submap.getKey()-1 > 0) {
                cuttingMap.put(startval, submap.getKey());
                startval = submap.getValue() + 1;
            }
            al.add(new ArrayList<>(){{
                add(String.valueOf(submap.getKey()));
                add(sentence.substring(submap.getKey()+1, submap.getValue()-1));
            }});
//            Text newtext =  new Text(sentence.substring(submap.getKey()+1, submap.getValue()-1));
//            newtext.getStyleClass().add("untouched-text");
//            textMap.put(submap.getKey(),newtext);
        }

        if(startval < sentence.length()) cuttingMap.put(startval, sentence.length());
        System.out.println(cuttingMap.toString());


        for(HashMap.Entry<Integer, Integer> map: cuttingMap.entrySet()){
//            Text newtext =  new Text(sentence.substring(map.getKey(), map.getValue()));
//            newtext.getStyleClass().add("untouched-text");
//            textMap.put(map.getKey(),newtext);
            al.add(new ArrayList<>(){{
                add(String.valueOf(map.getKey()));
                add(sentence.substring(map.getKey(), map.getValue()));
            }});
        }
        Map<Integer, Text> sortedMap = new TreeMap<>(textMap);
        System.out.println(al);
        System.out.println(("012345").substring(0,1));
    }
}
