import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ParseTreeGenerator {

//    public List<Node> parseTrees(Chart[] charts) {
//        assert charts.length > 0 && !charts[charts.length - 1].states.isEmpty();
//
//        List<Node> trees = new ArrayList<>();
//        ArrayList<State> finalStates = charts[charts.length - 1].states;
//        State finalState = finalStates.get(finalStates.size() - 1);
//
//        if (!finalState.rule.equals(EarleyParser.initRule)) {   // There are no trees in the chart
//            return trees;
//        }
//
//        State parentState = finalState;
//        Node node;
//        Node parent = null;
//        while(parentState != null) {
//            if (parentState.isFinished()) { // if state was compeleted
//                node = new Node(parentState.rule.lhs);
//                if (parent != null) {
//                    parent.children.add(0, node);
//                } else {
//                    trees.add(node);
//                }
//                parent = node;
//                // check if rhs contains terminal
//                if (!(parentState.rule.rhs[0] instanceof NT)) {
//                    node.children.add(new Node(parentState.rule.rhs[0]));
//                }
//
//            }
//            parentState = parentState.parentState;
//        }
//
//        return trees;
//    }

    public List<Node> parseTrees(Chart[] charts) {
        assert charts.length > 0 && !charts[charts.length - 1].states.isEmpty();

        List<Node> trees = new ArrayList<>();
        ArrayList<State> finalStates = charts[charts.length - 1].states;
        State finalState = finalStates.get(finalStates.size() - 1);

        if (!finalState.rule.equals(EarleyParser.initRule)) {   // There are no trees in the chart
            return trees;
        }

        Node parent = null;
        Node node, temp;
        Rule rule;
        List<Node> nodeChildren, oldchildren = null;
        Chart currentChart;
        State state;
        for (int i = charts.length - 1; i >= 0; i--) {
            currentChart = charts[i];
            for (int j = currentChart.states.size() - 1; j >= 0; j--) {
                state = currentChart.states.get(j);
                if (state.isFinished()) {

                    rule = state.rule;
                    node = null;
                    if (oldchildren == null) {
                        node = new Node(rule.lhs, state.i, i - 1);
                        trees.add(node);
                    } else {
                        for (Node n : oldchildren) {
                            if (rule.lhs.equals(n.value) && (n.from == null || state.i == n.from)) {
                                node = n;
                                break;
                            }
                        }
                    }
                    nodeChildren = node.children;
                    for (int k = 0; k < rule.rhs.length; k++) {
                        nodeChildren.add(temp = new Node(rule.rhs[k]));
                        if (k == 0) {
                            temp.from = node.from;
                        }
                        if (k == rule.rhs.length - 1) {
                            temp.to = node.to;
                        }
                    }
                    oldchildren = nodeChildren;
                }
            }
        }



        return trees;
    }

}
