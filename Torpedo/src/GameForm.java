//import com.sun.org.apache.bcel.internal.generic.RET;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;


public class GameForm extends JFrame {

    int clicks;
    int exClicks;
    int enemyClicks;
    boolean[][] ownMap;
    boolean[][] enemyMap;

    boolean newAutoClick;


    static Random vel = new Random();

    String[] hajok = new String[] {"**", "***", "****", "*****"};

    private JButton[][] fields = new JButton[11][11];

    JButton[][] enemyFields;


    public GameForm(boolean intelliEnemy, boolean[][] gamerMap1, boolean[][] gamerMap2, JButton[][] enemyFields){

        //clicks = 0;
        //exClicks = 0;
        //enemyClicks = 0;
        //newAutoClick = false;

        this.enemyFields = enemyFields;

        ownMap = gamerMap1;
        enemyMap = gamerMap2;

        setMap(6, ownMap);
        setMap(6, enemyMap);

        initGUI(intelliEnemy, ownMap, enemyMap, enemyFields);

    }


    public void setMap(int hajokSzama, boolean[][] gamerMap){


        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {

                gamerMap[i][j] = false;
            }


        }

        //int hajokSzama = 6;

        String ujHajo = "*";
        int firstPosX = 0;
        int firstPosY = 0;
        boolean irany = false;

        for (int i = 0; i < hajokSzama; i++){

            boolean emptyPos = false;

            while (!emptyPos){


                ujHajo = hajok[vel.nextInt(4)];
                firstPosX = vel.nextInt(10);
                firstPosY = vel.nextInt(10);
                irany = vel.nextInt(2) == 1;  // true => vízszintes hajó!!

                emptyPos = isEmptyPos(irany, firstPosX, firstPosY, ujHajo, gamerMap);

            }

            setHajo(irany, firstPosX, firstPosY, ujHajo, gamerMap);

        }

    }


    /*public boolean isChanged(boolean[][] enemyMap){

        int clicks = 0;

        for (int i = 0; i < enemyMap.length; i++){

            for (int j = 0; j < enemyMap[0].length; j++){

                if (enemyMap[i][j]) clicks++;

            }

        }
    }*/



    public boolean isEmptyPos(boolean irany, int startXPos, int startYPos, String hajo, boolean[][] gameArea){

        int aktPos;

        if (irany) aktPos = startXPos;
        else aktPos = startYPos;

        if (aktPos + hajo.length() >= 10) return false;

        for (int i = aktPos; i < aktPos + hajo.length(); i++)
        {
            //if (i >= 10) return false;
            if (irany && gameArea[startYPos][i]) return false;
            else if (!irany && gameArea[i][startXPos]) return false;

        }

        return true;

    }

    public void setHajo(boolean irany, int startXPos, int startYPos, String hajo, boolean[][] gameArea){

        int aktPos;
        if (irany) aktPos = startXPos;
        else aktPos = startYPos;

        for (int i = aktPos; i < aktPos + hajo.length(); i++)
        {
            if (irany) gameArea[startYPos][i] = true;
            else gameArea[i][startXPos] = true;

        }


    }


    public void intelliPlay(JButton[][] fields){

        int x = 0;
        int y = 0;

        /*JButton[][] newFields = new JButton[11][11];

        for (int i = 1; i < 11; i++) {

            for (int j = 1; j < 11; j++) {

                newFields[i][j] = fields[i][j];

            }

        }*/

        do {

            x = vel.nextInt(10) + 1;
            y = vel.nextInt(10) + 1;

        } while (fields[y][x].getBackground() != Color.cyan);


        if (fields[y][x].getBackground() == Color.cyan) {
            if (ownMap[y][x]) fields[y][x].setBackground(Color.BLUE);
            else fields[y][x].setBackground(Color.orange);

            enemyClicks++;
            //newAutoClick = true;
        }

    }

    JPanel panel = new JPanel();


    private void initGUI(boolean intelliEnemy, boolean[][] gamerMap1, boolean[][] gamerMap2, JButton[][] enemyFields){

        if (intelliEnemy) enemyClicks = clicks;
        else enemyClicks = exClicks;

        setSize(600, 600);
        if (intelliEnemy) setTitle("Torpedó Klasszik - ELLENFÉL");
        else setTitle("Torpedó Klasszik - SAJÁT");

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Container area = getContentPane();
        //add(panel);

        GridLayout glay = new GridLayout(0, 11);

        fields = new JButton[11][11];
        area.setLayout(glay);

        //char[] oszlop = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        char  oszlop2 = 'A';

        for (int i = 0; i < 11; i++){

            for (int j = 0; j < 11; j++){

                if (i == 0 && j == 0) {
                    fields[i][j] = new JButton();
                    area.add(fields[i][j]);
                    fields[i][j].setBackground(Color.gray);
                    fields[i][j].setForeground(Color.BLACK);

                    fields[i][j].setBorderPainted(false);
                }
                else if (i != 0 && j == 0) {
                    fields[i][j] = new JButton(Integer.toString(i));
                    area.add(fields[i][j]);
                    fields[i][j].setForeground(Color.BLACK);
                    fields[i][j].setBackground(Color.gray);

                    fields[i][j].setFont(new Font("Monaco", Font.PLAIN, 15));
                    fields[i][j].setBorderPainted(false);
                }
                else if (i == 0 && j != 0) {
                    //fields[i][j] = new JButton(Character.toString(oszlop[j - 1]));
                    fields[i][j] = new JButton(Character.toString(oszlop2++));
                    area.add(fields[i][j]);

                    fields[i][j].setBackground(Color.gray);
                    fields[i][j].setForeground(Color.BLACK);

                    fields[i][j].setFont(new Font("Monaco", Font.PLAIN, 15));
                    fields[i][j].setBorderPainted(false);
                }
                else {

                    fields[i][j] = new JButton();
                    area.add(fields[i][j]);

                    /*if (ownMap[i-1][j-1]) {
                        fields[i][j].setBackground(Color.BLUE);

                    }
                    else fields[i][j].setBackground(Color.WHITE);*/

                    fields[i][j].setBackground(Color.cyan);
                    fields[i][j].setForeground(Color.darkGray);

                    //fields[i][j].setBorderPainted(true);

                    fields[i][j].setBorder(BorderFactory.createLineBorder(Color.black));


                    final int i2 = i;
                    final int j2 = j;

                    if (!intelliEnemy) fields[i][j].addActionListener((ActionEvent event) -> {

                        //JButton jb = (JButton) event.getSource();

                        Color exColor = fields[i2][j2].getBackground();

                        if (fields[i2][j2].getBackground() == Color.cyan) {
                            if (ownMap[i2-1][j2-1]) fields[i2][j2].setBackground(Color.BLUE);
                            else fields[i2][j2].setBackground(Color.orange);

                            //intelliPlay(enemyFields);

                            //clicks++;
                            //newAutoClick = true;
                        }

                        if (exColor == Color.cyan) intelliPlay(enemyFields);  // -> Kattintásonként egyszer kellene
                        //                                                          az ellenfélnek tippelni!!
                        //intelliPlay(enemyFields);


                    });


                }

            }

        }


        /*if (intelliEnemy) {

            while (true){

                while (enemyClicks == clicks);
                intelliPlay(fields);

            }
        }*/




    }

    public JButton[][] getFields(){

        return fields;

    }

    public void setFields(JButton[][] fields){

        this.fields = fields;

    }


}
