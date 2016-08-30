import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ParseTreeGenerator {
    private Chart[] charts;
    private HashMap<Node, List<ExtendedState>> completed = new HashMap<>();
    private final Map<CharSequence, Integer> wordsMap;

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ParseTreeGenerator(Chart[] charts, Map<CharSequence, Integer> wordsMap) {
        this.charts = charts;
        this.wordsMap = wordsMap;
    }

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
        Node root = new Node(EarleyParser.INIT_RULE.lhs, 0, charts.length - 1);
        states = completed.get(root);

        /** TODO: Consider to replace all of it by something like:
         *      states = charts[charts.length - 1].states;
         *      buildTrees(states.get(states.size() - 1));
         */
        return states
                .stream()
                .filter(s -> (root.getFrom() == null || s.i == root.getFrom()) && (root.getTo() == null || s.j == root.getTo()))
                .flatMap(s -> buildTrees(s, new HashSet<>()).stream()).collect(Collectors.toList());
    }

    public List<Node> buildTrees(ExtendedState state, HashSet<ExtendedState> parentStates) {
        final List<Node> result = new ArrayList<>();
        final List<Node> newResult = new ArrayList<>();
        final Node root = new Node(state.rule.lhs, state.i, state.j);
        result.add(root);
        parentStates.add(state);

        int j;
        int lastNodeIndex;
        boolean differentLastNodeIndex;
        for (int i = 0; i < state.rule.rhs.length; i++) {
            CharSequence cs = state.rule.rhs[i];
            if (cs instanceof NT) {
                INode temp = new Node(cs);

                if (i == state.rule.rhs.length - 1) {
                    temp.setTo(state.j);
                }

                differentLastNodeIndex = false;

                // set node "to" index
                if (i == 0) {
                    temp.setFrom(state.i);
                } else {
                    // for the states like S[0:5] -> NP[0:3] VP[null:5] *
                    // predict VP[3:5] =>  S[0:5] -> NP[0:3] VP[3:5]

                    lastNodeIndex = result.get(0).getLastChildTo();
                    for (j = 1; j < result.size(); j++) {
                        if (lastNodeIndex != result.get(j).getLastChildTo()) {
                            differentLastNodeIndex = true;
                            break;
                        }
                    }
                    if (!differentLastNodeIndex) {
                        temp.setFrom(lastNodeIndex);
                    }
                }

                /**
                 * Generate alternatives
                 *
                 * there are two possible cases here:
                 *
                 * 1. all childs have the same "to" index,
                 *  in this case we reuse permutations, that were builded from states
                 *
                 * 2. childs have differrent "to" indexes, so we should
                 *  lookup and process states for each child sepatly. Possible optimization:
                 *  group childs by "to" index
                 */

                if (!differentLastNodeIndex) {
                    List<ExtendedState> states = completed.get(temp);
                    if (states != null) {
                        states.stream()
                                .filter(s -> !parentStates.contains(s) && (temp.getFrom() == null || s.i == temp.getFrom()) && (temp.getTo() == null || s.j == temp.getTo()))
                                .flatMap(s ->
                                        this.buildTrees(s, new HashSet<>(parentStates)).stream()
                                ).forEach(child -> {
                            for (Node tempRoot : result) {
                                Node testNode = new Node(tempRoot, child);
                                if (testNode.validateByInput(wordsMap)) {
                                    newResult.add(testNode);
                                }
                            }
                        });
                    } else {    // the hypothesis is false, remove corresponding alternative
                        result.clear();
                        return result;
                    }
                } else { // in this case last child of different results has different index
                    for (Node tempRoot : result) {
                        temp.setFrom(tempRoot.getLastChildTo());
                        List<ExtendedState> states = completed.get(temp);
                        if (states != null) {
                            states.stream()
                                    .filter(s -> !parentStates.contains(s) && (temp.getFrom() == null || s.i == temp.getFrom()) && (temp.getTo() == null || s.j == temp.getTo()))
                                    .flatMap(s ->
                                            this.buildTrees(s, new HashSet<>(parentStates)).stream()
                                    ).forEach(child -> {
                                        Node testNode = new Node(tempRoot, child);
                                        if (testNode.validateByInput(wordsMap)) {
                                            newResult.add(testNode);
                                        }
                                    }
                            );
                        } /** else this hypothesis is false, remove corresponding alternative
                         * the block absents, because we clearing result after each iteration
                         **/
                    }
                }
            } else {
                for (Node tempRoot : result) {
                    newResult.add(new Node(tempRoot, new LeafNode(cs, state.i, state.j)));
                }
            }
            result.clear();
            if (!newResult.isEmpty()) {
                result.addAll(newResult);
                newResult.clear();
            } else {
                return result;
            }
        }
        return result;
    }
}
