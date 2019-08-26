package tests;

import model.Grid;
import ui.LoadedGrid;
import model.GridObserver;
import model.exceptions.IncompatibleFileContents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GridTest implements GridObserver {

    private Grid notifyGrid;
    private Grid testDeadGrid;
    private LoadedGrid testAliveGrid1;
    private LoadedGrid testAliveGrid2;
    private int[][] currentState;
    private List<GridObserver> testObservers;

    @BeforeEach
    public void testSetupGrid() {
        testDeadGrid = new Grid();
        testAliveGrid1 = new LoadedGrid();
        testAliveGrid2 = new LoadedGrid();
//        notifyGrid = new Grid();
//        notifyGrid.addGridObserver(this);
    }

    @Test
    void testAddGridObserver() {
        testObservers = new ArrayList<>();
        assertEquals(0, testDeadGrid.getObservers().size());
        testDeadGrid.addGridObserver(this);
        assertEquals(1,testDeadGrid.getObservers().size());
        testDeadGrid.addGridObserver(this);
        assertEquals(1,testDeadGrid.getObservers().size());
    }

    @Test
    void notifyGridObservers() {
        GridObserver o = grid -> {

        };
        try {
            testAliveGrid1.addGridObserver(o);
            testAliveGrid1.load("CrazyPreset");
            testAliveGrid1.loadToGrid();
            testAliveGrid1.notifyGridObservers();

        } catch (IOException | IncompatibleFileContents e) {
            e.printStackTrace();
        }


    }

    @Test
    void setStartingGrid() {
        try {
            testAliveGrid1.load("UnendingPreset1");
        } catch (IOException | IncompatibleFileContents e) {
            e.printStackTrace();
        }
        testAliveGrid1.loadToGrid();

        testDeadGrid.setStartingGrid(testAliveGrid1.getNextGrid());
        assertFalse(testDeadGrid.getDeadGrid());
    }

    @Test
    void testGetDeadGrid() {
        assertFalse(testDeadGrid.getDeadGrid());
    }

    @Test
    void testIsGridDead() {
        testAliveGrid1.isGridDead();
        assertTrue(testAliveGrid1.getDeadGrid());

        testDeadGrid.isGridDead();
        assertTrue(testDeadGrid.getDeadGrid());
    }

    @Test
    void nextGeneration() {
        try {
            testAliveGrid1.load("UnendingPreset1");
        } catch (IOException | IncompatibleFileContents e) {
            e.printStackTrace();
        }
        try {
            testAliveGrid2.load("UnendingPreset2");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleFileContents incompatibleFileContents) {
            incompatibleFileContents.printStackTrace();
        }

        assertTrue(Arrays.deepEquals(testAliveGrid1.nextGeneration(),testAliveGrid2.getStartingGrid()));
    }

    @Test
    void testException() {
        try {
            testAliveGrid1.load("Incompatible");
            fail("Should throw an IncompatibleFileContents exception!");
        } catch (IOException e) {
            fail("Wrong exception!");
        } catch (IncompatibleFileContents incompatibleFileContents) {

        }
    }


    @Override
    public void update(int[][] grid) {
        currentState = grid;
    }
}