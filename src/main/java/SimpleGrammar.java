import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class SimpleGrammar extends Grammar {

    public final static NT EPSILON = new NT("ε");
    public final static String LHS_RHS_DELIM = "->";

    private final static String RIGHT_PART_DELIM = "|";
    private final static Pattern SPLIT_WITHOUT_QUOTES_PATTERN = Pattern.compile("([^\"']\\S*|[\"'].+?[\"'])\\s*");
    private final static Pattern QUOTE_PATTERN = Pattern.compile("[\"']");

    public SimpleGrammar() {
    }

    public SimpleGrammar(BufferedReader reader) {
        readFromBuffer(reader);
        setNullable();
    }

    public SimpleGrammar(String path) {
        readFromFile(path);
        setNullable();
    }

    private synchronized NT createOrGetNT(String symbol) {
        if (symbol == null) {
            return null;
        }
        Optional<NT> result = rules.keySet().stream().filter(nt -> symbol.equals(nt.toString())).findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            NT nt = new NT(symbol);
            rules.put(nt, new ArrayList<>());
            return nt;
        }
    }

    public void readResource(String path) {
//        ClassLoader.getSystemClassLoader().getResourceAsStream(path)
        try (InputStream inputStream = this.getClass().getResourceAsStream(path) ;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            readFromStream(reader.lines());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void readFromBuffer(BufferedReader reader) {
        try {
            readFromStream(reader.lines());
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFromStream(Stream<String> lines) {
        rules.putAll(lines.parallel()
                .filter(p -> !p.isEmpty() && !p.startsWith("#"))
                .map(this::parseLine).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> {
                    List<Rule> both = new ArrayList<Rule>(a);
                    both.addAll(b);
                    return both;
                })));
    }

    public void readFromFile(String path) {
//        URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            readFromStream(lines);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public Entry<NT, List<Rule>> parseLine(String line) {
        Matcher ntMatcher = SPLIT_WITHOUT_QUOTES_PATTERN.matcher(line);
        ArrayList<String> list = new ArrayList<>();
        while (ntMatcher.find()) {
            list.add(ntMatcher.group(1));
        }

        if (!LHS_RHS_DELIM.equals(list.get(1))) {    // Each lhs should be splitted from rhs by LHS_RHS_DELIM
            log.severe(() -> "Couldn't find '" + LHS_RHS_DELIM + "' in: " + line);
        }
        NT lhs = createOrGetNT(list.get(0));

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
        List<Rule> lhsRules = rhs.stream().filter(p -> !p.isEmpty()).map(sublist -> {
            return new Rule(lhs, sublist.stream().map(item -> {
                if (item.startsWith("\"") || item.startsWith("'")) {    // symbol is a terminal
                    Matcher quoteMatcher = QUOTE_PATTERN.matcher(item);
                    CharSequence terminal = quoteMatcher.replaceAll("");
                    terminals.add(terminal);
                    return terminal;
                } else {    // right part is a non-terminal
                    return createOrGetNT(item);
                }
            }).toArray(CharSequence[]::new));
        }).collect(Collectors.toList());
        return new AbstractMap.SimpleEntry<>(lhs, lhsRules);
    }


    public void setNullable() {

        if (!rules.containsKey(EPSILON)) {
            // There are no epsilon rules in the grammar, we have nothing to do here
            return;
        }

        HashSet<NT> nullable = new HashSet<>();
        int i, j;
        List<Rule> rules;
        Rule rule;
        NT lhs;
        for (Map.Entry<NT, List<Rule>> entry : this.rules.entrySet()) {
//            entry.getValue().stream().flatMap(r -> Stream.of(r.rhs)).filter(EPSILON::equals).findAny();
            rules = entry.getValue();
            for (i = 0; i < rules.size(); i++) {
                rule = rules.get(i);
                for (j = 0; j < rule.rhs.length; j++) {
                    if (EPSILON.equals(rule.rhs[j])) {
                        lhs = entry.getKey();
                        lhs.setNullable(true);
                        nullable.add(lhs);
                        rules.set(i, new Rule(lhs, new CharSequence[0]));
                        break;
                    }
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<NT, List<Rule>> entry : this.rules.entrySet()) {
                lhs = entry.getKey();
                if (lhs.isNullable()) {
                    continue;
                }
                rules = entry.getValue();
                // all rhs are nullable
                if (rules.stream().map(r -> Arrays.asList(r.rhs)).filter(nullable::containsAll).findAny().isPresent()) {
                    changed = true;
                    lhs.setNullable(true);
                    nullable.add(lhs);
                }
            }
        }
    }
}
