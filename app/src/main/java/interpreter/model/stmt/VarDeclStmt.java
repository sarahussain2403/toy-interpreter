package interpreter.model.stmt;

import java.util.Optional;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.type.IType;

public record VarDeclStmt(String name, IType type) implements IStmt {
    @Override
    public Optional<PrgState> execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();
        if (symTable.isDefined(name)) {
            throw new StatementException("variable " + name + " is already declared");
        }
        symTable.put(name, type.defaultValue());
        return Optional.empty();
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}