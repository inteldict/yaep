package util;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class YStringUtilsTest {

    @Test
    public void singleWordTest() {
        String oneWord = "Mary";
        assertThat(YStringUtils.split(oneWord), equalTo(new String[] {oneWord}));
    }

    @Test
    public void whiteSpacesTest() {
        String input = "    Mary called\tJan    \nfrom\rFrankfurt   ";
        assertThat(YStringUtils.split(input), equalTo(new String[] {"Mary", "called", "Jan",  "from", "Frankfurt",}));
    }

}
