package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadHeapExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();
    private final IHeap heap = new MyHeap();

    @Test
    void readsTheValueAtTheAddress() throws MyException {
        int address = heap.allocate(new IntValue(20));
        symTable.put("v", new RefValue(address, new IntType()));

        IValue result = new ReadHeapExp(new VarExp("v")).eval(symTable, heap);

        assertEquals(new IntValue(20), result);
    }

    @Test
    void readsThroughTwoLevelsOfReferences() throws MyException {
        int inner = heap.allocate(new IntValue(20));
        int outer = heap.allocate(new RefValue(inner, new IntType()));
        symTable.put("a", new RefValue(outer, new RefType(new IntType())));

        IExp exp = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));

        assertEquals(new IntValue(20), exp.eval(symTable, heap));
    }

    @Test
    void worksInsideAnArithmeticExpression() throws MyException {
        int inner = heap.allocate(new IntValue(20));
        int outer = heap.allocate(new RefValue(inner, new IntType()));
        symTable.put("a", new RefValue(outer, new RefType(new IntType())));

        IExp exp = new ArithExp(ArithOp.ADD,
                new ReadHeapExp(new ReadHeapExp(new VarExp("a"))),
                new ValueExp(new IntValue(5)));

        assertEquals(new IntValue(25), exp.eval(symTable, heap));
    }

    @Test
    void anExpressionThatIsNotAReferenceThrows() {
        IExp exp = new ReadHeapExp(new ValueExp(new IntValue(3)));
        assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
    }

    @Test
    void anAddressThatIsNotInTheHeapThrows() {
        symTable.put("v", new RefValue(5, new IntType()));
        IExp exp = new ReadHeapExp(new VarExp("v"));
        assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
    }

    @Test
    void theNullReferenceThrows() {
        symTable.put("v", new RefValue(0, new IntType()));
        IExp exp = new ReadHeapExp(new VarExp("v"));
        assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
    }

    @Test
    void anUndefinedVariableThrows() {
        IExp exp = new ReadHeapExp(new VarExp("nope"));
        assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
    }

    @Test
    void toStringIsInTheProgramForm() {
        assertEquals("rH(v)", new ReadHeapExp(new VarExp("v")).toString());
    }
}