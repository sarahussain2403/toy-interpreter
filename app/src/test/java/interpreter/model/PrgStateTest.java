package interpreter.model;

import interpreter.exceptions.MyException;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

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
        assertTrue(state.getHeap().getContent().isEmpty());
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
    void toLogStringHasTheFiveSectionsWithOneElementPerLine() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getSymTable().put("x", new IntValue(5));
        state.getOut().add(new IntValue(5));
        state.getFileTable().put(new StringValue("f.in"),
                new BufferedReader(new StringReader("")));
        state.getHeap().allocate(new IntValue(20));

        assertEquals("ExeStack:\nnop\nSymTable:\nx --> 5\nOut:\n5\nFileTable:\nf.in\n"
                + "Heap:\n1 --> 20\n", state.toLogString());
    }

    @Test
    void theHeapSectionOfTheLogIsSortedByAddress() {
        PrgState state = PrgState.of(new NopStmt());
        state.getHeap().setContent(Map.of(
                3, new IntValue(30), 1, new IntValue(10), 2, new IntValue(20)));

        String log = state.toLogString();

        assertTrue(log.endsWith("Heap:\n1 --> 10\n2 --> 20\n3 --> 30\n"));
    }

    @Test
    void anEmptyHeapGivesAnEmptyHeapSection() {
        PrgState state = PrgState.of(new NopStmt());
        assertTrue(state.toLogString().endsWith("FileTable:\nHeap:\n"));
    }
}