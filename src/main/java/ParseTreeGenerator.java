import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

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

    public List<Node> parseTrees() {
        Node temp;
        Chart chart;
        List<State> states;
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

        Optional<List<Node>> trees = states.stream()
                .filter(s -> (root.getFrom() == null || s.i == root.getFrom()) && (root.getTo() == null || s.j == root.getTo()))
                .map(s -> buildTree(s)).reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                });
        return trees.orElse(new ArrayList<>());
    }

    public List<Node> buildTree(State state) {
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
                    // predict VP[3:5]
                    Node previouslyAdded = result.get(j);
                    if (temp.getFrom() == null) {
                        temp.setFrom(previouslyAdded.getLastChildTo());
                    }

                    // get possible alternatives from the chart
                    List<State> states = completed.get(temp);
                    if (states != null) {
                        states.stream()
                                .filter(s -> (s != state) && (temp.getFrom() == null || s.i == temp.getFrom()) && (temp.getTo() == null || s.j == temp.getTo()))
                                .map(this::buildTree).forEach(s -> {
                            for (Node child : s) {
                                Node newNode = new Node(previouslyAdded);
                                newNode.addNode(child);
                                newResult.add(newNode);
                            }
                        });
                    } else {    //this hypothesis is false, remove this alternative
                        result.remove(j);
                        j--;
                    }
                    log.info(result.toString());
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

//    public Node stateToTree(State state) {
//        if (state.isFinished()) {
//            Node node = new Node(state.rule.lhs);
//            for (CharSequence cs : state.rule.rhs) {
//                node.children.add(new Node(cs));
//            }
//        }
//
//        return stateToTree(state.parentState);
//    }

//    public static String dottedRuleToString(DottedRule dr) {
//        StringBuilder sb = new StringBuilder();
//        // how about rules like:
//        //	NP -> NP and NP
//        // with embedded terminals?
//        if(dr != null) {
//            if(dr.complete()) {	              // S -> NP VP.
//                sb.append("(");
//                sb.append(dr.rule.get_lhs());
//                sb.append(" ");
//                if (Logger.isDebugMode()) {
//                    sb.append(String.format("%.1f ", dr.treeWeight));
//                }
//            }
//
//            sb.append(dottedRuleToString(dr.attachee_rule));
//
//            if(dr.completed_rule == null && dr.attachee_rule != null) {
//                sb.append(dr.attachee_rule.symbol_after_dot());
//                sb.append(" ");
//            }
//
//            sb.append(dottedRuleToString(dr.completed_rule));
//
//            if(dr.complete()) {
//                sb.append(")");
//            }
//        }
//
//        return sb.toString();
//    }
}
