package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyStackTest {
    private IStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>();
    }

    @Test
    void newStackIsEmpty() {
        assertTrue(stack.isEmpty());
    }

    @Test
    void stackIsNotEmptyAfterPush() {
        stack.push(1);
        assertFalse(stack.isEmpty());
    }

    @Test
    void popReturnsLastPushedFirst() throws ADTException {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }


    @Test
    void popOnEmptyStackThrows() {
        var ex = assertThrows(ADTException.class, () -> stack.pop());
        assertEquals("pop from an empty stack", ex.getMessage());
    }
    @Test
    void toListHasTheTopFirst() {
        IStack<Integer> s = new MyStack<>();
        s.push(1);
        s.push(2);
        s.push(3);
        assertEquals(List.of(3, 2, 1), s.toList());
    }
}