//import com.sun.org.apache.bcel.internal.generic.RET;

import javax.lang.model.util.ElementScanner6;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;


public class GameForm extends JFrame {

    int clicks;
    int exClicks;
    int enemyClicks;
    boolean[][] ownMap;
    boolean[][] enemyMap;

    ArrayList<String> enemyHits;

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

        enemyHits = new ArrayList<String>();
        //....

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



        for (int i = aktPos - 1; i < aktPos + hajo.length(); i++)
        {
            //if (i >= 10) return false;

            if (i >= 0) {

                if (irany && gameArea[startYPos][i]) return false;
                else if (startYPos - 1 >= 0 && irany && gameArea[startYPos - 1][i]) return false;
                else if (startYPos + 1 < 10 && irany && gameArea[startYPos + 1][i]) return false;
                else if (!irany && gameArea[i][startXPos]) return false;
                else if (startXPos - 1 >=0 && !irany && gameArea[i][startXPos - 1]) return false;
                else if (startXPos + 1 < 10 && !irany && gameArea[i][startXPos + 1]) return false;

            }

            //if (irany && gameArea[startYPos][i]) return false;
            //else if (!irany && gameArea[i][startXPos]) return false;

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


    public boolean neighborIsAShip(JButton[][] fields, int x, int y){

        if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange) return true;
        else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange) return true;
        else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange) return true;
        else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange) return true;

        return false;

    }



    public void intelliPlay(JButton[][] fields){

        int x = 0;
        int y = 0;
        boolean talalat = false;

        int[] joIrany = {0, 0};

        int newX = 0;
        int newY = 0;


        do
        {


            /*JButton[][] newFields = new JButton[11][11];

            for (int i = 1; i < 11; i++) {

                for (int j = 1; j < 11; j++) {

                    newFields[i][j] = fields[i][j];

                }

            }*/

            int szabadIrany = 0;
            if (y - 1 >= 1 && fields[y - 1][x].getBackground() == Color.BLUE) szabadIrany++;
            if (szabadIrany == 0 && y + 1 < 11 && fields[y + 1][x].getBackground() == Color.BLUE) szabadIrany++;
            if (szabadIrany == 0 && x - 1 >= 1 && fields[y][x - 1].getBackground() == Color.BLUE) szabadIrany++;
            if (szabadIrany == 0 && x + 1 < 11 && fields[y][x + 1].getBackground() == Color.BLUE) szabadIrany++;

            boolean wasEnemyHit = false;

            if (enemyHits.size() > 0 && vel.nextInt(6) == 5)
            {
                wasEnemyHit = true;

            }


            if (wasEnemyHit && joIrany[0] == 0){

                newX = 0;
                newY = 0;


                do{

                    int i = 0;
                    boolean nextField = true;


                    if (enemyHits.size() > 1) i = vel.nextInt(enemyHits.size());

                    x = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(1))) + 1;
                    y = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(0))) + 1;

                    szabadIrany = 0;
                    if (y - 1 >= 1 && fields[y - 1][x].getBackground() == Color.BLUE) szabadIrany++;
                    if (szabadIrany == 0 && y + 1 < 11 && fields[y + 1][x].getBackground() == Color.BLUE) szabadIrany++;
                    if (szabadIrany == 0 && x - 1 >= 1 && fields[y][x - 1].getBackground() == Color.BLUE) szabadIrany++;
                    if (szabadIrany == 0 && x + 1 < 11 && fields[y][x + 1].getBackground() == Color.BLUE) szabadIrany++;

                    if (fields[y - 1][x].getBackground() == Color.orange)         //Ha felette volt találat, akkor következő irány lefelé...
                    {
                        if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.BLUE) { y++; newY = 1; }
                        else nextField = false;  //az az eset, amikor a hajónak adott irányban már nem lehet folytatása/következő mezője!!
                    }
                    else if (fields[y][x + 1].getBackground() == Color.orange)    //Ha jobbra volt találat, akkor következő irány balra...
                    {
                        if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.BLUE) { x--; newX = -1; }
                        else nextField = false;  //nincs folytatása a hajónak...
                    }
                    else if (fields[y + 1][x].getBackground() == Color.orange)    //Ha alatta volt találat, akkor következő irány felfelé...
                    {
                        if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.BLUE) { y--; newY = -1; }
                        else nextField = false;  //nincs folytatása a hajónak...
                    }
                    else if (fields[y][x - 1].getBackground() == Color.orange)    //Ha balra volt találat, akkor következő irány jobbra...
                    {
                        if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.BLUE) { x++; newX = 1; }
                        else nextField = false;  //nincs folytatása a hajónak...
                    }
                    else if (szabadIrany != 0){

                        int rand = -1;

                        do{

                            newX = 0;
                            newY = 0;

                            rand = vel.nextInt(4);   // 0 => Felfelé próbálkozik...
                            // 1 => Jobbra próbálkozik...
                            // 2 => Lefelé próbálkozik...
                            // 3 => Balra próbálkozik...

                            if (rand == 0) newY = -1;
                            else if (rand == 1) newX = 1;
                            else if (rand == 2) newY = 1;
                            else if (rand == 3) newX = -1;


                        }while (x + newX < 1 || x + newX >= 11 || y + newY < 1 || y + newY >= 11 ||
                                fields[y + newY][x + newX].getBackground() == Color.cyan ||
                                fields[y + newY][x + newX].getBackground() == Color.orange ||
                                (joIrany[0] == 0 && neighborIsAShip(fields, x + newX, y + newY)));

                        x += newX;
                        y += newY;

                    }


                    if (!nextField) enemyHits.remove(i);   //kivesszük az enemyHits listából!!


                }while (enemyHits.size() > 0 && fields[y][x].getBackground() != Color.BLUE);



            }
            else if (!talalat || (talalat && szabadIrany == 0)){       //Ha volt találat, de már minden mellette
                                                                       // levő "szomszéd" mező ki lett lőve... (felette, alatta, balra, jobbra)

                do {

                    x = vel.nextInt(10) + 1;
                    y = vel.nextInt(10) + 1;

                } while (fields[y][x].getBackground() != Color.BLUE || neighborIsAShip(fields, x, y));

            }
            else {


                int rand = -1;


                do{

                    newX = 0;
                    newY = 0;

                    rand = vel.nextInt(4);   // 0 => Felfelé próbálkozik...
                                                   // 1 => Jobbra próbálkozik...
                                                   // 2 => Lefelé próbálkozik...
                                                   // 3 => Balra próbálkozik...

                    switch (rand){

                        case 0: newY -= 1;  break;
                        case 1: newX += 1;  break;
                        case 2: newY += 1;  break;
                        case 3: newX -= 1;  break;
                        default:  break;

                    }

                }while (x + newX < 1 || x + newX >= 11 || y + newY < 1 || y + newY >= 11 ||
                        fields[y + newY][x + newX].getBackground() == Color.cyan ||
                        fields[y + newY][x + newX].getBackground() == Color.orange ||
                        (joIrany[0] == 0 && neighborIsAShip(fields, x + newX, y + newY)) ||
                        (talalat && joIrany[0] != 0 && !Arrays.asList(joIrany).contains(rand)));

                x += newX;
                y += newY;


            }



            Color exColor = fields[y][x].getBackground();

            if (fields[y][x].getBackground() == Color.BLUE) {

                if (enemyMap[y - 1][x - 1]) fields[y][x].setBackground(Color.orange);
                else fields[y][x].setBackground(Color.cyan);

                enemyClicks++;
                //newAutoClick = true;
            }


            if (!wasEnemyHit || (wasEnemyHit && exColor == Color.BLUE)){

                if ((talalat || wasEnemyHit) && enemyMap[y - 1][x - 1]) {

                    if (newX != 0) { joIrany[0] = 1; joIrany[1] = 3; }  // Vízszintes iranyú "dupla találat" történt => Innentől csak balra és jobbra irányban próbálkozhat tovább!!!
                    else { joIrany[0] = 0; joIrany[1] = 2; }            // Függőleges iranyú "dupla találat" történt => Innentől csak felfele és lefele irányban próbálkozhat tovább!!!

                }
                else if ((talalat || wasEnemyHit) && !enemyMap[y - 1][x - 1] && joIrany[0] != 0) { joIrany[0] = 0; joIrany[1] = 0; }

                if (!talalat && !wasEnemyHit && enemyMap[y - 1][x - 1]) enemyHits.add(Integer.toString(y - 1) + Integer.toString(x - 1));

                talalat = enemyMap[y - 1][x - 1];

            }


        }while (talalat);




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
                        fields[i][j].setBackground(Color.orange);

                    }
                    else fields[i][j].setBackground(Color.WHITE);*/

                    fields[i][j].setBackground(Color.BLUE);
                    fields[i][j].setForeground(Color.darkGray);

                    //fields[i][j].setBorderPainted(true);

                    fields[i][j].setBorder(BorderFactory.createLineBorder(Color.black));


                    final int i2 = i;
                    final int j2 = j;

                    if (!intelliEnemy) fields[i][j].addActionListener((ActionEvent event) -> {

                        //JButton jb = (JButton) event.getSource();

                        Color exColor = fields[i2][j2].getBackground();

                        if (fields[i2][j2].getBackground() == Color.BLUE) {
                            if (ownMap[i2 - 1][j2 - 1]) fields[i2][j2].setBackground(Color.orange);
                            else fields[i2][j2].setBackground(Color.cyan);

                            //intelliPlay(enemyFields);

                            //clicks++;
                            //newAutoClick = true;
                        }

                        if (!ownMap[i2 - 1][j2 - 1] && exColor == Color.BLUE) intelliPlay(enemyFields);  // -> Kattintásonként egyszer kellene
                        //                                                                                     az ellenfélnek tippelni!!
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
