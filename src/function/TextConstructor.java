package function;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class TextConstructor {
    private static final Pattern regexPattern = Pattern.compile("_(?!\\s)[a-zA-Z0-9 çëËêÊéÉÈèâÂäÄàÀ&\\/^#$,:.;|?!§*\\+\\-\\(){}]+_");

    public static TextFlow BOLD (String sentence) {
        int startval = 0;
        HashMap<Integer,Integer> foundMatch = new HashMap<>();
        HashMap<Integer,Integer> cuttingMap = new HashMap<>();
        HashMap<Integer, Text> textMap = new HashMap<>();
        TextFlow textFlow  = new TextFlow();

        for ( MatchResult match : allMatches.allMatches(Pattern.compile(String.valueOf(regexPattern)), sentence) ) foundMatch.put(match.start(), match.end());

        for (HashMap.Entry<Integer,Integer> submap: foundMatch.entrySet()) {
            if (submap.getKey()-1 > 0) {
                cuttingMap.put(startval, submap.getKey());
                startval = submap.getValue() + 1;
            }
            Text newtext =  new Text(sentence.substring(submap.getKey()+1, submap.getValue()-1)+" ");
            newtext.setFill(Color.BLACK);
            newtext.setStyle("-fx-font-weight: bold");
//            newtext.getStyleClass().add("message");
//            newtext.getStyleClass().add("touched-text");
            textMap.put(submap.getKey(),newtext);
        }

        if(startval < sentence.length()) cuttingMap.put(startval, sentence.length());

        for (HashMap.Entry<Integer, Integer> map: cuttingMap.entrySet()){
            Text newtext =  new Text(sentence.substring(map.getKey(), map.getValue()));
            newtext.setFill(Color.BLACK);
            newtext.setStyle("-fx-font-weight: regular");
//            newtext.getStyleClass().add("message");
            textMap.put(map.getKey(),newtext);
        }
        Map<Integer, Text> sortedMap = new TreeMap<>(textMap);
        sortedMap.forEach((key, value) -> textFlow.getChildren().add(value));
        return textFlow;
    }
}
