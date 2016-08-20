
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class SimpleGrammar extends Grammar {

    public final static String LHS_RHS_DELIM = "->";
    private final static String RIGHT_PART_DELIM = "|";
    private final static Pattern SPLIT_WITHOUT_QUOTES_PATTERN = Pattern.compile("([^\"']\\S*|[\"'].+?[\"'])\\s*");
    private final static Pattern QUOTE_PATTERN = Pattern.compile("[\"']");

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public SimpleGrammar(String fileName) {
        super();
        readFromFile(fileName);
    }

    public void readFromFile(String fileName) {

        URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
        try (
                Stream<String> stream = Files.lines(Paths.get(url.toURI()))) {
            stream.parallel().filter(p -> !p.isEmpty() && !p.startsWith("#")).forEach(line -> {
                Matcher ntMatcher = SPLIT_WITHOUT_QUOTES_PATTERN.matcher(line);
                ArrayList<String> list = new ArrayList<>();
                while (ntMatcher.find()) {
                    list.add(ntMatcher.group(1));
                }
                if (LHS_RHS_DELIM.equals(list.get(1))) {    // Each lhs schould be splitted from rhs by LHS_RHS_DELIM
                    NT lhs = new NT(list.get(0));

                    // Processing ot the right part of the rule like A -> B | C | D
                    List<List<String>> rhs = list.stream()
                            .skip(2) // skip lhs ->
                            .collect(
                                    () -> new ArrayList<List<String>>(Arrays.asList(new ArrayList<>())),
                                    (sublist, s) -> {
                                        if (RIGHT_PART_DELIM.equals(s)) {
                                            sublist.add(new ArrayList<>());
                                        } else {
                                            sublist.get(sublist.size() - 1).add(s);
                                        }
                                    },
                                    (list1, list2) -> {
                                        // Simple merging of partial sublists would
                                        // introduce a false level-break at the beginning.
                                        list1.get(list1.size() - 1).addAll(list2.remove(0));
                                        list1.addAll(list2);
                                    });

                    // Build rules
                    List<Rule> lhsRules = rhs.parallelStream().filter(p -> !p.isEmpty()).map(sublist -> {
                        String firstItem = sublist.get(0);
                        if (firstItem.startsWith("\"") || firstItem.startsWith("'")) {    // right part is a terminal
                            Matcher quoteMatcher = QUOTE_PATTERN.matcher(firstItem);
                            POS.add(lhs);
                            return new TerminalRule(lhs, quoteMatcher.replaceAll(""));
                        } else {    // right part is a non-terminal
                            return new NonTerminalRule(lhs, sublist.stream().map(NT::new).toArray(NT[]::new));
                        }
                    }).collect(Collectors.toList());

                    // Add rules to the grammar
                    if (rules.containsKey(lhs)) {
                        rules.get(lhs).addAll(lhsRules);
                    } else {
                        rules.put(lhs, lhsRules);
                    }
                } else {
                    log.severe(() -> "Couldn't find '" + LHS_RHS_DELIM + "' in: " + line);
                }
            });
        } catch (IOException | URISyntaxException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
