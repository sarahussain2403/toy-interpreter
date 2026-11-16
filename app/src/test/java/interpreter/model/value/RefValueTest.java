package interpreter.model.value;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefValueTest {

    @Test
    void getTypeIsARefTypeOfTheLocationType() {
        assertEquals(new RefType(new IntType()),
                new RefValue(1, new IntType()).getType());
    }

    @Test
    void aReferenceToAReferenceHasANestedType() {
        RefValue value = new RefValue(2, new RefType(new IntType()));
        assertEquals(new RefType(new RefType(new IntType())), value.getType());
    }

    @Test
    void equalReferencesAreEqualAndHaveTheSameHashCode() {
        RefValue a = new RefValue(1, new IntType());
        RefValue b = new RefValue(1, new IntType());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentAddressesAreNotEqual() {
        assertNotEquals(new RefValue(1, new IntType()),
                new RefValue(2, new IntType()));
    }

    @Test
    void differentLocationTypesAreNotEqual() {
        assertNotEquals(new RefValue(1, new IntType()),
                new RefValue(1, new RefType(new IntType())));
    }

    @Test
    void toStringShowsTheAddressAndTheLocationType() {
        assertEquals("(1, int)", new RefValue(1, new IntType()).toString());
        assertEquals("(2, Ref(int))",
                new RefValue(2, new RefType(new IntType())).toString());
    }
}