import java.util.Arrays;
import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */

public class EarleyParser {
    private Grammar grammar;
    private String[] sentence;
    private Chart[] charts;

    public EarleyParser(Grammar g) {
        grammar = g;
    }

    public Chart[] getCharts() {
        return charts;
    }

    public boolean parseSentence(String[] s) {
        sentence = s;
        charts = new Chart[sentence.length + 1];
        Arrays.fill(charts, new Chart());

        NT nts = new NT("S");
        Rule initRule = new NonTerminalRule(new NT("$"), new NT[] {nts});
        State initState = new State(initRule, 0, 0, 0, null);
        charts[0].addState(initState);

        for (Chart c : charts) {
            for (int i = 0; i < c.states.size(); i++) {
                State st = c.states.get(i);
                if (st.isFinished()) {   // RHS: ... @
                    completer(st);
                }
                else if (st.rule.isTerminal()) { // RHS: ... @ A ..., where A is a part of speech
                    scanner(st);
                }
                else {  // RHS: ... @ A ..., where A is a non-terminal
                    predictor(st);
                }
            }
        }
        // Determine whether a successful parse.
        State finalState = new State(initRule, 0, sentence.length, initRule.getRHSLength(), null);
        State lastState = charts[sentence.length].getState(charts[sentence.length].states.size() - 1);
        return finalState.equals(lastState);
    }

    private void predictor(State state) {
        Object lhs = state.getNextSymbol();
        List<Rule> rhs = grammar.rules.get(lhs);
        int j = state.j;
        for (int i = 0; i < rhs.size(); i++) {
            charts[j].addState(new State(rhs.get(i), j, j, 0, state));
        }
    }

    private void scanner(State state) {
        NT lhs = state.rule.lhs;
        List<Rule> rhs = grammar.rules.get(lhs);
        int j = state.j;
        for (Rule rule : rhs) {
            String symbol = ((TerminalRule)rule).rhs;
            if (j < sentence.length && symbol.equals(sentence[j])) {
                charts[j + 1].addState(new State(rule, j, j + 1, 1, state));
            }
        }
    }

    private void completer(State state) {
        NT lhs = state.rule.lhs;
        for (State st : charts[state.i].states) {
            Object after = st.getNextSymbol();
            if (lhs.equals(after)) {
                charts[state.j].addState(new State(st.rule, st.i, st.j, st.dot + 1, state));
            }
        }
    }
}
