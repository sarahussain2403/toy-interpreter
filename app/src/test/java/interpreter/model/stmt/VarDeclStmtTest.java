package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VarDeclStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void declaringAnIntGivesItTheValueZero() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void declaringABoolGivesItTheValueFalse() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        assertEquals(new BoolValue(false), state.getSymTable().lookup("b"));
    }

    @Test
    void declaringAVariableTwiceThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new VarDeclStmt("v", new IntType()).execute(state));
        assertEquals("variable v is already declared", ex.getMessage());
    }
}