package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void assignmentChangesTheValueOfTheVariable() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        new AssignStmt("v", new ValueExp(new IntValue(5))).execute(state);
        assertEquals(new IntValue(5), state.getSymTable().lookup("v"));
    }

    @Test
    void assigningToAnUndeclaredVariableThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new AssignStmt("x", new ValueExp(new IntValue(1))).execute(state));
        assertEquals("variable x was not declared", ex.getMessage());
    }

    @Test
    void assigningAValueOfTheWrongTypeThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new AssignStmt("v", new ValueExp(new BoolValue(true))).execute(state));
        assertEquals("declared type of variable v and type of the assigned expression do not match",
                ex.getMessage());
    }
}