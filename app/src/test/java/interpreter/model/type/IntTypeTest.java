package interpreter.model.type;

import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntTypeTest {
    @Test
    void defaultValueIsZero() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
    }

    @Test
    void equalsOnlyAnotherIntType() {
        assertEquals(new IntType(), new IntType());
        assertNotEquals(new IntType(), new BoolType());
    }

    @Test
    void toStringIsInt() {
        assertEquals("int", new IntType().toString());
    }
}