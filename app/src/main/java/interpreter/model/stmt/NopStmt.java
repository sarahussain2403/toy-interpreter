package interpreter.model.stmt;

import java.util.Optional;

import interpreter.model.PrgState;

public record NopStmt() implements IStmt {
    @Override
    public Optional<PrgState> execute(PrgState state) {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "nop";
    }
}