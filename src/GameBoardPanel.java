import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameBoardPanel extends JPanel {
    public static final ImageIcon EMPTY_TILE = formatIcon(new ImageIcon("src\\images\\emptyTile.png"), 100,100);
    public static final ImageIcon YELLOW_TILE = formatIcon(new ImageIcon("src\\images\\yellowTile.png"), 100, 100);
    public static final ImageIcon YELLOW_ARROW_TILE = formatIcon(new ImageIcon("src\\images\\yellowArrowTile.png"), 100, 100);
    public static final ImageIcon RED_TILE = formatIcon(new ImageIcon("src\\images\\redTile.png"), 100, 100);
    public static final ImageIcon RED_ARROW_TILE = formatIcon(new ImageIcon("src\\images\\redArrowTile.png"), 100, 100);

    private final JButton[][] buttons = new JButton[6][7];
    private final List<JButton> buttonList = new ArrayList<>();

    private final Game game;

    public GameBoardPanel(Game game) {
        this.game = game;
        setBackground(Color.BLUE);
        setLayout(new GridLayout(6, 7));
        createButtons();
        repaint();
    }

    private void createButtons() {
        for (int row = 5; row >= 0; row--) {
            for (int column = 0; column < 7; column++) {
                buttons[row][column] = new JButton();
                buttons[row][column].addActionListener(game);
                buttons[row][column].setBackground(Color.BLUE);
                buttons[row][column].addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        JButton jButton = (JButton) e.getSource();
                        jButton.setRolloverIcon(game.isRedTurn() ? RED_ARROW_TILE : YELLOW_ARROW_TILE);
                    }
                });
                buttons[row][column].setIcon(EMPTY_TILE);
                buttons[row][column].setFocusPainted(false);
                buttons[row][column].setContentAreaFilled(false);
                buttons[row][column].setBorder(BorderFactory.createLineBorder(Color.BLUE));
                buttonList.add(buttons[row][column]);
                add(buttons[row][column]);
            }
        }
    }

    public JButton[][] getButtons() {
        return buttons;
    }

    public static ImageIcon formatIcon(ImageIcon oldImageIcon, int width,
                                int height) {
        Image oldImage = oldImageIcon.getImage();
        Image newImg = oldImage.getScaledInstance(width, height,
                java.awt.Image.SCALE_SMOOTH);

        return new ImageIcon(newImg);
    }

    public List<JButton> getButtonList() {
        return buttonList;
    }

}
