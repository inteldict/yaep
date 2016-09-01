import java.util.Objects;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 *
 * Non-Terminal implementation
 */
public class NT implements java.io.Serializable, Comparable<CharSequence>, CharSequence {
    protected final String symbol;
    private boolean nullable;

    public NT(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NT)) return false;
        NT nt = (NT) o;
        return symbol.equals(nt.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public int length() {
        return symbol.length();
    }

    @Override
    public char charAt(int index) {
        return symbol.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return symbol.subSequence(start, end);
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public int compareTo(CharSequence o) {
        return symbol.compareTo(o.toString());
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
