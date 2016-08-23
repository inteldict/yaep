/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class LeafNode implements INode {
    private CharSequence symbol;
    private Integer from;
    private Integer to;

    public LeafNode(CharSequence symbol, Integer from, Integer to) {
        this.symbol = symbol;
        this.from = from;
        this.to = to;
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
