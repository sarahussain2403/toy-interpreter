package interpreter.model.value;

import interpreter.model.type.StringType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringValueTest {

    @Test
    void getTypeIsStringType() {
        assertEquals(new StringType(), new StringValue("hi").getType());
    }

    @Test
    void equalStringsAreEqual() {
        assertEquals(new StringValue("test.in"), new StringValue("test.in"));
    }

    @Test
    void equalStringsHaveTheSameHashCode() {
        assertEquals(new StringValue("test.in").hashCode(),
                new StringValue("test.in").hashCode());
    }

    @Test
    void differentStringsAreNotEqual() {
        assertNotEquals(new StringValue("a"), new StringValue("b"));
    }

    @Test
    void toStringShowsQuotes() {
        assertEquals("\"hi\"", new StringValue("hi").toString());
    }
}