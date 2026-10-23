package interpreter.model.value;

import interpreter.model.type.IntType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntValueTest {
    @Test
    void typeIsInt() {
        assertEquals(new IntType(), new IntValue(2).getType());
    }

    @Test
    void valuesWithTheSameNumberAreEqual() {
        assertEquals(new IntValue(2), new IntValue(2));
        assertNotEquals(new IntValue(2), new IntValue(3));
    }

    @Test
    void toStringShowsTheNumber() {
        assertEquals("2", new IntValue(2).toString());
    }
}