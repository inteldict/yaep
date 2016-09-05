import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class ChartManager {

    private final Chart[] charts;
    private final NT startSymbol;
    private final String[] words;

    public ChartManager(Chart[] charts, NT startSymbol, String[] words) {
        this.charts = charts;
        this.startSymbol = startSymbol;
        this.words = words;
    }

    public Stream<State> initialStates() {
        if (charts == null || startSymbol == null) {
            return Stream.empty();
        }
        return charts[0].states.stream().filter(st -> startSymbol.equals(st.rule.lhs) && st.dot == 0 && st.i == 0);
    }

    public Stream<State> finalStates() {
        if (charts == null || charts.length != (words.length + 1) || startSymbol == null) {
            return Stream.empty();
        }
        return charts[charts.length - 1].states.stream().filter(st -> (st.i == 0 && st.isFinished() && startSymbol.equals(st.rule.lhs)));
    }

    public boolean isRecognized() {
        return finalStates().findAny().isPresent();
    }

    public Chart[] getCharts() {
        return charts;
    }

    public NT getStartSymbol() {
        return startSymbol;
    }

    public String[] getWords() {
        return words;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChartManager that = (ChartManager) o;
        return Arrays.equals(charts, that.charts) &&
                Objects.equals(startSymbol, that.startSymbol) &&
                Arrays.equals(words, that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(charts, startSymbol, words);
    }
}
