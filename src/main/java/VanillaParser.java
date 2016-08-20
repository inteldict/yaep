import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class VanillaParser {
    private Grammar grammar;
    private String[] words;
    private Chart[] charts;

    public VanillaParser(Grammar g) {
        grammar = g;
    }

    public Chart[] getCharts() {
        return charts;
    }

    public boolean parseSentence(String[] words) {
        charts = new Chart[words.length];
        Arrays.fill(charts, new Chart());
    }

    private void init(String[] words) {
        charts = new Chart[words.length];
        Arrays.fill(charts, new Chart());
    }

    private Chart[] parse(String[] words) {
        init(words);
        Rule initRule = new NonTerminalRule(new NT("^"), new NT[]{new NT("S")});
        State initState = new State(initRule, 0, 0, 0, null);
        ArrayDeque<State> stack = new ArrayDeque<>();

        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < charts[i].states.size(); j++) {
                State st = charts[i].states.get(i);
                if (st.isFinished()) {
                    completer(st, i);
//                } else if () {
//
//                }
                }
            }

        }
        return charts;
    }

    private void predictor(State state, int i) {}

    private void scanner(State state, int i, String word) {}

    private void completer(State state, int i) {}

}
