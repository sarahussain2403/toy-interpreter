package interpreter.model.stmt;

import java.util.Optional;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;

public record WhileStmt(IExp cond, IStmt body) implements IStmt {

    @Override
    public Optional<PrgState> execute(PrgState state) throws MyException {
        IValue value = cond.eval(state.getSymTable(), state.getHeap());
        if (!(value instanceof BoolValue(boolean condition)))
            throw new StatementException("condition " + cond + " is not a boolean");

        if (condition) {
            state.getExeStack().push(this);
            state.getExeStack().push(body);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "(while (" + cond + ") " + body + ")";
    }
}