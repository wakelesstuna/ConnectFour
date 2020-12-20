import model.User;
import model.UserDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class LoginMenuView extends JPanel implements ActionListener {
    private final JTextField userNameField = new JTextField("Enter username");
    private final JTextField passwordField = new JTextField("Enter password");
    private final JButton newUserButton = new JButton("Create new user");
    private final JButton confirmLoginButton = new JButton("Log in");
    private final JButton highScoreButton = new JButton("Highscore");
    private final JButton resetHighScore = new JButton("Reset Highscore");

    private final JLabel outputLabel = new JLabel("Välkommen till världens bästa 4-i-rad spel!");
    private final JLabel blankLabel = new JLabel("   ");

    private final GameController gameController;


    public LoginMenuView() {
        this.gameController = new GameController(this);
        setLayout(new GridLayout(5, 1));
        setBackground(Color.BLUE);
        outputLabel.setForeground(Color.WHITE);
        add(outputLabel);
        add(blankLabel);
        add(userNameField);
        add(passwordField);
        newUserButton.setBackground(Color.GREEN);
        add(newUserButton);
        newUserButton.addActionListener(this);
        confirmLoginButton.setBackground(new Color(114, 197, 252));
        add(confirmLoginButton);
        confirmLoginButton.addActionListener(this);
        highScoreButton.setBackground(new Color(255,215,0));
        add(highScoreButton);
        highScoreButton.addActionListener(this);
        resetHighScore.setBackground(new Color(224, 35, 252));
        add(resetHighScore);
        resetHighScore.addActionListener(this);

    }

    void createUser() {
        User user = new User()
                .setUserName(userNameField.getText().trim())
                .setPassword(passwordField.getText().trim());

        try {
            UserDatabase.addUser(user);
            outputLabel.setText(user + " added");
        } catch (IllegalArgumentException e) {
            outputLabel.setText(e.getMessage());
        }
    }

    void attemptLogin() {
        Optional<User> userOptional;
        userOptional = UserDatabase.getUser(userNameField.getText(), passwordField.getText());
        userOptional.ifPresentOrElse(this::loginSuccessful, this::loginFail);

    }

    void loginSuccessful(User user) {
        outputLabel.setText(user + " logged in");
        gameController.addUser(user);
    }

    void loginFail() {
        outputLabel.setText("Felaktigt användarnamn eller lösenord, försök igen");
    }

    private boolean checkAdminLogin(){
        String userName = JOptionPane.showInputDialog(this, "Mata in ditt användarnamn: "
                , "Endast admin kan nollställa highscoren", JOptionPane.INFORMATION_MESSAGE );
        String passWord = JOptionPane.showInputDialog(this, "Mata in ditt lösenord: "
                , "Endast admin kan nollställa highscoren", JOptionPane.INFORMATION_MESSAGE);
        return userName.equals("admin") && passWord.equals("admin");
    }

    private void resetHighScore() {
        if (checkAdminLogin()) {
            UserDatabase.getUserList().forEach(User::resetGameStats);
            UserDatabase.save();
            UserDatabase.load();
        }else {
            JOptionPane.showMessageDialog(this, "Felaktigt användarnamn eller lösenord");
        }
    }

    private void showHighScore(){
        JOptionPane.showMessageDialog(this, gameController.getHighScoreString(),
                "Highscore", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((JButton)e.getSource()).getText()){
            case "Create new user" -> createUser();
            case "Log in" -> attemptLogin();
            case "Highscore" -> showHighScore();
            case "Reset Highscore" -> resetHighScore();
        }
    }


}
