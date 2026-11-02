package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStmtTest {

    @TempDir
    Path dir;

    private PrgState state;
    private Path numbers;
    private ValueExp file;

    @BeforeEach
    void setUp() throws Exception {
        numbers = dir.resolve("numbers.in");
        Files.writeString(numbers, "7\n");
        file = new ValueExp(new StringValue(numbers.toString()));
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void closeFilesLeftOpen() throws Exception {
        // Windows cannot delete a file that is still open
        for (BufferedReader r : state.getFileTable().toMap().values()) r.close();
    }

    @Test
    void openAddsTheFileToTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(new StringValue(numbers.toString())));
    }

    @Test
    void openWithANonStringThrows() {
        var stmt = new OpenRFileStmt(new ValueExp(new IntValue(3)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,
                () -> new OpenRFileStmt(file).execute(state));
    }

    @Test
    void openingAMissingFileThrows() {
        var missing = new ValueExp(new StringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class,
                () -> new OpenRFileStmt(missing).execute(state));
    }


    @Test
    void readGivesTheNextNumberThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void readGivesTheNumbersInOrder() throws Exception {
        Files.writeString(numbers, "15\n50\n");
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(15), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(50), state.getSymTable().lookup("x"));
    }

    @Test
    void readIntoAnUndeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "nope").execute(state));
    }

    @Test
    void readIntoANonIntVariableThrows() throws MyException {
        new VarDeclStmt("s", new StringType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "s").execute(state));
    }

    @Test
    void readWithANonStringExpressionThrows() {
        var stmt = new ReadFileStmt(new ValueExp(new IntValue(1)), "x");
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void readFromAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "x").execute(state));
    }

    @Test
    void readOfANonIntegerLineThrows() throws Exception {
        Files.writeString(numbers, "abc\n");
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "x").execute(state));
    }


    @Test
    void closeRemovesTheFileFromTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(new StringValue(numbers.toString())));
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class,
                () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void closeWithANonStringThrows() {
        var stmt = new CloseRFileStmt(new ValueExp(new IntValue(1)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }


    @Test
    void toStringsAreInTheProgramForm() {
        assertEquals("openRFile(varf)", new OpenRFileStmt(new VarExp("varf")).toString());
        assertEquals("readFile(varf, varc)",
                new ReadFileStmt(new VarExp("varf"), "varc").toString());
        assertEquals("closeRFile(varf)", new CloseRFileStmt(new VarExp("varf")).toString());
    }
}