package interpreter.repository;

import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryLogTest {

    @TempDir
    Path dir;

    @Test
    void logWritesTheStateToTheFile() throws Exception {
        Path logFile = dir.resolve("log.txt");
        var repo = new Repository(PrgState.of(new NopStmt()), logFile.toString());

        repo.logPrgStateExec();

        String log = Files.readString(logFile);
        assertTrue(log.contains("ExeStack:\nnop\n"));
    }

    @Test
    void loggingTwiceAppendsInsteadOfOverwriting() throws Exception {
        Path logFile = dir.resolve("log.txt");
        var repo = new Repository(PrgState.of(new NopStmt()), logFile.toString());

        repo.logPrgStateExec();
        repo.logPrgStateExec();

        String log = Files.readString(logFile);
        assertEquals(2, log.split("ExeStack:", -1).length - 1);
    }

    @Test
    void anUnwritableLogPathThrowsARepositoryException() {
        Path badPath = dir.resolve("no-such-folder").resolve("log.txt");
        var repo = new Repository(PrgState.of(new NopStmt()), badPath.toString());

        assertThrows(RepositoryException.class, () -> repo.logPrgStateExec());
    }
}