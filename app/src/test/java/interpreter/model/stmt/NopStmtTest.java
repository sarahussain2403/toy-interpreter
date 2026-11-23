package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NopStmtTest {
    @Test
    void nopLeavesTheStateUnchanged() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().pop();

        Optional<PrgState> result = new NopStmt().execute(state);

        assertTrue(result.isEmpty());
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(0, state.getOut().size());
        assertFalse(state.getSymTable().isDefined("x"));
    }
}