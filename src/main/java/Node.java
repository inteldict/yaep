import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Node implements INode {

    private  CharSequence symbol;
    private Integer from;
    private Integer to;
    private List<INode> children = new ArrayList<>();

    public Node(Node node) {
        this.symbol = node.getSymbol();
        this.from = node.getFrom();
        this.to = node.getTo();
        children.addAll(node.getChildren());
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
        return children.add(node);
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
}
