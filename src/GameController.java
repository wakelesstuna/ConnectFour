import model.Tile;
import model.User;
import model.UserDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

public class GameController extends JFrame implements ActionListener {
    private User redPlayer;
    private User yellowPlayer;
    private int tileCounter = 0;
    private final int[][] tileGrid = new int[6][7];
    private boolean isRedTurn = true;

    private final LoginMenuView loginMenuView;
    private final GameBoardView gameBoardView;

    public GameController(LoginMenuView loginMenuView) {
        this.loginMenuView = loginMenuView;
        this.gameBoardView = new GameBoardView(this);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, loginMenuView);
        setTitle("Logga in spelare 1");
        setSize(new Dimension(1000, 800));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void placeTile(int column) {
        for (int row = 0; row < 6; row++) {
            if (tileGrid[row][column] == Tile.EMPTY.getI()) {
                tileGrid[row][column] = isRedTurn ? Tile.RED.getI() : Tile.YELLOW.getI();
                gameBoardView.getButtons()[row][column].setIcon(isRedTurn ? GameBoardView.RED_TILE : GameBoardView.YELLOW_TILE);

                tileCounter++;
                if (hasWon(row, column)) {
                    processResult(true);
                    return;
                } else if (tileCounter == 42) {
                    processResult(false);
                    return;
                }
                isRedTurn = !isRedTurn;
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Kolumnen är redan full! Placera någon annanstans");
    }

    public boolean hasWon(int placedRow, int placedColumn) {
        int correctColor = isRedTurn ? Tile.RED.getI() : Tile.YELLOW.getI();

        int startRow = Math.max(placedRow - 3, 0);
        int endRow = Math.min(placedRow + 3, 5);
        int startColumn = Math.max(placedColumn - 3, 0);
        int endColumn = Math.min(placedColumn + 3, 6);

        //Check left to right win
        if (checkHorizontalWin(startColumn, endColumn, placedRow, correctColor)) {
            return true;
        }

        //Check bottom to top win
        if (checkVerticalWin(startRow, endRow, placedColumn, correctColor)) {
            return true;
        }

        //Check top left to bottom right win
        if (checkLeftUpWin(startRow, endRow, startColumn, endColumn,
                placedRow, placedColumn, correctColor)) {
            return true;
        }
        //Check bottom left to top right win
        if (checkLeftDownWin(startRow, endRow, startColumn, endColumn,
                placedRow, placedColumn, correctColor)) {
            return true;
        }
        return false;
    }

    private boolean checkHorizontalWin(int lowColumn, int highColumn, int placedRow, int correctColor) {
        int inARowCounter = 0;
        while (lowColumn <= highColumn) {
            inARowCounter = tileGrid[placedRow][lowColumn] == correctColor ? inARowCounter + 1 : 0;
            if (inARowCounter == 4) {
                return true;
            }
            lowColumn++;
        }
        return false;
    }

    private boolean checkVerticalWin(int lowRow, int highRow, int placedColumn, int correctColor) {
        int inARowCounter = 0;
        while (lowRow <= highRow) {
            inARowCounter = tileGrid[lowRow][placedColumn] == correctColor ? inARowCounter + 1 : 0;
            if (inARowCounter == 4) {
                return true;
            }
            lowRow++;
        }
        return false;
    }

    private boolean checkLeftUpWin(int lowRow, int highRow, int lowColumn, int highColumn,
                                   int placedRow, int placedColumn, int correctColor) {
        int inARowCounter = 0;


        int columnDifference = lowColumn - placedColumn;
        int rowDifference =  lowRow - placedRow;

        int columnRowDifference = columnDifference - rowDifference;

        if (columnRowDifference < 0) lowColumn -= columnRowDifference;


        while (lowRow <= highRow && lowColumn <= highColumn) {
            inARowCounter = ((tileGrid[lowRow][lowColumn] == correctColor) ? inARowCounter + 1 : 0);
            if (inARowCounter == 4) {
                return true;
            }
            lowColumn++;
            lowRow++;
        }
        return false;
    }

    private boolean checkLeftDownWin(int lowRow, int highRow, int lowColumn, int highColumn,
                                     int placedRow, int placedColumn, int correctColor) {
        int inARowCounter = 0;

        int columnDifference = lowColumn - placedColumn;
        int rowDifference = placedRow - highRow;

        int columnRowDifference = columnDifference - rowDifference;

        if (columnRowDifference > 0) highRow -= columnRowDifference;
        else lowColumn -= columnRowDifference;

        while (highRow >= lowRow && lowColumn <= highColumn) {
            inARowCounter = ((tileGrid[highRow][lowColumn] == correctColor) ? inARowCounter + 1 : 0);
            if (inARowCounter == 4) {
                return true;
            }
            lowColumn++;
            highRow--;
        }
        return false;
    }

    public void processResult(boolean isWon) {
        if (!isWon) {
            redPlayer.getGameStats().addTie();
            yellowPlayer.getGameStats().addTie();
        } else if (isRedTurn) {
            redPlayer.getGameStats().addWin();
            yellowPlayer.getGameStats().addLoss();
        } else {
            yellowPlayer.getGameStats().addWin();
            redPlayer.getGameStats().addLoss();
        }
        gameBoardView.getButtonList().forEach(e -> e.removeActionListener(this));
        UserDatabase.save();
        JOptionPane.showMessageDialog(this, getHighScoreString(), "Highscore", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    public void addUser(User user) {
        if ((redPlayer == null)) {
            redPlayer = user;
            setTitle("Logga in spelare 2");
        } else {
            yellowPlayer = user;
            setTitle("4 i rad");
            startGame();
        }
    }

    private void startGame() {
        remove(loginMenuView);
        add(gameBoardView);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 7; column++) {
                if (e.getSource() == gameBoardView.getButtons()[row][column]) {
                    placeTile(column);
                }
            }
        }
    }

    public boolean isRedTurn() {
        return isRedTurn;
    }

    public String getHighScoreString(){
        StringBuilder highScore = new StringBuilder();

        List<User> sortedUsers = UserDatabase.getUserList();
        sortedUsers.sort(Collections.reverseOrder());
        for (int i = 0; i < Math.min(sortedUsers.size(), 10); i++) {
            if (sortedUsers.get(i).getGameStats().getWins() == 0) {
                break;
            }
            highScore.append(String.format("%d: %s %s%n", i + 1, sortedUsers.get(i).getUserName(), sortedUsers.get(i).getGameStats().toString()));
        }
        return highScore.toString();
    }

    public GameBoardView getGameBoardPanel() {
        return gameBoardView;
    }
}

