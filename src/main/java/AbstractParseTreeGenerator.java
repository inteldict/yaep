import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public abstract class AbstractParseTreeGenerator implements IParseTreeGenerator {

    protected static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected Chart[] charts;
    protected HashMap<Node, List<ExtendedState>> completed = new HashMap<>();

    public List<Node> parseTreesOnTime() {
        Instant start = Instant.now();
        List<Node> result = parseTrees();
        Duration timeElapsed = Duration.between(start, Instant.now());
        log.info(() -> "Building trees time: " + timeElapsed.toMillis() + "ms");
        return result;
    }

    public List<Node> parseTrees() {
        Node temp;
        Chart chart;
        List<ExtendedState> states;

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

        List<State> chartStates = charts[charts.length - 1].states;
        State lastState = chartStates.get(chartStates.size() -1);

        if (lastState.isFinished() && lastState.equals(new State(AbstractEarley.INIT_RULE, 0, 1))) {
            log.info(() -> "Recognition succeed");
            return buildTrees(new ExtendedState(lastState, charts.length - 1), new HashSet<>());
        } else {
            log.severe(() -> "Recognition failed");
            return new ArrayList<>();
        }
    }
}
