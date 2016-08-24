import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Chart {

    protected final ArrayList<State> states = new ArrayList<>();

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
        StringBuilder result = new StringBuilder();
        for(State state : states) {
            result.append(state).append('\n');
        }
        return result.toString();
    }
}
