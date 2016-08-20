/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public abstract class Rule {

    protected final NT lhs;

    public Rule(NT lhs) {
        this.lhs = lhs;
    }

    public abstract int getRHSLength();

    public abstract boolean isTerminal(int index);

    public abstract Object getSymbol(int index);

    public abstract String toStringWithDot(int dot);
}
