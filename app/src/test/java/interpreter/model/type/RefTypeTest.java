package interpreter.model.type;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefTypeTest {

    @Test
    void defaultValueIsTheNullReferenceToTheInnerType() {
        assertEquals(new RefValue(0, new IntType()),
                new RefType(new IntType()).defaultValue());
    }

    @Test
    void defaultValueOfANestedReferenceKeepsTheInnerReferenceType() {
        assertEquals(new RefValue(0, new RefType(new IntType())),
                new RefType(new RefType(new IntType())).defaultValue());
    }

    @Test
    void aRefVariableGetsTheDefaultValueWhenDeclared() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        assertEquals(new RefValue(0, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void equalNestedTypesAreEqual() {
        assertEquals(new RefType(new RefType(new IntType())),
                new RefType(new RefType(new IntType())));
    }

    @Test
    void equalNestedTypesHaveTheSameHashCode() {
        assertEquals(new RefType(new RefType(new IntType())).hashCode(),
                new RefType(new RefType(new IntType())).hashCode());
    }

    @Test
    void typesOfDifferentDepthAreDifferent() {
        assertNotEquals(new RefType(new RefType(new IntType())),
                new RefType(new IntType()));
    }

    @Test
    void typesWithDifferentInnerTypesAreDifferent() {
        assertNotEquals(new RefType(new IntType()), new RefType(new BoolType()));
    }

    @Test
    void toStringShowsTheNesting() {
        assertEquals("Ref(int)", new RefType(new IntType()).toString());
        assertEquals("Ref(Ref(int))",
                new RefType(new RefType(new IntType())).toString());
    }
}