import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry;
/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Node implements INode {

    private  CharSequence symbol;
    private Integer from;
    private Integer to;
    private List<INode> children = new ArrayList<>();
    private HashMap<CharSequence, Integer> wordsMap = new HashMap<>();

    public Node(Node node) {
        this.symbol = node.getSymbol();
        this.from = node.getFrom();
        this.to = node.getTo();
        children.addAll(node.getChildren());
        wordsMap.putAll(node.wordsMap);
    }

    public Node(Node node, INode child) {
        this(node);
        addNode(child);
    }

    public Node(CharSequence symbol) {
        this.symbol = symbol;
    }

    public Node(CharSequence symbol, Integer from, Integer to) {
        this.symbol = symbol;
        this.from = from;
        this.to = to;
    }

    public Node(CharSequence symbol, Integer from) {
        this.symbol = symbol;
        this.from = from;
    }

    public Node(CharSequence lhs, int i, int j, Map<CharSequence, Integer> inputMap) {
        this(lhs, i, j);
        wordsMap.putAll(inputMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(symbol, node.symbol) &&
                Objects.equals(from, node.from);
    }

    public Integer getLastChildTo() {
        if (children.isEmpty()) {
            return null;
        }
        return children.get(children.size() - 1).getTo();
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, from);
    }

    public String toString() {
        return String.format("[%d:%d] %s", from, to, symbol);
    }

    public CharSequence prettyPrint(int level) {
       String padding = "\n";
       if (level > 0) {
           char[] tabs = new char[level];
           Arrays.fill(tabs, '\t');
           padding += new String(tabs);
       }
       return String.format("%s([%d:%d] %s %s)",padding, from, to, symbol, children.isEmpty() ? "" : children.stream().map(c -> c.prettyPrint(level + 1)).collect(Collectors.joining()));
    }

    public boolean addNode(INode node) {
        if (node instanceof Node) {
            ((Node) node).getWordsMap().forEach((k, v) -> wordsMap.merge(k, v, (a,b) -> a + b));
        } else {
            wordsMap.put(node.getSymbol(), 1);
        }
        return children.add(node);
    }

    public boolean validateByInput(Map<CharSequence, Integer> inputMap) {
        return !wordsMap
                .entrySet()
                .stream()
                .filter(m -> inputMap.get(m.getKey()) < m.getValue())
                .findAny()
                .isPresent();

    }

    public List<INode> getChildren() {
        return children;
    }

    @Override
    public CharSequence getSymbol() {
        return symbol;
    }

    @Override
    public Integer getFrom() {
        return from;
    }

    @Override
    public Integer getTo() {
        return to;
    }

    public void setSymbol(CharSequence symbol) {
        this.symbol = symbol;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    final Map<CharSequence, Integer> wordsMap() {
        List<Map<CharSequence, Integer>> childMaps = new ArrayList<>();

        for (INode node : children) {
            if (node instanceof Node) {
                childMaps.add(((Node) node).wordsMap());
            } else {
                Map<CharSequence, Integer> wordsMap = new HashMap<>();
                wordsMap.put(node.getSymbol(), 1);
                childMaps.add(wordsMap);
            }
        }

        return childMaps.stream()
                .flatMap(m ->  m.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a,b) -> a + b));
    }

    public HashMap<CharSequence, Integer> getWordsMap() {
        return wordsMap;
    }
}
