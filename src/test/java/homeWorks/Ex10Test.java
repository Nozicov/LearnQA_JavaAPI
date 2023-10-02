package homeWorks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex10Test {

    @ParameterizedTest
    @ValueSource(strings = {"15CharacterText", "16_CharacterText", "5Text"})
    @Disabled
    public void testLenghText(String text){
        assertTrue(text.length() > 15, "Length text not 15 characters");
    }
}
