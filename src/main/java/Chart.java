import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Chart {

    protected final ArrayList<State> states = new ArrayList<>();

    public static String prettyPrint(final Chart[] charts) {
        StringBuilder chartOutput = new StringBuilder();
        for (int i = 0; i < charts.length; i++) {
            chartOutput.append("Chart " + i + ":\n" + charts[i].toString(String.valueOf(i)) + "\n");
        }
        return chartOutput.toString();
    }

    public void addState(State s) {
        if (!states.contains(s)) {
            states.add(s);
        }
    }

    public State getState(int i) {
        if (i < 0 || i >= states.size())
            return null;
        return states.get(i);
    }

    @Override
    public String toString() {
        return toString("..");
    }

    public String toString(String j) {
        StringBuilder result = new StringBuilder();
        for (State state : states) {
            result.append(state.toString(j)).append('\n');
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chart chart = (Chart) o;
        return Objects.equals(states, chart.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(states);
    }
}
