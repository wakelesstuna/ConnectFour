import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginMenuPanel extends JPanel implements ActionListener {
    private final JTextField userNameField = new JTextField("Enter username");
    private final JTextField passwordField = new JTextField("Enter password");
    private final JButton newUserButton = new JButton("Create new user");
    private final JButton confirmLoginButton = new JButton("Log in");
    private final JButton highScoreButton = new JButton("Highscore");
    private final JButton resetHighScore = new JButton("Reset Highscore");

    private final JLabel outputLabel = new JLabel("Välkommen till världens bästa 4-i-rad spel!");
    private final JLabel blankLabel = new JLabel("   ");

    private final Game game;


    public LoginMenuPanel() {
        this.game = new Game(this);
        setLayout(new GridLayout(5, 1));
        add(outputLabel);
        add(blankLabel);
        add(userNameField);
        add(passwordField);
        add(newUserButton);
        newUserButton.addActionListener(this);
        add(confirmLoginButton);
        confirmLoginButton.addActionListener(this);
        add(highScoreButton);
        highScoreButton.addActionListener(this);
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
        game.addUser(user);
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
        JOptionPane.showMessageDialog(this, game.getHighScoreString(),
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
