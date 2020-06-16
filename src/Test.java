import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<String> matches = Pattern.compile("^(.*)(_)([a-zA-Z0-9 ëËêÊéÉÈèâÂäÄàÀ&\\\\/^#$,:.;|?!§*\\\\+\\\\-\\\\(){}]+)(_)(.*)",3)
                .matcher("string to search _il est là_ from here _et là aussi_")
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        System.out.println(matches);

        for (MatchResult match : allMatches.allMatches(Pattern.compile("[abc]"), "abracadabra") ) {
            System.out.println(match.group() + " at " + match.start()+ " end " + match.end());
        }
    }
}
