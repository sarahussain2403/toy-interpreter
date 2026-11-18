package interpreter.model.stmt;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.IExp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WhileStmtTest {

    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
    }

    private void runUntilTheStackIsEmpty(PrgState program) throws MyException {
        while (!program.getExeStack().isEmpty()) {
            program.getExeStack().pop().execute(program);
        }
    }

    private IStmt countdownFrom(int start) {
        IExp positive = new RelationalExp(RelOp.GT,
                new VarExp("v"), new ValueExp(new IntValue(0)));
        IStmt decrement = new AssignStmt("v",
                new ArithExp(ArithOp.SUB, new VarExp("v"), new ValueExp(new IntValue(1))));
        IStmt body = new CompStmt(new PrintStmt(new VarExp("v")), decrement);
        return new CompStmt(new VarDeclStmt("v", new IntType()),
                new CompStmt(new AssignStmt("v", new ValueExp(new IntValue(start))),
                        new WhileStmt(positive, body)));
    }

    @Test
    void aLoopThatNeverRunsDoesNotExecuteTheBody() throws MyException {
        state = PrgState.of(countdownFrom(0));

        runUntilTheStackIsEmpty(state);

        assertEquals(0, state.getOut().size());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void aLoopThatRunsOnceExecutesTheBodyOnce() throws MyException {
        state = PrgState.of(countdownFrom(1));

        runUntilTheStackIsEmpty(state);

        assertEquals(List.of(new IntValue(1)), state.getOut().toList());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void aLoopThatRunsSeveralTimesExecutesTheBodyEachTime() throws MyException {
        state = PrgState.of(countdownFrom(4));

        runUntilTheStackIsEmpty(state);

        assertEquals(List.of(new IntValue(4), new IntValue(3),
                new IntValue(2), new IntValue(1)), state.getOut().toList());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void aTrueConditionPushesTheLoopAndThenTheBody() throws MyException {
        IStmt body = new PrintStmt(new ValueExp(new IntValue(1)));
        var loop = new WhileStmt(new ValueExp(new BoolValue(true)), body);

        loop.execute(state);

        assertSame(body, state.getExeStack().pop());
        assertSame(loop, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void aFalseConditionPushesNothing() throws MyException {
        var loop = new WhileStmt(new ValueExp(new BoolValue(false)), new NopStmt());

        loop.execute(state);

        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void aNonBooleanConditionThrows() {
        var loop = new WhileStmt(new ValueExp(new IntValue(1)), new NopStmt());
        assertThrows(StatementException.class, () -> loop.execute(state));
    }

    @Test
    void aConditionThatCannotBeEvaluatedThrows() {
        var loop = new WhileStmt(new VarExp("nope"), new NopStmt());
        assertThrows(ExpressionException.class, () -> loop.execute(state));
    }

    @Test
    void toStringIsInTheProgramForm() {
        assertEquals("(while (v) nop)", new WhileStmt(new VarExp("v"), new NopStmt()).toString());
    }
}