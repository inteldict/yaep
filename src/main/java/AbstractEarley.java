import util.YStringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public abstract class AbstractEarley implements IEarley {

    public NT startSymbol = new NT("S");

    protected static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected Grammar grammar;
    protected Chart[] charts;

    public AbstractEarley(Grammar grammar) {
        this.grammar = grammar;
    }

    /**
     * Wrapper of the parse method measures the parsing time of the input string
     *
     * @param words - tokenized input string
     * @return charts
     * @see Chart[] parse(String[] words)
     */
    public ChartManager parseOnTime(String[] words) {
        Instant start = Instant.now();
        ChartManager result = parse(words);
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
    public ChartManager parse(String[] words) {
        init(words);

        predictor(startSymbol, 0);
        Chart currentChart;
        int i,j;
        for (i = 0; i <= words.length; i++) {
            currentChart = charts[i];
            for (j = 0; j < currentChart.states.size(); j++) {
                State st = currentChart.states.get(j);
                if (st.isFinished()) {
                    completer(st, i);
                } else if (st.isNextSymbolNonterminal()) {
                    predictor(st, i);
                } else if (i < words.length) { //do not call scanner for the last chart
                    scanner(st, i);
                }
            }
        }
        return new ChartManager(charts, startSymbol, words);
    }

    /**
     * For the state in S(i) of the form (X → α • Y β, j), add (Y → • γ, i) to S(i) for every production in the grammar with Y on the left-hand side (Y → γ)
     *
     * @param lhs   - next Nonterminal after dot
     * @param i     - state index
     */
    public void predictor(NT lhs, int i) {
        List<Rule> rules = grammar.rules.get(lhs);
        if (rules == null) {
            log.severe(() -> "There is no " + lhs + " rule in the grammar");
            System.exit(1);
        }
        for (Rule rule : rules) {
            charts[i].addState(new State(rule, i, 0));
        }
    }

    /**
     * For the state in S(i) of the form (X → α • Y β, j), add (Y → • γ, i) to S(i) for every production in the grammar with Y on the left-hand side (Y → γ)
     *
     * @param state
     * @param i     - state index
     */
    public void predictor(State state, int i) {
        NT lhs = (NT) state.getNextSymbol();
        predictor(lhs, i);
        // Aycock and Horspool solution for epsilon rules
        if (lhs.isNullable()) {
            charts[i].addState(new State(state.rule, i, state.dot + 1));
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
        List<State> states = charts[state.i].states;
        State st;
        for (int k = 0; k < states.size(); k++) {
            st = states.get(k);
            if (!st.isFinished() && lhs.equals(st.getNextSymbol())) {
                currentChart.addState(new State(st.rule, st.i, st.dot + 1));
            }
        }
    }

    public ChartManager parse(String input) {
        if (input == null | input.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'input' shouldn't be null or empty");
        }
        String[] tokens = YStringUtils.split(input);
        return parseOnTime(tokens);
    }

/*
        def check_coverage(self, tokens):
        """
        Check whether the grammar rules cover the given list of tokens.
        If not, then raise an exception.

        :type tokens: list(str)
        """
        missing = [tok for tok in tokens
                   if not self._lexical_index.get(tok)]
        if missing:
            missing = ', '.join('%r' % (w,) for w in missing)
            raise ValueError("Grammar does not cover some of the "
                             "input words: %r." % missing)
 */

    protected abstract void init(String[] words);

}
