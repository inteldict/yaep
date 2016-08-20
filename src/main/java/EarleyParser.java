import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class EarleyParser {
    private Grammar grammar;
    private String[] words;
    private Chart[] charts;

    public EarleyParser(Grammar g) {
        grammar = g;
    }

    public Chart[] getCharts() {
        return charts;
    }

    private void init(String[] words) {
        this.words = words;
        charts = new Chart[words.length + 1];
        for (int i = 0; i < charts.length; i++) {
            charts[i] = new Chart();
        }
    }

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

    private void predictor(State state, int i) {
        CharSequence lhs = state.getNextSymbol();
        List<Rule> rules = grammar.rules.get(lhs);
        for (Rule rule : rules) {
            charts[i].addState(new State(rule, i, i, 0, state));
        }
    }

    private void scanner(State state, int i, String word) {
        if (word.equals(state.getNextSymbol())) {
            charts[i + 1].addState(new State(state.rule, i, i + 1, state.dot + 1, state));
        }
    }

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
