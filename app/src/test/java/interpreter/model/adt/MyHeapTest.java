package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyHeapTest {

    private IHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MyHeap();
    }

    @Test
    void allocateReturnsAddressesOneTwoThree() {
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }

    @Test
    void getReturnsTheStoredValue() throws ADTException {
        int address = heap.allocate(new IntValue(20));
        assertEquals(new IntValue(20), heap.get(address));
    }

    @Test
    void getOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.get(1));
    }

    @Test
    void getOfAddressZeroThrows() {
        assertThrows(ADTException.class, () -> heap.get(0));
    }

    @Test
    void updateChangesTheValue() throws ADTException {
        int address = heap.allocate(new IntValue(20));
        heap.update(address, new IntValue(30));
        assertEquals(new IntValue(30), heap.get(address));
    }

    @Test
    void updateOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.update(5, new IntValue(1)));
    }

    @Test
    void isDefinedIsTrueOnlyForAllocatedAddresses() {
        int address = heap.allocate(new IntValue(20));
        assertTrue(heap.isDefined(address));
        assertFalse(heap.isDefined(address + 1));
        assertFalse(heap.isDefined(0));
    }

    @Test
    void getContentReturnsTheLocations() {
        heap.allocate(new IntValue(20));
        assertEquals(Map.of(1, new IntValue(20)), heap.getContent());
    }

    @Test
    void getContentIsAReadOnlyCopy() {
        heap.allocate(new IntValue(20));
        Map<Integer, IValue> content = heap.getContent();
        assertThrows(UnsupportedOperationException.class,
                () -> content.put(2, new IntValue(1)));
    }

    @Test
    void aCopyTakenEarlierDoesNotChangeWhenTheHeapChanges() {
        heap.allocate(new IntValue(20));
        Map<Integer, IValue> before = heap.getContent();
        heap.allocate(new IntValue(30));
        assertEquals(1, before.size());
        assertEquals(2, heap.getContent().size());
    }

    @Test
    void setContentReplacesTheContent() {
        heap.allocate(new IntValue(20));
        heap.setContent(Map.of(7, new IntValue(70)));
        assertEquals(Map.of(7, new IntValue(70)), heap.getContent());
    }

    @Test
    void addressesAreNeverReusedAfterSetContent() {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.allocate(new IntValue(30));
        heap.setContent(Map.of(3, new IntValue(30)));
        assertEquals(4, heap.allocate(new IntValue(40)));
    }

    @Test
    void toStringShowsTheContent() {
        heap.allocate(new IntValue(20));
        assertEquals("{1=20}", heap.toString());
    }
}