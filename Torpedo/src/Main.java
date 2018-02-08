import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        boolean[][] gamerMap1 = new boolean[10][10];
        boolean[][] gamerMap2 = new boolean[10][10];

        GameForm jatek2 = new GameForm(true, gamerMap2, gamerMap1, new JButton[11][11]);
        jatek2.setVisible(true);

        GameForm jatek = new GameForm(false, gamerMap1, gamerMap2, jatek2.getFields());
        jatek.setVisible(true);


    }
}
