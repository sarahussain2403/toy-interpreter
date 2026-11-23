package interpreter.model.stmt;

import java.util.Optional;

import interpreter.model.PrgState;

public record CompStmt(IStmt first, IStmt second) implements IStmt {
    @Override
    public Optional<PrgState> execute(PrgState state) {
        state.getExeStack().push(second);
        state.getExeStack().push(first);
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "(" + first + "; " + second + ")";
    }
}