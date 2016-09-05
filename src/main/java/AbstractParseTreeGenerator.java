import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public abstract class AbstractParseTreeGenerator implements IParseTreeGenerator {

    protected static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected HashMap<Node, List<ExtendedState>> completed = new HashMap<>();

    public List<Node> parseTreesOnTime(ChartManager chartManager) {
        Instant start = Instant.now();
        List<Node> result = parseTrees(chartManager);
        Duration timeElapsed = Duration.between(start, Instant.now());
        log.info(() -> "Building trees time: " + timeElapsed.toMillis() + "ms");
        return result;
    }

    public List<Node> parseTrees(ChartManager chartManager) {
        Node temp;
        Chart chart;
        List<ExtendedState> states;
        Chart[] charts = chartManager.getCharts();
        // This code can be replaced by lambda in Java 9 using Enumeration streams
        for (int i = 0; i < charts.length; i++) {
            chart = charts[i];
            for (State state : chart.states) {
                if (state.isFinished()) {
                    temp = new Node(state.rule.lhs, state.i, i);
                    if (completed.containsKey(temp)) {
                        completed.get(temp).add(new ExtendedState(state, i));
                    } else {
                        states = new ArrayList<>();
                        states.add(new ExtendedState(state, i));
                        completed.put(temp, states);
                    }
                }
            }
        }
        return chartManager.finalStates().flatMap(st -> buildTrees(new ExtendedState(st, charts.length - 1), new HashSet<>()).stream()).collect(Collectors.toCollection(ArrayList<Node>::new));
    }

        /**
     * Method, that used by parseTrees to generate subtrees.
     * @param state A completed state from Chart
     * @param parentStates States from Chart, that were influenced by @param state during parsing
     * @return all possible trees alternatives
     */
    protected abstract List<Node> buildTrees(ExtendedState state, HashSet<ExtendedState> parentStates);
}
