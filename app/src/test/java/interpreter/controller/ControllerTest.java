package interpreter.controller;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.stmt.AssignStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @TempDir
    Path dir;
    private Repository newRepo(PrgState state) {
        return new Repository(state, dir.resolve("log.txt").toString());
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        new Controller(newRepo(state), false).allStep();
        return state;
    }



    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }


    @Test
    void example4DivisionByZeroStopsTheProgram() {
        var ex = assertThrows(ExpressionException.class, () -> run(Examples.EX4));
        assertEquals("division by zero", ex.getMessage());
    }

    @Test
    void example5AssigningIntToBoolThrows() {
        assertThrows(StatementException.class, () -> run(Examples.EX5));
    }



    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = run(new NopStmt());
        Controller controller = new Controller(newRepo(state), false);
        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertEquals("program state stack is empty", ex.getMessage());
    }

    @Test
    void assigningToUndeclaredVariableStopsTheProgram() {
        IStmt program = new AssignStmt("x", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> run(program));
    }



    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void logFileHasOneBlockForTheStartAndOneForEachStep() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int blocks = log.split("ExeStack:", -1).length - 1;
        assertEquals(6, blocks);
    }

    @Test
    void logFileIsWrittenEvenWhenTheProgramFails() throws Exception {
        assertThrows(ExpressionException.class, () -> run(Examples.EX4));
        String log = Files.readString(dir.resolve("log.txt"));
        assertTrue(log.contains("ExeStack:"));
    }
}