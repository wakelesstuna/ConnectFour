import model.UserDatabase;

public class Main {
    public static void main(String[] args) {
        UserDatabase.load();
        new LoginMenuView();
    }
}
