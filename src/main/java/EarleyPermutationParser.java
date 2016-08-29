import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class EarleyPermutationParser implements IEarley {

    public static final Rule INIT_RULE = new Rule(new NT("TOP"), new NT[]{new NT("S")});
    public static final State INIT_STATE = new State(INIT_RULE, 0, 0);
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Grammar grammar;
    private Chart[] charts;
    HashMap<String, List<Integer>> wordsMap = new HashMap<>();

    public EarleyPermutationParser(Grammar grammar) {
        this.grammar = grammar;
    }

    private void init(String[] words) {
        List<Integer> indexes;
        charts = new Chart[words.length + 1];
        for (int i = 0; i < words.length; i++) {
            charts[i] = new Chart();

            if ((indexes = wordsMap.get(words[i])) == null) {
                indexes = new ArrayList<>();
                indexes.add(i);
                wordsMap.put(words[i], indexes);
            } else {
                indexes.add(i);
            }
        }
        charts[words.length] = new Chart();
    }

    /**
     * Wrapper of the parse method measures the parsing time of the input string
     *
     * @param words - tokenized input string
     * @return charts
     * @see Chart[] parse(String[] words)
     */
    public Chart[] parseOnTime(String[] words) {
        Instant start = Instant.now();
        Chart[] result = parse(words);
        Duration timeElapsed = Duration.between(start, Instant.now());
        log.info(() -> "Parsing time: " + timeElapsed.toMillis() + "ms");
        return result;
    }

    /**
     * coordinates Earley's predictor/scanner/completer
     *
     * @param words
     * @return
     */
    public Chart[] parse(String[] words) {
        init(words);

        charts[0].addState(INIT_STATE);

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
     *
     * @param state
     * @param i     - state index
     */
    public void predictor(State state, int i) {
        CharSequence lhs = state.getNextSymbol();
        List<Rule> rules = grammar.rules.get(lhs);
        for (Rule rule : rules) {
            charts[i].addState(new State(rule, i, 0));
        }
    }

    /**
     * Compares the next symbol in the input stream with the next symbol of the form (X → α • a β, i), if it matchses add (X → α a • β, i) to S(i+1).
     *
     * @param state
     * @param i
     * @param word
     */
    public void scanner(State state, int i, String word) {
        // 1st: check if symbol in input
        if (wordsMap.containsKey(state.getNextSymbol())) {
            charts[i + 1].addState(new State(state.rule, i, state.dot + 1));
        }
    }

    /**
     * For the completed state in S(i) of the form (X → γ •, j), find states in S(j) of the form (Y → α • X β, k) and add (Y → α X • β, k) to S(i).
     *
     * @param state
     * @param i
     */
    public void completer(State state, int i) {
        NT lhs = state.rule.lhs;
        Chart currentChart = charts[i];
        charts[state.i].states
                .stream()
                .filter(st -> !st.isFinished() && lhs.equals(st.getNextSymbol()))
                .forEach(st -> {
                    currentChart.addState(new State(st.rule, st.i, st.dot + 1));
                });
    }
}
