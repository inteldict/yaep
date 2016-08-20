/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Rule {

    protected final NT lhs;

    protected final CharSequence[] rhs;

    public Rule(NT lhs, CharSequence[] rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public int getRHSLength() {
        return rhs.length;
    }

    public boolean isTerminal(int index) {
        assert index >= 0 && index < rhs.length;

        return !(rhs[index] instanceof NT);
    }

    public CharSequence getSymbol(int index) {
        assert index >= 0 && index < rhs.length;

        return rhs[index];
    }

    public String toStringWithDot(int dot) {
        assert dot >= 0 && dot <= rhs.length;

        StringBuilder beforeDot = new StringBuilder();
        StringBuilder afterDot = new StringBuilder();
        for (int i = 0; i < dot && i < rhs.length; i++) {
            beforeDot.append(rhs[i]).append(" ");
        }
        for (int i = dot; i < rhs.length; i++) {
            afterDot.append(rhs[i]).append(" ");
        }
        beforeDot.append(" * ").append(afterDot);
        return beforeDot.toString();
    }

}
