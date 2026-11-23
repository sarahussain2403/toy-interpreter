package interpreter.model.stmt;

import java.util.Optional;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;

import java.util.Optional;

public interface IStmt {
    Optional<PrgState> execute(PrgState state) throws MyException;
}