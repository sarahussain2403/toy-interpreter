package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void newListIsEmpty() {
        assertEquals(0, list.size());
    }

    @Test
    void addIncreasesTheSize() {
        list.add(10);
        list.add(20);
        assertEquals(2, list.size());
    }

    @Test
    void getReturnsItemsInTheOrderTheyWereAdded() throws ADTException {
        list.add(10);
        list.add(20);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
    }

    @Test
    void getWithIndexOutOfBoundsThrows() {
        list.add(10);
        assertThrows(ADTException.class, () -> list.get(5));
        assertThrows(ADTException.class, () -> list.get(-1));
    }
    @Test
    void toListKeepsInsertionOrder() {
        IList<Integer> l = new MyList<>();
        l.add(1);
        l.add(2);
        l.add(3);
        assertEquals(List.of(1, 2, 3), l.toList());
    }
}