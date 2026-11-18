package interpreter.controller;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GarbageCollectorTest {

    private final List<IValue> symTable = List.of(
            new RefValue(3, new IntType()),
            new RefValue(2, new RefType(new IntType())));

    private final Map<Integer, IValue> heap = Map.of(
            1, new IntValue(20),
            2, new RefValue(1, new IntType()),
            3, new IntValue(30),
            4, new IntValue(7));

    @Test
    void addressesFromTheSymTableAreOnlyThoseOfReferences() {
        List<IValue> values = List.of(new IntValue(5),
                new RefValue(3, new IntType()),
                new RefValue(0, new IntType()));

        assertEquals(Set.of(3, 0), GarbageCollector.getAddrFromSymTable(values));
    }

    @Test
    void theUnsafeCollectorForgetsALocationReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);

        Map<Integer, IValue> kept = GarbageCollector.unsafeGarbageCollector(roots, heap);

        assertEquals(Set.of(2, 3), kept.keySet());
    }

    @Test
    void theSafeCollectorKeepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);

        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);

        assertEquals(Set.of(1, 2, 3), kept.keySet());
    }

    @Test
    void theSafeCollectorRemovesALocationNobodyCanReach() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);

        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);

        assertFalse(kept.containsKey(4));
    }

    @Test
    void theSafeCollectorKeepsTheValuesOfTheLocationsItKeeps() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);

        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);

        assertEquals(new IntValue(20), kept.get(1));
        assertEquals(new RefValue(1, new IntType()), kept.get(2));
        assertEquals(new IntValue(30), kept.get(3));
    }

    @Test
    void reachableAddressesFollowsAChainOfReferences() {
        Map<Integer, IValue> chain = Map.of(
                1, new IntValue(7),
                2, new RefValue(1, new IntType()),
                3, new RefValue(2, new RefType(new IntType())));

        assertEquals(Set.of(1, 2, 3), GarbageCollector.reachableAddresses(Set.of(3), chain));
    }

    @Test
    void reachableAddressesFromNoRootsIsEmpty() {
        assertTrue(GarbageCollector.reachableAddresses(Set.of(), heap).isEmpty());
    }

    @Test
    void theNullAddressKeepsNothing() {
        assertTrue(GarbageCollector.reachableAddresses(Set.of(0), heap).isEmpty());
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(0), heap).isEmpty());
    }

    @Test
    void anAddressThatIsNotInTheHeapIsIgnored() {
        assertTrue(GarbageCollector.reachableAddresses(Set.of(99), heap).isEmpty());
    }

    @Test
    void anEmptyHeapStaysEmpty() {
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(1), Map.of()).isEmpty());
    }
}