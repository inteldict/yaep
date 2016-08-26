import java.util.*;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */

public class State implements Comparable<State> {

    protected final Rule rule;
    protected final int i, j;
    protected final int dot;
    protected final LinkedHashSet<State> parentStates = new LinkedHashSet<>();

    public State(Rule rule, int i, int j, int dot, List<State> parentStates) {
        this.rule = rule;
        this.i = i;
        this.j = j;
        this.dot = dot;
        this.parentStates.addAll(parentStates);
    }

    public State(Rule rule, int i, int j, int dot) {
        this.rule = rule;
        this.i = i;
        this.j = j;
        this.dot = dot;
    }

    public boolean addParentState(State parentState) {
        return parentStates.add(parentState);
    }

    public boolean isNextSymbolTerminal() {
        return rule.isTerminal(dot);
    }

    public CharSequence getNextSymbol() {
        return rule.getSymbol(dot);
    }

    public boolean isFinished() {
        return rule.getRHSLength() == dot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return i == state.i &&
                j == state.j &&
                dot == state.dot &&
                Objects.equals(rule, state.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, i, j, dot);
    }

    @Override
    public String toString() {
        return String.format("[%d:%d] %s %s %s", i,j, rule.lhs.symbol, SimpleGrammar.LHS_RHS_DELIM, rule.toStringWithDot(dot));
    }

    @Override
    public int compareTo(State o) {
        if (i < o.i) {
            return -1;
        } else if (i == o.i) {
            if (j < o.j) {
                return -1;
            } else if (j == o.j) {
                return 0;
            } else { // j > o.i
                return 1;
            }
        } else { // i > o.i
            return 1;
        }
    }
}
