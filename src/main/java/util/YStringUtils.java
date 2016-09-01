package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class YStringUtils {
    //    public static final Pattern SPLIT_PATTERN = Pattern.compile("\\s*(\\S*|.+?)\\s*");
    public static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * Split input by whitespaces into tokens.
     *
     * @param input
     * @return Splitted input. If input has no whitespaces, return new String[]{input}
     */
    public static String[] split(String input) {
        input = input.trim();
        Matcher matcher = WHITESPACE_PATTERN.matcher(input);
        if (matcher.find()) {
            return WHITESPACE_PATTERN.split(input);
        } else {
            return new String[]{input};
        }
    }

}
