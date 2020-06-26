package function;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class TextConstructor {
    private static final Pattern regexPattern = Pattern.compile("_(?!\\s)[a-zA-Z0-9 çëËêÊéÉÈèâÂäÄàÀ&\\/^#$,:.;|?!§=\"\'*\\+\\-\\(){}]+_");

    public static TextFlow BOLD (String sentence) {
        int startval = 0;
        HashMap<Integer,Integer> cuttingMap = new HashMap<>();
        HashMap<Integer, Text> textMap = new HashMap<>();
        TextFlow textFlow  = new TextFlow();

        for ( MatchResult match : allMatches.allMatches(Pattern.compile(String.valueOf(regexPattern)), sentence) ) {
            if(match.start()-1 > 0) cuttingMap.put(startval, match.start());
            startval = match.end() + 1;
            Text newtext =  new Text(sentence.substring(match.start()+1, match.end()-1));
            newtext.setStyle("-fx-font-weight: bold; -fx-fill: black");
            textMap.put(match.start(),newtext);
        }
        cuttingMap.put(startval, sentence.length());

        for (HashMap.Entry<Integer, Integer> map: cuttingMap.entrySet()){
            Text newtext =  new Text();
            if(map.getKey() == 0) newtext.setText(sentence.substring(map.getKey(), map.getValue()));
            else newtext.setText(sentence.substring(map.getKey()-1, map.getValue())+" ");
            newtext.setStyle("-fx-font-weight: 300;  -fx-fill: black");
            textMap.put(map.getKey(),newtext);
        }
        Map<Integer, Text> sortedMap = new TreeMap<>(textMap);
        textFlow.getChildren().addAll(sortedMap.values());
        return textFlow;
    }
}
