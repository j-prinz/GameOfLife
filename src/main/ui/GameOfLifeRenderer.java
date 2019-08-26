package ui;

import model.Grid;
import model.GridObserver;
import model.exceptions.IncompatibleFileContents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class GameOfLifeRenderer implements ActionListener, GridObserver {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 650;
    private static final int ROWS = 50;
    private static final int COLUMNS = 80;
    private static final int CELL_SIZE = 10;
    private Grid grid = new Grid();
    private List<String> presets = new ArrayList<>();
    private JToggleButton[][] buttons;
    private int[][] original = new int[ROWS][COLUMNS];
    private int[][] currentState;
    private JFrame frame;
    private JButton saveGame;
    private JButton play;
    private JButton stop;
    private JPopupMenu loadMenu;
    private Map<String, JMenuItem> filenameMap;
    private boolean stopPressed = false;
    private boolean originalSaved = false;
    private Timer timer;


    public GameOfLifeRenderer() {
        grid.addGridObserver(this);
        frame = new JFrame("Conway's Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ActionListener sv = this;
        saveGame = new JButton("Save Game");
        saveGame.setBackground(Color.white);
        saveGame.setForeground(Color.decode("#663399"));
        saveGame.setBounds(400, 0, 100, 20);
        saveGame.addActionListener(sv);


        JButton loadGame = new JButton("Load Game");
        loadGame.setBackground(Color.white);
        loadGame.setForeground(Color.decode("#663399"));
        loadGame.setBounds(500, 0, 100, 20);

        ActionListener al = this::showPopup;
        loadGame.addActionListener(al);

        ActionListener selectLoad = this;
        loadPresetNames();
        filenameMap = new HashMap<>();
        loadMenu = new JPopupMenu();
        for (String s : presets) {
            JMenuItem filename = new JMenuItem(s);
            filename.addActionListener(selectLoad);
            filenameMap.put(s, filename);
            loadMenu.add(filename);
        }

        play = new JButton("▶");
        play.setBackground(Color.white);
        play.setForeground(Color.decode("#663399"));
        play.setBounds(470, 20, 30, 30);
        play.addActionListener(this);


        stop = new JButton("■");
        stop.setBackground(Color.white);
        stop.setForeground(Color.decode("#663399"));
        stop.setBounds(500, 20, 30, 30);
        stop.addActionListener(this);

        JPanel selectGamePanel = new JPanel();
        selectGamePanel.setBounds(0, 0, WIDTH, 20);
        selectGamePanel.setBackground(Color.white);
        selectGamePanel.add(saveGame);
        selectGamePanel.add(loadGame);

        JPanel controls = new JPanel();
        controls.setBounds(0, 0, WIDTH, 30);
        controls.setBackground(Color.white);
        controls.add(play);
        controls.add(stop);

        JPanel masterPanel = new JPanel();
        masterPanel.setBackground(Color.white);
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(ROWS, COLUMNS));
        gamePanel.setBackground(Color.white);
        gamePanel.setMaximumSize(new Dimension(ROWS * COLUMNS * CELL_SIZE, ROWS * CELL_SIZE));
        ActionListener clicked = this;
        buttons = new JToggleButton[ROWS][COLUMNS];
        currentState = new int[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                buttons[i][j] = new JToggleButton();
                buttons[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                buttons[i][j].setBackground(Color.black);
                UIManager.put("ToggleButton.select", Color.decode("#663399"));
                SwingUtilities.updateComponentTreeUI(buttons[i][j]);
                buttons[i][j].setSelected(false);
                buttons[i][j].addActionListener(clicked);
//                buttons[i][j].setBorderPainted(false);
                gamePanel.add(buttons[i][j]);
            }
        }

        JOptionPane.showMessageDialog(frame,
                "Welcome to Conway's Game of Life! \n\n"
                        + "As you play, please keep these rules in mind:\n"
                        + "1. Any live cell with fewer than two live neighbours dies, as if by underpopulation.\n"
                        + "2. Any live cell with two or three live neighbours lives on to the next generation.\n"
                        + "3. Any live cell with more than three live neighbours dies, as if by overpopulation.\n"
                        + "4. Any dead cell with exactly three live neighbours becomes a live cell, as if by "
                        + "reproduction.",
                "Message",
                JOptionPane.PLAIN_MESSAGE);
        JOptionPane.setRootFrame(frame);

        masterPanel.add(selectGamePanel);
        masterPanel.add(gamePanel);
        masterPanel.add(controls);
        frame.setContentPane(masterPanel);

        frame.setBackground(Color.white);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GameOfLifeRenderer();
    }

    private void showPopup(ActionEvent ae) {
        Component b = (Component) ae.getSource();
        Point p = b.getLocationOnScreen();
        loadMenu.show(frame, 0, 0);
        loadMenu.setLocation(p.x, p.y + b.getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == play) {
            stopPressed = false;
            saveOriginal();
            play();
        }
        if (e.getSource() == stop) {
            stopPressed = true;
        }
        if (e.getSource() == saveGame) {
            save();

        }

        for (String s : presets) {
            if (e.getSource() == filenameMap.get(s)) {
                loadGame(s);
            }
        }
        ifJToggleButtonToggled(e);
    }

    private void saveOriginal() {
        if (!originalSaved) {
            original = currentState;
            originalSaved = true;
        }
    }

    private void ifJToggleButtonToggled(ActionEvent e) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (e.getSource() == buttons[i][j]) {
                    buttonToggled(i, j);
                }
            }
        }
    }

    private void buttonToggled(int i, int j) {
        if (buttons[i][j].isSelected()) {
            setCurrentStateAlive(i, j);
            System.out.println(Arrays.deepToString(currentState));
            System.out.println("Selecting");
        } else {
            setCurrentStateDead(i, j);
            System.out.println(Arrays.deepToString(currentState));
            System.out.println("Deselecting");
        }
    }

    private void setCurrentStateAlive(int i, int j) {
        currentState[i][j] = 1;
    }

    private void setCurrentStateDead(int i, int j) {
        currentState[i][j] = 0;
    }

    private void play() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!grid.getDeadGrid()) {
                    if (stopPressed) {
                        timer.cancel();
                    }
                    continuouslyUpdate();
                } else {
                    System.out.println("Game over!");
                    timer.cancel();
                }
            }
        }, 100, 100);
    }

    private void translateNextState() {
        System.out.println("Translating!");
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (currentState[i][j] == 1) {
                    buttons[i][j].setSelected(true);
                } else {
                    buttons[i][j].setSelected(false);
                }
            }
        }
    }

    @Override
    public void update(int[][] grid) {
        currentState = grid;
        System.out.println("Updating!");
    }

    private void continuouslyUpdate() {
        System.out.println("Timer starting");
        grid.setStartingGrid(currentState);
        update(grid.getNextGrid());
        grid.isGridDead();
        translateNextState();
    }

    private void save() {
        System.out.println("Saving");
        String result = JOptionPane.showInputDialog("Enter save name:");
        presets.add(result);
        System.out.println(result);

        Grid orig = new Grid();
        orig.setStartingGrid(original);
        try {
            save(result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void save(String filename) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                builder.append(original[i][j]);
            }
            builder.append("\n");
        }

        BufferedWriter writer = new BufferedWriter(new PrintWriter("data/" + filename, "UTF-8"));
        writer.write(builder.toString());
        writer.close();
    }

    private void loadGame(String s) {
        LoadedGrid load = new LoadedGrid();
        try {
            load.load(s);
            load.loadSuccessfully();
            currentState = load.getLoadedStartingGrid();
            translateNextState();
        } catch (IOException | IncompatibleFileContents ex) {
            ex.printStackTrace();
        }
    }

    private void loadPresetNames() {
        File folder = new File("data");
        File[] listOfPresets = folder.listFiles();

        assert listOfPresets != null;
        for (File listOfPreset : listOfPresets) {
            presets.add(listOfPreset.getName());
        }
    }



}
