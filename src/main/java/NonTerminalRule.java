/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class NonTerminalRule extends Rule{

    protected final NT[] rhs;

    public NonTerminalRule(NT lhs, NT[] rhs) {
        super(lhs);
        this.rhs = rhs;
    }

    @Override
    public int getRHSLength() {
        return rhs.length;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    public NT getSymbol(int index) {
        assert index >= 0 && index < rhs.length;
        return rhs[index];
    }

    @Override
    public String toStringWithDot(int dot) {
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
