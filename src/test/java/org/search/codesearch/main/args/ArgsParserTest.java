package org.search.codesearch.main.args;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArgsParserTest {

    @Test
    public void handles_empty_array() {
        Map<String, String> params = ArgsParser.cmdParams(new String[]{});
        assertEquals(0, params.size());
    }


    @Test
    public void handles_name_value_params() {
        Map<String, String> params = ArgsParser.cmdParams(new String[]{"-location", "/some/path"});
        assertEquals("/some/path", params.get("location"));
    }

    @Test
    public void fails_when_name_value_pair_mismatch() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ArgsParser.cmdParams(new String[]{"-location", "/some/path", "-s"}));
    }

    @Test
    public void fails_when_name_value_does_not_start_with_hyphen() {
        assertThrows(IllegalArgumentException.class, () -> ArgsParser.cmdParams(new String[]{"location", "/some/path"}));
    }

}
