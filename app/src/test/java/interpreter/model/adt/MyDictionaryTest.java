package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putThenLookupReturnsTheValue() throws ADTException {
        dict.put("a", 5);
        assertEquals(5, dict.lookup("a"));
    }

    @Test
    void isDefinedIsTrueOnlyForExistingKeys() {
        dict.put("a", 5);
        assertTrue(dict.isDefined("a"));
        assertFalse(dict.isDefined("b"));
    }

    @Test
    void lookupOfMissingKeyThrows() {
        var ex = assertThrows(ADTException.class, () -> dict.lookup("x"));
        assertEquals("key x is not defined", ex.getMessage());
    }

    @Test
    void updateChangesTheValue() throws ADTException {
        dict.put("a", 5);
        dict.update("a", 9);
        assertEquals(9, dict.lookup("a"));
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("x", 1));
    }
}