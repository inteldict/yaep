import util.Cartesian;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ParseTreeGenerator {
    private Chart[] charts;

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ParseTreeGenerator(Chart[] charts) {
        this.charts = charts;
    }

    public List<INode> parseTreesOnTime() {
        Instant start = Instant.now();
        List<INode> result = parseTrees();
        Duration timeElapsed = Duration.between(start, Instant.now());
        log.info(() -> "Building trees time: " + timeElapsed.toMillis() + "ms");
        return result;
    }

    public List<INode> parseTrees() {
        List<State > states = charts[charts.length - 1].states;
        State rootState = states.get(states.size() - 1);
        return buildTreesFromParentStates(rootState);
    }

    public List<INode> buildTreesFromParentStates(State state) {
        final List<INode> result = new ArrayList<>();
        final Node root = new Node(state.rule.lhs, state.i, state.j);

        State[] parentStates = state.parentStates.stream().toArray(State[]::new);

        if (parentStates.length == 0) {
            CharSequence cs = state.rule.rhs[0];
            LeafNode leaf = new LeafNode(cs, state.i, state.j);
            root.addNode(leaf);
            result.add(root);
            return result;
        }

        if (parentStates.length == 1) {
            List<INode> children = buildTreesFromParentStates(parentStates[0]);
            for (INode child : children) {
                Node newNode = new Node(root);
                newNode.addNode(child);
                result.add(newNode);
            }
            return result;
        }

        List<List<INode>> allChildren;
        State a,b;
        int from, to;

        /**
         * TODO: implement all number of permutations between parentstates, that give
         * a set from state.i till state.j
         */
        for (int i = 0; i < parentStates.length -1; i++) {
            allChildren = new ArrayList<>();
            a = parentStates[i];
            from = a.i;
            to = a.j;
            allChildren.add(buildTreesFromParentStates(a));

            for (int j = i+1; j < parentStates.length; j++) {
                b = parentStates[j];
                if (b.i == to) {
                    allChildren.add(buildTreesFromParentStates(b));
                    to = b.j;
                }
            }

            if (from == state.i && to == state.j) { //found all children
                result.addAll(combineResults(root, allChildren));
                if(allChildren.size() == parentStates.length) {   //there is only one possibility
                    return result;
                }
            }
        }
        return result;
    }

    public List<INode> combineResults(Node root, List<List<INode>> allChildren) {
        List<List<INode>> childrenProduct = Cartesian.cartesianProduct(allChildren);
        Node newNode;
        List<INode> result = new ArrayList<>();
        for (List<INode> children : childrenProduct) {
            newNode = new Node(root);
            newNode.setChildren(children);
            result.add(newNode);
        }
        return result;
    }

//    public List<Node> buildTreesFromChart(State state) {
//        final List<Node> result = new ArrayList<>();
//        final List<Node> newResult = new ArrayList<>();
//        final Node root = new Node(state.rule.lhs, state.i, state.j);
//        result.add(root);
//
//        int j;
//        for (int i = 0; i < state.rule.rhs.length; i++) {
//            newResult.clear();
//            for (j = 0; j < result.size(); j++) {
//                CharSequence cs = state.rule.rhs[i];
//                if (cs instanceof NT) {
//                    INode temp = new Node(cs);
//                    if (i == 0) {
//                        temp.setFrom(state.i);
//                    }
//                    if (i == state.rule.rhs.length - 1) {
//                        temp.setTo(state.j);
//                    }
//                    // for the states like S[0:5] -> NP[0:3] VP[null:5] *
//                    // predict VP[3:5] =>  S[0:5] -> NP[0:3] VP[3:5]
//                    Node previouslyAdded = result.get(j);
//                    if (temp.getFrom() == null) {
//                        temp.setFrom(previouslyAdded.getLastChildTo());
//                    }
//
//                    // get possible alternatives from the chart
//                    List<State> states = completed.get(temp);
//                    if (states != null) {
//                        states.stream()
//                                .filter(s -> (s != state) && (temp.getFrom() == null || s.i == temp.getFrom()) && (temp.getTo() == null || s.j == temp.getTo()))
//                                .flatMap(s -> this.buildTreesFromParentStates(s).stream())
//                                .forEach(child ->
//                                    newResult.add(new Node(previouslyAdded, child))
//                                );
//                    } else {    // this hypothesis is false, remove corresponding alternative
//                        result.remove(j);
//                        j--;
//                    }
//                } else {
//                    root.addNode(new LeafNode(cs, state.i, state.j));
//                }
//            }
//            if (!newResult.isEmpty()) {
//                result.clear();
//                result.addAll(newResult);
//            }
//        }
//        return result;
//    }
}
