package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
    void removeDeletesTheKey() throws ADTException {
        IDictionary<String, Integer> d = new MyDictionary<>();
        d.put("a", 1);
        d.remove("a");
        assertFalse(d.isDefined("a"));
    }

    @Test
    void removeOfAMissingKeyThrows() {
        IDictionary<String, Integer> d = new MyDictionary<>();
        assertThrows(ADTException.class, () -> d.remove("a"));
    }

    @Test
    void toMapReturnsTheContents() {
        IDictionary<String, Integer> d = new MyDictionary<>();
        d.put("a", 1);
        assertEquals(Map.of("a", 1), d.toMap());
    }

    @Test
    void toMapIsAReadOnlyCopy() {
        IDictionary<String, Integer> d = new MyDictionary<>();
        d.put("a", 1);
        Map<String, Integer> copy = d.toMap();
        assertThrows(UnsupportedOperationException.class, () -> copy.put("b", 2));
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("x", 1));
    }
}