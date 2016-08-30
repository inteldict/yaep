import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ParseTreeGenerator {
    private Chart[] charts;
    private HashMap<Node, List<ExtendedState>> completed = new HashMap<>();
    private final Map<String, Integer> wordsMap;

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ParseTreeGenerator(Chart[] charts, Map<String, Integer> wordsMap)
    {
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
//        System.out.println("Root:" + root);
        int j;
        Integer index;
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
                    Map<CharSequence, Integer> previouslyAddedMap = previouslyAdded.wordsMap();
                    if (temp.getFrom() == null) {
//                        System.out.println(previouslyAdded.getLastChildTo());
                        temp.setFrom(previouslyAdded.getLastChildTo());
                    }

                    // get possible alternatives from the chart
                    List<ExtendedState> states = completed.get(temp);
                    if (states != null) {
                        states.stream()
                                .filter(s -> !parentStates.contains(s) && (temp.getFrom() == null || s.i == temp.getFrom()) && (temp.getTo() == null || s.j == temp.getTo()))
                                .flatMap(s ->
                                    this.buildTrees(s, new HashSet<>(parentStates)).stream()
                                ).forEach(child -> {
//                                            newResult.add(new Node(previouslyAdded, child));
                                            Map<CharSequence, Integer> childMap = child.wordsMap();
                                            Map<CharSequence, Integer> totalMap = Stream.of(previouslyAddedMap, childMap).flatMap(m -> m.entrySet().stream())
                                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a + b));

                                            if (!totalMap.entrySet().stream().filter(m -> wordsMap.containsKey(m.getKey()) && wordsMap.get(m.getKey()) < m.getValue()).findAny().isPresent()) {
                                                newResult.add(new Node(previouslyAdded, child));
                                            }
                                        }
                                );
                    } else {    // the hypothesis is false, remove corresponding alternative
                        result.remove(j);
                        j--;
                    }
                } else {
                    newResult.add(new Node(root, new LeafNode(cs, state.i, state.j)));
//                    root.addNode(new LeafNode(cs, state.i, state.j));
                }
            }
            result.clear();
            if (!newResult.isEmpty()) {
                result.addAll(newResult);
            } else {
                return result;
            }
        }
        return result;
    }
}
