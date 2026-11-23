package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class NoNewThreadTest {

    @TempDir
    Path dir;

    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        new VarDeclStmt("x", new IntType()).execute(state);
        new VarDeclStmt("r", new RefType(new IntType())).execute(state);
    }

    @Test
    void nopCreatesNoThread() throws MyException {
        assertTrue(new NopStmt().execute(state).isEmpty());
    }

    @Test
    void compoundCreatesNoThread() throws MyException {
        assertTrue(new CompStmt(new NopStmt(), new NopStmt()).execute(state).isEmpty());
    }

    @Test
    void declarationCreatesNoThread() throws MyException {
        assertTrue(new VarDeclStmt("y", new IntType()).execute(state).isEmpty());
    }

    @Test
    void assignmentCreatesNoThread() throws MyException {
        assertTrue(new AssignStmt("x", new ValueExp(new IntValue(1))).execute(state).isEmpty());
    }

    @Test
    void printCreatesNoThread() throws MyException {
        assertTrue(new PrintStmt(new ValueExp(new IntValue(1))).execute(state).isEmpty());
    }

    @Test
    void ifCreatesNoThread() throws MyException {
        var stmt = new IfStmt(new ValueExp(new BoolValue(true)), new NopStmt(), new NopStmt());
        assertTrue(stmt.execute(state).isEmpty());
    }

    @Test
    void whileCreatesNoThread() throws MyException {
        var stmt = new WhileStmt(new ValueExp(new BoolValue(false)), new NopStmt());
        assertTrue(stmt.execute(state).isEmpty());
    }

    @Test
    void newCreatesNoThread() throws MyException {
        assertTrue(new NewStmt("r", new ValueExp(new IntValue(5))).execute(state).isEmpty());
    }

    @Test
    void writeHeapCreatesNoThread() throws MyException {
        new NewStmt("r", new ValueExp(new IntValue(5))).execute(state);
        assertTrue(new WriteHeapStmt("r", new ValueExp(new IntValue(6))).execute(state).isEmpty());
    }

    @Test
    void fileStatementsCreateNoThread() throws Exception {
        Path file = dir.resolve("n.in");
        Files.writeString(file, "7\n");
        var name = new ValueExp(new StringValue(file.toString()));

        assertTrue(new OpenRFileStmt(name).execute(state).isEmpty());
        assertTrue(new ReadFileStmt(name, "x").execute(state).isEmpty());
        assertTrue(new CloseRFileStmt(name).execute(state).isEmpty());
    }
}