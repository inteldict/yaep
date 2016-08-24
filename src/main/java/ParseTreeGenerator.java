import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ParseTreeGenerator {
    private Chart[] charts;
    private HashMap<Node, List<State>> completed = new HashMap<>();

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ParseTreeGenerator(Chart[] charts) {
        this.charts = charts;
    }

    public List<Node> parseTreesOnTime() {
        long startTime = System.currentTimeMillis();
        List<Node> result = parseTrees();
        log.info(() -> "Building trees time: " + (System.currentTimeMillis() - startTime) + "ms");
        return result;
    }

    public List<Node> parseTrees() {
        Node temp;
        Chart chart;
        List<State> states;

        // This code can be replaced by lambda in Java 9 using Enumeration streams
        for (int i = 0; i < charts.length; i++) {
            chart = charts[i];
            for (State state : chart.states) {
                if (state.isFinished()) {
                    temp = new Node(state.rule.lhs, state.i, i);
                    if (completed.containsKey(temp)) {
                        completed.get(temp).add(state);
                    } else {
                        states = new ArrayList<>();
                        states.add(state);
                        completed.put(temp, states);
                    }
                }
            }
        }
        Node root = new Node(EarleyParser.INIT_RULE.lhs, 0, charts.length - 1);
        states = completed.get(root);

        /** TODO: Consider to replace all of it by something like:
         *      states = charts[charts.length - 1].states;
         *      buildTrees(states.get(states.size() - 1));
         */
        return states
                .stream()
                .filter(s -> (root.getFrom() == null || s.i == root.getFrom()) && (root.getTo() == null || s.j == root.getTo()))
                .flatMap(s -> buildTrees(s).stream()).collect(Collectors.toList());
    }

    public List<Node> buildTrees(State state) {
        final List<Node> result = new ArrayList<>();
        final List<Node> newResult = new ArrayList<>();
        final Node root = new Node(state.rule.lhs, state.i, state.j);
        result.add(root);

        int j;
        for (int i = 0; i < state.rule.rhs.length; i++) {
            newResult.clear();
            for (j = 0; j < result.size(); j++) {
                CharSequence cs = state.rule.rhs[i];
                if (cs instanceof NT) {
                    INode temp = new Node(cs);
                    if (i == 0) {
                        temp.setFrom(state.i);
                    }
                    if (i == state.rule.rhs.length - 1) {
                        temp.setTo(state.j);
                    }
                    // for the states like S[0:5] -> NP[0:3] VP[null:5] *
                    // predict VP[3:5] =>  S[0:5] -> NP[0:3] VP[3:5]
                    Node previouslyAdded = result.get(j);
                    if (temp.getFrom() == null) {
                        temp.setFrom(previouslyAdded.getLastChildTo());
                    }

                    // get possible alternatives from the chart
                    List<State> states = completed.get(temp);
                    if (states != null) {
                        states.stream()
                                .filter(s -> (s != state) && (temp.getFrom() == null || s.i == temp.getFrom()) && (temp.getTo() == null || s.j == temp.getTo()))
                                .flatMap(s -> this.buildTrees(s).stream())
                                .forEach(child ->
                                    newResult.add(new Node(previouslyAdded, child))
                                );
                    } else {    // this hypothesis is false, remove corresponding alternative
                        result.remove(j);
                        j--;
                    }
                } else {
                    root.addNode(new LeafNode(cs, state.i, state.j));
                }
            }
            if (!newResult.isEmpty()) {
                result.clear();
                result.addAll(newResult);
            }
        }
        return result;
    }
}
