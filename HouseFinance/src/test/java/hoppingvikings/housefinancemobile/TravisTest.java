package hoppingvikings.housefinancemobile;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TravisTest {
    @Test
    public void Dumb_CI_Validator_Test() {
        assertThat(true, is(true));
    }
}