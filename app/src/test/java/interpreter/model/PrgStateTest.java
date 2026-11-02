package interpreter.model;

import interpreter.exceptions.MyException;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class PrgStateTest {

    @Test
    void ofCreatesEmptyStructuresWithTheProgramOnTheStack() throws MyException {
        IStmt program = new NopStmt();
        PrgState state = PrgState.of(program);

        assertSame(program, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
        assertTrue(state.getSymTable().toMap().isEmpty());
        assertEquals(0, state.getOut().size());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileTableFindsAFileByAnEqualStringValue() {
        PrgState state = PrgState.of(new NopStmt());
        state.getFileTable().put(new StringValue("f"),
                new BufferedReader(new StringReader("")));

        assertTrue(state.getFileTable().isDefined(new StringValue("f")));
        assertFalse(state.getFileTable().isDefined(new StringValue("g")));
    }

    @Test
    void toLogStringHasTheFourSectionsWithOneElementPerLine() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getSymTable().put("x", new IntValue(5));
        state.getOut().add(new IntValue(5));
        state.getFileTable().put(new StringValue("f.in"),
                new BufferedReader(new StringReader("")));

        assertEquals("ExeStack:\nnop\nSymTable:\nx --> 5\nOut:\n5\nFileTable:\nf.in\n",
                state.toLogString());
    }
}