package ui;

import model.Grid;
import model.exceptions.IncompatibleFileContents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class LoadedGrid extends Grid {
    private ArrayList<Integer> input;
    private boolean loaded = false;

    // EFFECTS: places each line read from a file into a list, then reads each line to
    //          ensure validity, and if valid, adds it to input; if not valid, throws an
    //          IncompatibleFileContents exception
    public void load(String filename) throws IOException, IncompatibleFileContents {
        input = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get("data/" + filename));
        for (String line : lines) {
            for (int i = 0; i <= line.length() - 1; i++) {
                String k = line.substring(i, i + 1);
                if (k.equals("1") || k.equals("0")) {
                    int value = parseInt(k);
                    input.add(value);
                } else {
                    throw new IncompatibleFileContents();
                }
            }
        }
    }

    public int[][] getLoadedStartingGrid() {
        return startingGrid;
    }

    public boolean getLoaded() {
        return loaded;
    }


    // EFFECTS: if loaded is false, loads the updated input to the grid and sets loaded to true
    //          otherwise, does nothing.
    public void loadSuccessfully() {
        if (!loaded) {
            loadToGrid();
            loaded = true;
        }
    }

    // EFFECTS: updates startingGrid to reflect input, if file is incompatible throws an
    //          IncompatibleFileContents exception.
    public void loadToGrid() {
        int inputIndex = 0;
        try {
            if (input.size() != row * column) {
                throw new IncompatibleFileContents();
            } else {
                for (int r = 0; r < row; r++) {
                    for (int c = 0; c < column; c++) {
                        startingGrid[r][c] = input.get(inputIndex++);
                    }
                }
            }
        } catch (IncompatibleFileContents e) {
            System.out.println("Exiting game...");
        }
    }
}
