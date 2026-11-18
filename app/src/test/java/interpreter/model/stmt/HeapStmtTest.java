package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeapStmtTest {

    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
    }

    @Test
    void newAllocatesAtAddressOneAndUpdatesTheVariable() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);

        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void aSecondNewUsesTheNextAddressAndKeepsTheFirstLocation() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(30))).execute(state);

        assertEquals(new RefValue(2, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
        assertEquals(new IntValue(30), state.getHeap().get(2));
    }

    @Test
    void newCanStoreAReferenceInsideAReference() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);

        new NewStmt("a", new VarExp("v")).execute(state);

        assertEquals(new RefValue(2, new RefType(new IntType())),
                state.getSymTable().lookup("a"));
        assertEquals(new RefValue(1, new IntType()), state.getHeap().get(2));
    }

    @Test
    void newWithAnUndeclaredVariableThrows() {
        var stmt = new NewStmt("nope", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void newOnANonReferenceVariableThrows() throws MyException {
        new VarDeclStmt("x", new IntType()).execute(state);
        var stmt = new NewStmt("x", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void newWithAValueOfTheWrongTypeThrowsAndAllocatesNothing() {
        var stmt = new NewStmt("v", new ValueExp(new BoolValue(true)));

        assertThrows(StatementException.class, () -> stmt.execute(state));

        assertTrue(state.getHeap().getContent().isEmpty());
    }

    @Test
    void newToStringIsInTheProgramForm() {
        assertEquals("new(v, 20)", new NewStmt("v", new ValueExp(new IntValue(20))).toString());
    }

    @Test
    void wHChangesTheHeapLocation() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);

        new WriteHeapStmt("v", new ValueExp(new IntValue(30))).execute(state);

        assertEquals(new IntValue(30), state.getHeap().get(1));
    }

    @Test
    void wHDoesNotChangeTheVariable() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);

        new WriteHeapStmt("v", new ValueExp(new IntValue(30))).execute(state);

        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void wHWithAnUndeclaredVariableThrows() {
        var stmt = new WriteHeapStmt("nope", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void wHOnANonReferenceVariableThrows() throws MyException {
        new VarDeclStmt("x", new IntType()).execute(state);
        var stmt = new WriteHeapStmt("x", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void wHOnTheNullReferenceThrows() {
        var stmt = new WriteHeapStmt("v", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void wHWithAValueOfTheWrongTypeThrowsAndLeavesTheHeapUnchanged() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);
        var stmt = new WriteHeapStmt("v", new ValueExp(new BoolValue(true)));

        assertThrows(StatementException.class, () -> stmt.execute(state));

        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void wHToStringIsInTheProgramForm() {
        assertEquals("wH(v, 30)",
                new WriteHeapStmt("v", new ValueExp(new IntValue(30))).toString());
    }
}