import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class EarleyParser {

    private Grammar grammar;
    private String[] words;
    private Chart[] charts;

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public EarleyParser(Grammar grammar) {
        this.grammar = grammar;
    }

    private void init(String[] words) {
        this.words = words;
        charts = new Chart[words.length + 1];
        for (int i = 0; i < charts.length; i++) {
            charts[i] = new Chart();
        }
    }

    /**
     *  Wrapper of the parse method measures the parsing time of the input string
     * @see Chart[] parse(String[] words)
     *
     * @param words - tokenized input string
     * @return charts
     */
    public Chart[] parseOnTime(String[] words) {
        long startTime = System.currentTimeMillis();
        Chart[]  result = parse(words);
        log.info(() -> "Parsing time: " + (System.currentTimeMillis() - startTime) + "ms");
        return result;
    }

    /**
     * coordinates Earley's predictor/scanner/completer
     * @param words
     * @return
     */
    public Chart[] parse(String[] words) {
        init(words);

        Rule initRule = new Rule(new NT("^"), new NT[]{new NT("S")});
        State initState = new State(initRule, 0, 0, 0, null);
        charts[0].addState(initState);

        Chart currentChart;
        for (int i = 0; i < words.length; i++) {
            currentChart = charts[i];
            for (int j = 0; j < currentChart.states.size(); j++) {
                State st = currentChart.states.get(j);
                if (st.isFinished()) {
                    completer(st, i);
                } else if (st.isNextSymbolTerminal()) {
                        scanner(st, i, words[i]);
                } else {
                    predictor(st, i);
                }
            }
        }
        currentChart = charts[words.length];
        for (int j = 0; j < currentChart.states.size(); j++) {
            State st = currentChart.states.get(j);
            if (st.isFinished()) {
                completer(st, words.length);
            }
        }

        return charts;
    }

    /**
     * For the state in S(i) of the form (X → α • Y β, j), add (Y → • γ, i) to S(i) for every production in the grammar with Y on the left-hand side (Y → γ)
     * @param state
     * @param i - state index
     */
    private void predictor(State state, int i) {
        CharSequence lhs = state.getNextSymbol();
        List<Rule> rules = grammar.rules.get(lhs);
        for (Rule rule : rules) {
            charts[i].addState(new State(rule, i, i, 0, state));
        }
    }

    /**
     * Compares the next symbol in the input stream with the next symbol of the form (X → α • a β, i), if it matchses add (X → α a • β, i) to S(i+1).
     * @param state
     * @param i
     * @param word
     */
    private void scanner(State state, int i, String word) {
        if (word.equals(state.getNextSymbol())) {
            charts[i + 1].addState(new State(state.rule, i, i + 1, state.dot + 1, state));
        }
    }

    /**
     * For the completed state in S(i) of the form (X → γ •, j), find states in S(j) of the form (Y → α • X β, k) and add (Y → α X • β, k) to S(i).
     * @param state
     * @param i
     */
    private void completer(State state, int i) {
        NT lhs = state.rule.lhs;
        Chart currentChart = charts[i];
        charts[state.i].states
                .stream()
                .filter(st -> !st.isFinished() && lhs.equals(st.getNextSymbol()))
                .forEach(st -> {
                    currentChart.addState(new State(st.rule, st.i, i, st.dot + 1, state));
                });
    }
}
