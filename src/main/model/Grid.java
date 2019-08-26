package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    protected final int column = 80;
    protected final int row = 50;
    protected int[][] startingGrid = new int[row][column];
    protected int[][] futureGrid;
    protected int[][] deadGrid = new int[row][column];
    protected boolean isGridDead = false;
    protected List<GridObserver> observers = new ArrayList<>();

    public void addGridObserver(GridObserver o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void notifyGridObservers() {
        for (GridObserver o : observers) {
            o.update(startingGrid);
        }
    }

    public List<GridObserver> getObservers() {
        return observers;
    }

    public int[][] getStartingGrid() {
        return startingGrid;
    }

    public int[][] getNextGrid() {
        isGridDead();
        return startingGrid;
    }

    public void setStartingGrid(int[][] state) {
        startingGrid = state;
    }

    public int[][] nextGeneration() {
        futureGrid = new int[row][column];
        for (int l = 1; l < row - 1; l++) {
            for (int m = 1; m < column - 1; m++) {
                int aliveNeighbours = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        aliveNeighbours += this.startingGrid[l + i][m + j];
                    }
                }
                aliveNeighbours -= this.startingGrid[l][m];

                rulesOfLife(l, m, aliveNeighbours);
            }
        }
        startingGrid = futureGrid;
        return futureGrid;
    }

    private void rulesOfLife(int l, int m, int aliveNeighbours) {
        if ((startingGrid[l][m] == 1) && (aliveNeighbours < 2)) {
            futureGrid[l][m] = 0;
        } else if ((startingGrid[l][m] == 1) && (aliveNeighbours > 3)) {
            futureGrid[l][m] = 0;
        } else if ((startingGrid[l][m] == 0) && (aliveNeighbours == 3)) {
            futureGrid[l][m] = 1;
        } else {
            futureGrid[l][m] = startingGrid[l][m];
        }
    }


    // EFFECTS: updates startingGrid to reflect input, if file is incompatible throws an
    //          IncompatibleFileContents exception.

    public void isGridDead() {
        if (Arrays.deepEquals(startingGrid, deadGrid)) {
            isGridDead = true;
        } else {
            notifyGridObservers();
            nextGeneration();
        }
    }


    public boolean getDeadGrid() {
        return isGridDead;
    }
}
