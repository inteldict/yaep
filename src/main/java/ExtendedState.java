import java.util.HashSet;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ExtendedState extends State {
    protected final int j;

    public ExtendedState(State state, int j) {
        super(state.rule, state.i, state.dot);
        this.j = j;
    }

    public ExtendedState(Rule rule, int i, int j, int dot) {
        super(rule, i, dot);
        this.j = j;
    }

    @Override
    public String toString() {
        return String.format("[%d:%d] %s %s %s", i, j, rule.lhs.symbol, SimpleGrammar.LHS_RHS_DELIM, rule.toStringWithDot(dot));
    }
}
