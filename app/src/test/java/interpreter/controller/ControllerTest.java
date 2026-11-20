package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.AssignStmt;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
    void example5PrintsFive() throws MyException {
        PrgState state = run(Examples.EX5);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(5), state.getOut().get(0));
    }

    @Test
    void notEqualsExampleFromA3PrintsTrue() throws MyException {
        IStmt program = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new VarDeclStmt("c", new BoolType()),
                                new CompStmt(
                                        new AssignStmt("a", new ValueExp(new IntValue(1))),
                                        new CompStmt(
                                                new AssignStmt("b", new ValueExp(new IntValue(2))),
                                                new CompStmt(
                                                        new AssignStmt("c",
                                                                new RelationalExp(RelOp.NE,
                                                                        new VarExp("a"),
                                                                        new VarExp("b"))),
                                                        new PrintStmt(new VarExp("c"))))))));

        PrgState state = run(program);

        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new BoolValue(true), state.getOut().get(0));
        assertEquals(new IntValue(1), state.getSymTable().lookup("a"));
        assertEquals(new IntValue(2), state.getSymTable().lookup("b"));
        assertEquals(new BoolValue(true), state.getSymTable().lookup("c"));
    }

    @Test
    void example6PrintsTheTwoReferences() throws MyException {
        PrgState state = run(Examples.EX6);

        assertEquals(2, state.getOut().size());
        RefValue v = assertInstanceOf(RefValue.class, state.getOut().get(0));
        RefValue a = assertInstanceOf(RefValue.class, state.getOut().get(1));
        assertEquals(1, v.address());
        assertEquals(new IntType(), v.locationType());
        assertEquals(2, a.address());
        assertEquals(new RefType(new IntType()), a.locationType());
        assertEquals(new IntValue(20), state.getHeap().getContent().get(1));
        assertEquals(v, state.getHeap().getContent().get(2));
    }

    @Test
    void example7PrintsTwentyAndTwentyFive() throws MyException {
        PrgState state = run(Examples.EX7);

        assertEquals(List.of(new IntValue(20), new IntValue(25)), state.getOut().toList());
    }

    @Test
    void example8WritesThirtyIntoTheHeap() throws MyException {
        PrgState state = run(Examples.EX8);

        assertEquals(List.of(new IntValue(20), new IntValue(35)), state.getOut().toList());
        assertEquals(Map.of(1, new IntValue(30)), state.getHeap().getContent());
    }

    @Test
    void example10CountsDownFromFourToZero() throws MyException {
        PrgState state = run(Examples.EX10);

        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2),
                new IntValue(1), new IntValue(0)), state.getOut().toList());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");

        PrgState state = run(Examples.fileExample(in.toString()));

        assertEquals(2, state.getOut().size());
        assertEquals(new IntValue(15), state.getOut().get(0));
        assertEquals(new IntValue(50), state.getOut().get(1));
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithAnEmptyFilePrintsZeroTwice() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");

        PrgState state = run(Examples.fileExample(in.toString()));

        assertEquals(2, state.getOut().size());
        assertEquals(new IntValue(0), state.getOut().get(0));
        assertEquals(new IntValue(0), state.getOut().get(1));
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithAMissingFileThrows() {
        String missing = dir.resolve("missing.in").toString();
        assertThrows(StatementException.class,
                () -> run(Examples.fileExample(missing)));
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
    void logFileContainsTheFiveSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:"),
                e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
    }

    @Test
    void logFileHasABlockForTheStartAndTwoForEachStep() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int blocks = log.split("ExeStack:", -1).length - 1;
        assertEquals(11, blocks);
    }

    @Test
    void logFileIsWrittenEvenWhenTheProgramFails() throws Exception {
        IStmt program = new AssignStmt("x", new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> run(program));
        String log = Files.readString(dir.resolve("log.txt"));
        assertTrue(log.contains("ExeStack:"));
    }

    @Test
    void example9PrintsTwentyBecauseTheGarbageCollectorIsSafe() throws MyException {
        PrgState state = run(Examples.EX9);

        assertEquals(List.of(new IntValue(20)), state.getOut().toList());
    }

    @Test
    void theControllerRemovesLocationsNobodyCanReach() throws MyException {
        IStmt program = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ValueExp(new IntValue(20))),
                        new NewStmt("v", new ValueExp(new IntValue(30)))));

        PrgState state = run(program);

        assertEquals(Map.of(2, new IntValue(30)), state.getHeap().getContent());
    }
}