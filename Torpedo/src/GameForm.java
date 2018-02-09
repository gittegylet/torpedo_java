//import com.sun.org.apache.bcel.internal.generic.RET;

import javax.lang.model.util.ElementScanner6;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;


public class GameForm extends JFrame {

    boolean reGame;
    int xFreeWays;    //.... segéd!

    long enemyHitsX;

    int clicks;
    int exClicks;
    int enemyClicks;
    boolean[][] ownMap;
    boolean[][] enemyMap;

    int eClicks = 0;
    int ownHits = 0;


    int shipCounter;
    int enemyShipCount;

    List<String> enemyHits;

    
    static Random vel = new Random();

    String[] hajok = new String[] {"**", "***", "****", "*****"};

    private JButton[][] fields = new JButton[11][11];

    JButton[][] enemyFields;

    private int maxFreeWays = 4;


    public static synchronized void playSound(final String path) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }


    public GameForm(boolean intelliEnemy, boolean[][] gamerMap1, boolean[][] gamerMap2, JButton[][] enemyFields){

        //do {

            clicks = 0;
            exClicks = 0;
            enemyClicks = 0;
            shipCounter = 0;
            enemyShipCount = 0;

            enemyHitsX = 0;


            if (!intelliEnemy) {

                reGame = true;
                for (int i = 0; i < 10; i++){

                    for (int j = 0; j < 10; j++){

                        if (gamerMap2[i][j]) enemyShipCount++;

                    }

                }

            }
            else reGame = false;


            enemyHits = new ArrayList<String>();

            this.enemyFields = enemyFields;

            ownMap = gamerMap1;
            enemyMap = gamerMap2;

            setMap(6, ownMap);
            if (intelliEnemy) setMap(6, enemyMap);

            initGUI(intelliEnemy, ownMap, enemyMap, this.enemyFields);


        //}
        //while (reGame);

    }

    private void popUp(String message, boolean gameOver){
        if (!gameOver) JOptionPane.showMessageDialog(null, message);
        else JOptionPane.showMessageDialog(null, message, "GAME OVER", 0);
    }


    public void setMap(int hajokSzama, boolean[][] gamerMap){


        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {

                gamerMap[i][j] = false;
            }


        }


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

            shipCounter += ujHajo.length();
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

        if (aktPos + hajo.length() > 10) return false;

        int aktPos0 = aktPos;
        if (aktPos0 - 1 >= 0) aktPos0 -= 1;

        for (int i = aktPos0; i <= aktPos + hajo.length(); i++)
        {

            if (i >= 0 && i < 10) {

                if (irany && gameArea[startYPos][i]) return false;
                else if (startYPos - 1 >= 0 && irany && gameArea[startYPos - 1][i]) return false;
                else if (startYPos + 1 < 10 && irany && gameArea[startYPos + 1][i]) return false;
                else if (!irany && gameArea[i][startXPos]) return false;
                else if (startXPos - 1 >=0 && !irany && gameArea[i][startXPos - 1]) return false;
                else if (startXPos + 1 < 10 && !irany && gameArea[i][startXPos + 1]) return false;

            }

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


    private int wasANearbyShip(JButton[][] fields, int x, int y, int diff){

        if (y - diff > 0 && fields[y - 1][x].getBackground() == Color.BLUE && fields[y - diff][x].getBackground() == Color.orange) return 0;        //Függőleges irányú "dupla találat" történt ...
        else if (x + diff < 11 && fields[y][x + 1].getBackground() == Color.BLUE && fields[y][x + diff].getBackground() == Color.orange) return 1;  //Vízszintes irányú "dupla találat" történt ...
        else if (y + diff < 11 && fields[y + 1][x].getBackground() == Color.BLUE && fields[y + diff][x].getBackground() == Color.orange) return 2;
        else if (x - diff > 0 && fields[y][x - 1].getBackground() == Color.BLUE && fields[y][x - diff].getBackground() == Color.orange) return 3;

        return -1;

    }


    private boolean neighborIsAShip(JButton[][] fields, int x, int y){

        if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange) return true;
        else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange) return true;
        else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange) return true;
        else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange) return true;
        else if (y - 1 > 0 && x + 1 < 11 && fields[y - 1][x + 1].getBackground() == Color.orange) return true;
        else if (y + 1 < 11 && x + 1 < 11 && fields[y + 1][x + 1].getBackground() == Color.orange) return true;
        else if (y + 1 < 11 && x - 1 > 0 && fields[y + 1][x - 1].getBackground() == Color.orange) return true;
        else if (y - 1 > 0 && x - 1 > 0 && fields[y - 1][x - 1].getBackground() == Color.orange) return true;

        return false;

    }

    private boolean neighborIsNOTAFULLShip(JButton[][] fields, int x, int y){

        // ==>>  NEM CSAK A BALRA-JOBBRA-ALATTA-FELETTE LEVŐKRE KELL VONATKOZZON, HANEM ÁTLÓSAN IS!!!!  ==>

        if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange &&
            enemyHits.contains(Integer.toString(y - 2) + Integer.toString(x - 1) + "-") &&
            fullShipSize(fields, x, y - 1, false) < -1) return false;
        else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange &&
                 enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x) + "-") &&
                fullShipSize(fields, x + 1, y, false) < -1) return false;
        else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange &&
                 enemyHits.contains(Integer.toString(y) + Integer.toString(x - 1) + "-") &&
                fullShipSize(fields, x, y + 1, false) < -1) return false;
        else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange &&
                 enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x - 2) + "-") &&
                fullShipSize(fields, x - 1, y, false) < -1) return false;
        else if (y - 1 > 0 && x + 1 < 11 && fields[y - 1][x + 1].getBackground() == Color.orange &&
                enemyHits.contains(Integer.toString(y - 2) + Integer.toString(x) + "-") &&
                fullShipSize(fields, x + 1, y - 1, false) < -1) return false;
        else if (y + 1 < 11 && x + 1 < 11 && fields[y + 1][x + 1].getBackground() == Color.orange &&
                enemyHits.contains(Integer.toString(y) + Integer.toString(x) + "-") &&
                fullShipSize(fields, x + 1, y + 1, false) < -1) return false;
        else if (y + 1 < 11 && x - 1 > 0 && fields[y + 1][x - 1].getBackground() == Color.orange &&
                enemyHits.contains(Integer.toString(y) + Integer.toString(x - 2) + "-") &&
                fullShipSize(fields, x - 1, y + 1, false) < -1) return false;
        else if (y - 1 > 0 && x - 1 > 0 && fields[y - 1][x - 1].getBackground() == Color.orange &&
                enemyHits.contains(Integer.toString(y - 2) + Integer.toString(x - 2) + "-") &&
                fullShipSize(fields, x - 1, y - 1, false) < -1) return false;

        return true;

    }

    private int fullShipSize(JButton[][] fields, int x, int y, boolean clearAllPos){

        int fullSize = 0;  // Ha negatív érték (-1), akkor a hajó már elérte a "maximális" méretét...
        // (azaz kilőttük az egész hajót!)
        int X = x;
        int Y = y;

        int length = 0;

        if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1)))
            while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");

        if (X + 1 < 11 && fields[Y][X + 1].getBackground() == Color.orange) while (X + 1 < 11 && fields[Y][X + 1].getBackground() == Color.orange)
        {
            X++;
            if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");
        }
        else if (X - 1 > 0 && fields[Y][X - 1].getBackground() == Color.orange) while (X - 1 > 0 && fields[Y][X - 1].getBackground() == Color.orange)
        {
            X--;
            if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");
        }
        else if (Y + 1 < 11 && fields[Y + 1][X].getBackground() == Color.orange) while (Y + 1 < 11 && fields[Y + 1][X].getBackground() == Color.orange)
        {
            Y++;
            if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");
        }
        else if (Y - 1 > 0 && fields[Y - 1][X].getBackground() == Color.orange) while (Y - 1 > 0 && fields[Y - 1][X].getBackground() == Color.orange)
        {
            Y--;
            if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");
        }
        else { fullSize = 1; length = 1; }

        if (length != 1) {

            length = 1;

            if (X + 1 < 11 && fields[Y][X + 1].getBackground() == Color.orange) while (X + 1 < 11 && fields[Y][X + 1].getBackground() == Color.orange)
            {
                if (length == 1 && (X - 1 <= 0 || fields[Y][X - 1].getBackground() == Color.cyan)) fullSize++;
                X++;
                length++;
                if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");

            }
            else if (X - 1 > 0 && fields[Y][X - 1].getBackground() == Color.orange) while (X - 1 > 0 && fields[Y][X - 1].getBackground() == Color.orange)
            {

                if (length == 1 && (X + 1 >= 11 || fields[Y][X + 1].getBackground() == Color.cyan)) fullSize++;
                X--;
                length++;
                if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");

            }
            else if (Y + 1 < 11 && fields[Y + 1][X].getBackground() == Color.orange) while (Y + 1 < 11 && fields[Y + 1][X].getBackground() == Color.orange)
            {

                if (length == 1 && (Y - 1 <= 0 || fields[Y - 1][X].getBackground() == Color.cyan)) fullSize++;
                Y++;
                length++;
                if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");

            }
            else if (Y - 1 > 0 && fields[Y - 1][X].getBackground() == Color.orange) while (Y - 1 > 0 && fields[Y - 1][X].getBackground() == Color.orange)
            {

                if (length == 1 && (Y + 1 >= 11 || fields[Y + 1][X].getBackground() == Color.cyan)) fullSize++;
                Y--;
                length++;
                if (clearAllPos && enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) while (enemyHits.contains(Integer.toString(Y - 1) + Integer.toString(X - 1))) enemyHits.set(enemyHits.indexOf(Integer.toString(Y - 1) + Integer.toString(X - 1)), Integer.toString(Y - 1) + Integer.toString(X - 1) + "-");

            }

        }



        if (length > 1 && length != 5) {

            if (X + 1 < 11 && fields[Y][X + 1].getBackground() == Color.orange &&
                    (X - 1 <= 0 || fields[Y][X - 1].getBackground() == Color.cyan)) fullSize++;
            else if (X - 1 > 0 && fields[Y][X - 1].getBackground() == Color.orange &&
                    (X + 1 >= 11 || fields[Y][X + 1].getBackground() == Color.cyan)) fullSize++;
            else if (Y + 1 < 11 && fields[Y + 1][X].getBackground() == Color.orange &&
                    (Y - 1 <= 0 || fields[Y - 1][X].getBackground() == Color.cyan)) fullSize++;
            else if (Y - 1 > 0 && fields[Y - 1][X].getBackground() == Color.orange &&
                    (Y + 1 >= 11 || fields[Y + 1][X].getBackground() == Color.cyan)) fullSize++;

        }


        if (fullSize == 2 || length == 5) fullSize = -1;
        else fullSize = 1;    // =>  ebben az esetben a hajót még nem lőttük ki teljesen, azaz vannak ismeretlen cellái

        return length * fullSize;

    }


    public void intelliPlay(JButton[][] fields) {

        int x = 0;
        int y = 0;
        boolean talalat = false;

        int[] joIrany = {0, 0};

        int newX = 0;
        int newY = 0;

        int fullSize = 0;

        boolean wasEnemyHit = false;

        Color exColor = Color.green;
        Color exexColor = Color.green;

        enemyClicks = 0;

        int iranyTipp = -1;
        int hasANearbyShip_2 = -1;
        int hasANearbyShip_3 = -1;

        do
        {

            boolean neighborISAFULLShip = false;

            if (talalat || fields[y][x].getBackground() == Color.orange || exexColor == Color.orange)
                enemyHitsX = enemyHits.stream()
                                        .filter(h -> !h.contains("-"))
                                        .count();
            

            if (enemyHitsX > 1)
                neighborISAFULLShip = false;


            if (iranyTipp != -1)
            {
                iranyTipp = -1;
                hasANearbyShip_2 = -1;
                hasANearbyShip_3 = -1;
            }

            int szabadIrany = freeWay(fields, x, y);


            //if (fields[y][x].getBackground() == Color.orange || (joIrany[1] != 0 && talalat)) {

            //    fullSize = fullShipSize(fields, x, y, false);

            //}

            //if ((fields[y][x].getBackground() != Color.orange || fullSize < 0) && !wasEnemyHit) {

            if (enemyHitsX > 0 && joIrany[1] != 0 && (((fields[y][x].getBackground() == Color.orange || fullSize < 0) && !wasEnemyHit) ||
                (exexColor == Color.orange && (fields[y][x].getBackground() == Color.orange || talalat) && fullShipSize(fields, x, y, false) == 1))) {

                if (vel.nextInt(7) <= 4)
                {
                    wasEnemyHit = true;

                }

            }

            boolean stopSor = false;

            if (fields[y][x].getBackground() == Color.orange && joIrany[1] != 0) {

                if (joIrany[0] == 1 && y >= 1 && y < 11 && x > 1 && x < 10 && fields[y][x - 1].getBackground() == Color.orange && fields[y][x + 1].getBackground() == Color.orange) {

                    if (x <= 5) {

                        while (x < 10 && fields[y][x + 1].getBackground() == Color.orange)
                        {
                            x++;
                            if (x == 10 || (fields[y][x + 1].getBackground() != Color.BLUE)) stopSor = true;
                        }
                    }
                    else {

                        while (x > 1 && fields[y][x - 1].getBackground() == Color.orange)
                        {
                            x--;
                            if (x == 1 || (fields[y][x - 1].getBackground() != Color.BLUE)) stopSor = true;
                        }

                    }

                } //stopSor = true;
                else if (joIrany[1] == 2 && y > 1 && y < 10 && x >= 1 && x < 11 && fields[y - 1][x].getBackground() == Color.orange && fields[y + 1][x].getBackground() == Color.orange)  {

                    if (y <= 5) {

                        while (y < 10 && fields[y + 1][x].getBackground() == Color.orange)
                        {
                            y++;
                            if (y == 10 || (fields[y + 1][x].getBackground() != Color.BLUE)) stopSor = true;
                        }

                    }
                    else {

                        while (y > 1 && fields[y - 1][x].getBackground() == Color.orange)
                        {
                            y--;
                            if (y == 1 || (fields[y - 1][x].getBackground() != Color.BLUE)) stopSor = true;
                        }

                    }

                } //stopSor = true;

            }

            if (fields[y][x].getBackground() == Color.orange) fullSize = fullShipSize(fields, x, y, false);


            //if (fullSize > 0 && !stopSor && (wasEnemyHit || ((fields[y][x].getBackground() == Color.orange || talalat) && szabadIrany <= 2))){
            if (!stopSor && (wasEnemyHit || (fullSize > 0 && (fields[y][x].getBackground() == Color.orange || talalat) && joIrany[1] != 0) ||
                (exexColor == Color.orange && (fields[y][x].getBackground() == Color.orange || talalat) && fullShipSize(fields, x, y, false) == 1))){

                newX = 0;
                newY = 0;

                int i = -1;
                boolean nextField = true;


                do{

                    if (((((newX == 0 && newY == 0) || !nextField || i == -1) && (wasEnemyHit || fields[y][x].getBackground() == Color.orange)) ||
                            (exexColor == Color.orange && (fields[y][x].getBackground() == Color.orange || talalat) && fullShipSize(fields, x, y, false) == 1))
                            && enemyHitsX >= 1) {

                        do{

                            nextField = true;

                            i = vel.nextInt(enemyHits.size());

                            x = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(1))) + 1;
                            y = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(0))) + 1;

                            fullSize = fullShipSize(fields, x, y, false);

                            if (fullSize < 0 && enemyHits.get(i).length() < 3) {

                                enemyHits.set(i, Integer.toString(y - 1) + Integer.toString(x - 1) + "-");

                            }

                        }while (enemyHitsX >= 1 && fullSize < 0 && enemyHits.get(i).length() < 3);

                    }


                    szabadIrany = freeWay(fields, x, y);

                    fullSize = 0;
                    if ((y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange) ||
                            (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange) ||
                            (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange) ||
                            (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange))
                    {

                        fullSize = fullShipSize(fields, x, y, false);
                        if (fullSize < 0) {

                            if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x, y - 1, false);
                                if (fullSize < 0) {fullSize = fullShipSize(fields, x, y - 1, true);
                                enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count();}
                            }
                            else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x, y + 1, false);
                                if (fullSize < 0) {fullSize = fullShipSize(fields, x, y + 1, true);
                                enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count();}
                            }
                            else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x - 1, y, false);
                                if (fullSize < 0) {fullSize = fullShipSize(fields, x - 1, y, true);
                                enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count();}
                            }
                            else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x + 1, y, false);
                                if (fullSize < 0) {fullSize = fullShipSize(fields, x + 1, y, true);
                                enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count();}
                            }
                            

                        }

                    }

                    if (fullSize > 0 && y - 1 >= 1 && fields[y - 1][x].getBackground() == Color.orange)
                    //Ha felette volt találat, akkor következő irány lefelé...
                    {

                        if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.BLUE) { y++; newY = 1; }
                        else {

                            int x2 = x;
                            int y2 = y - 2;

                            while (y2 >= 1 && fields[y2][x2].getBackground() == Color.orange) y2--;
                            if ((y2 < 1 || fields[y2][x2].getBackground() != Color.BLUE) && fullShipSize(fields, x2, y2, false) < 0) { nextField = false; fullShipSize(fields, x2, y2, true); enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count(); } //az az eset,
                                // amikor a hajónak adott irányban már nem lehet folytatása/következő mezője!!
                            else if (fields[y2][x2].getBackground() == Color.BLUE && !enemyHits.contains(Integer.toString(y2 - 1) + Integer.toString(x2))) { enemyHits.set(i, Integer.toString(y2 - 1) + Integer.toString(x2 - 1) + "-"); enemyHits.add(Integer.toString(y2) + Integer.toString(x2 - 1)); }

                        }
                    }
                    else if (fullSize > 0 && x + 1 <= 10 && fields[y][x + 1].getBackground() == Color.orange)
                    //Ha jobbra volt találat, akkor következő irány balra...
                    {
                        if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.BLUE) { x--; newX = -1; }
                        else {

                            int x2 = x + 2;
                            int y2 = y;

                            while (x2 <= 10 && fields[y2][x2].getBackground() == Color.orange) x2++;
                            if ((x2 > 10 || fields[y2][x2].getBackground() != Color.BLUE) && fullShipSize(fields, x2, y2, false) < 0) { nextField = false; fullShipSize(fields, x2, y2, true); enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count(); } //nincs folytatása a hajónak...
                            else if (fields[y2][x2].getBackground() == Color.BLUE && !enemyHits.contains(Integer.toString(y2 - 1) + Integer.toString(x2))) { enemyHits.set(i, Integer.toString(y2 - 1) + Integer.toString(x2 - 1) + "-"); enemyHits.add(Integer.toString(y2 - 1) + Integer.toString(x2 - 2)); }

                        }
                    }
                    else if (fullSize > 0 && y + 1 <= 10 && fields[y + 1][x].getBackground() == Color.orange)
                    //Ha alatta volt találat, akkor következő irány felfelé...
                    {
                        if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.BLUE) { y--; newY = -1; }
                        else {

                            int x2 = x;
                            int y2 = y + 2;

                            while (y2 <= 10 && fields[y2][x2].getBackground() == Color.orange) y2++;
                            if ((y2 > 10 || fields[y2][x2].getBackground() != Color.BLUE) && fullShipSize(fields, x2, y2, false) < 0) { nextField = false; fullShipSize(fields, x2, y2, true); enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count(); } //nincs folytatása a hajónak...
                            else if (fields[y2][x2].getBackground() == Color.BLUE && !enemyHits.contains(Integer.toString(y2 - 1) + Integer.toString(x2))) { enemyHits.set(i, Integer.toString(y2 - 1) + Integer.toString(x2 - 1) + "-"); enemyHits.add(Integer.toString(y2 - 2) + Integer.toString(x2 - 1)); }

                        }
                    }
                    else if (fullSize > 0 && x - 1 >= 1 && fields[y][x - 1].getBackground() == Color.orange)
                    //Ha balra volt találat, akkor következő irány jobbra...
                    {
                        if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.BLUE) { x++; newX = 1; }
                        else {

                            int x2 = x - 2;
                            int y2 = y;

                            while (x2 >= 1 && fields[y2][x2].getBackground() == Color.orange) x2--;
                            if ((x2 < 1 || fields[y2][x2].getBackground() != Color.BLUE) && fullShipSize(fields, x2, y2, false) < 0) { nextField = false; fullShipSize(fields, x2, y2, true); enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count(); } //nincs folytatása a hajónak...
                            else if (fields[y2][x2].getBackground() == Color.BLUE && !enemyHits.contains(Integer.toString(y2 - 1) + Integer.toString(x2))) { enemyHits.set(i, Integer.toString(y2 - 1) + Integer.toString(x2 - 1) + "-"); enemyHits.add(Integer.toString(y2 - 1) + Integer.toString(x2)); }

                        }
                    }
                    else if (szabadIrany != 0 && fullSize >= 0){

                        int rand = -1;

                        ArrayList<Integer> iranyProbak = new ArrayList<Integer>();

                        if (fields[y][x].getBackground() == Color.orange) szabadIrany = freeWay(fields, x, y);
                        while (szabadIrany == 0 && fields[y][x].getBackground() == Color.orange) {

                            if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange)
                                while (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange) x++;
                            else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange)
                                while (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange) x--;
                            else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange)
                                while (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange) y++;
                            else if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange)
                                while (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange) y--;

                            szabadIrany = freeWay(fields, x, y);
                            if (szabadIrany == 0 && fullShipSize(fields, x, y, false) < 0)
                            {
                                fullShipSize(fields, x, y, true);
                                enemyHitsX = enemyHits.stream().filter(h -> !h.contains("-")).count();

                                if (enemyHitsX > 0) {

                                    do{

                                        i = vel.nextInt(enemyHits.size());

                                        x = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(1))) + 1;
                                        y = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(0))) + 1;

                                        fullSize = fullShipSize(fields, x, y, false);

                                        if (fullSize < 0 && enemyHits.get(i).length() < 3) {

                                            enemyHits.set(i, Integer.toString(y - 1) + Integer.toString(x - 1) + "-");

                                        }

                                    }while (enemyHitsX >= 1 && fullSize < 0 && enemyHits.get(i).length() < 3);

                                }

                                szabadIrany = freeWay(fields, x, y);
                                if (szabadIrany < 1 && enemyHits.size() == 0) {

                                    do{
                                        x = vel.nextInt(10) + 1;
                                        y = vel.nextInt(10) + 1;

                                        szabadIrany = 0;

                                        if (fields[y][x].getBackground() == Color.orange) szabadIrany = freeWay(fields, x, y);

                                        if (neighborIsAShip(fields, x, y) && !neighborIsNOTAFULLShip(fields, x, y)) neighborISAFULLShip = true;
                                        else if (neighborISAFULLShip) neighborISAFULLShip = false;

                                    } while (neighborISAFULLShip || (fields[y][x].getBackground() != Color.orange || (fields[y][x].getBackground() == Color.orange && (fullShipSize(fields, x, y, false) < 0 || szabadIrany < 1))));

                                }

                            }

                        }

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

                            Integer irany = 10 * newY + newX;
                            if (!iranyProbak.contains(irany)) iranyProbak.add(irany);


                        }while (x + newX < 1 || x + newX >= 11 || y + newY < 1 || y + newY >= 11 ||
                                fields[y + newY][x + newX].getBackground() == Color.cyan ||
                                fields[y + newY][x + newX].getBackground() == Color.orange ||
                                ((fields[y][x].getBackground() != Color.orange || fields[y][x].getBackground() != Color.orange) && joIrany[1] == 0 && neighborIsAShip(fields, x + newX, y + newY) && iranyProbak.size() < szabadIrany) ||
                                (fields[y][x].getBackground() == Color.orange && joIrany[1] != 0 && !Arrays.asList(joIrany).contains(rand) && iranyProbak.size() < szabadIrany));

                        if ((newX != 0 || newY != 0) && fields[y + newY][x + newX].getBackground() == Color.BLUE) {

                            x += newX;
                            y += newY;
                            if (exexColor == Color.orange && !enemyMap[y - 1][x - 1]) exexColor = Color.green;

                        }
                        else if (iranyProbak.size() == 4 || !((newX != 0 || newY != 0) && fields[y + newY][x + newX].getBackground() != Color.BLUE)) {

                            wasEnemyHit = false;
                            //joIrany[0] = 0;
                            //joIrany[1] = 0;
                            //if (enemyHitsX > 0) enemyHits.set(i, Integer.toString(y - 1) + Integer.toString(x - 1) + "-");
                            //nextField = true;
                            fullSize = 0;
                            //talalat = true;
                        }

                    }

                    if ((newX != 0 || newY != 0) && fields[y + newY][x + newX].getBackground() == Color.orange) fullSize = fullShipSize(fields, x, y, false);


                }while (fullSize > 0 && enemyHitsX > 0 && fields[y][x].getBackground() != Color.BLUE);


                try {
                    if (fields[y][x].getBackground() == Color.BLUE) Thread.sleep(1100);
                    else {

                        newX = 0; newY = 0;
                        //boolean exHit;
                        boolean wasShip;

                        boolean follower = false;

                        boolean nextExShip = false;


                        if (enemyHitsX > 0 && vel.nextInt(3) == 0) nextExShip = true;

                        int freeWays = 0;
                        maxFreeWays = freeWayCount(fields); //freeWay(fields, x, y);

                        do {

                            x = vel.nextInt(10) + 1;
                            y = vel.nextInt(10) + 1;

                            wasShip = neighborIsAShip(fields, x, y);

                            /*exHit = false;
                            if (wasShip && enemyHitsX > 0) {

                                int X = 0;
                                int Y = 0;

                                for (int I = 0; I < enemyHits.size() && !exHit; I++) {

                                    X = Integer.parseInt(String.valueOf(enemyHits.get(I).charAt(1))) + 1;
                                    Y = Integer.parseInt(String.valueOf(enemyHits.get(I).charAt(0))) + 1;

                                    if ((Math.abs(X - x) == 1 && Y == y) || (Math.abs(Y - y) == 1 && X == x)) exHit = true;

                                }

                            }*/

                            follower = false;
                            if (x + 1 < 11 && enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x))) follower = true;
                            else if (x - 1 > 0 && enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x - 2))) follower = true;
                            else if (y + 1 < 11 && enemyHits.contains(Integer.toString(y) + Integer.toString(x - 1))) follower = true;
                            else if (y - 1 > 0 && enemyHits.contains(Integer.toString(y - 2) + Integer.toString(x - 1))) follower = true;


                            if (freeWays < maxFreeWays || !((!nextExShip && (fields[y][x].getBackground() != Color.BLUE )) || (nextExShip))) freeWays = freeWay(fields, x, y);

                            if (neighborIsAShip(fields, x, y) && !neighborIsNOTAFULLShip(fields, x, y)) neighborISAFULLShip = true;
                            else if (neighborISAFULLShip) neighborISAFULLShip = false;

                        } while ((wasShip && !follower) || neighborISAFULLShip || (!follower && ((freeWays < maxFreeWays || (!nextExShip && fields[y][x].getBackground() != Color.BLUE)) || (freeWays < maxFreeWays - 1 || (fields[y][x].getBackground() != Color.BLUE && nextExShip)))));
                        ////} while ((wasShip && !follower) || neighborISAFULLShip || (!follower && ((freeWays < maxFreeWays || (!nextExShip && fields[y][x].getBackground() != Color.BLUE)) || (freeWays < maxFreeWays - 1 && nextExShip))));
                        //} while (neighborISAFULLShip || (!follower && ((freeWays < maxFreeWays || (!nextExShip && (fields[y][x].getBackground() != Color.BLUE) || (wasShip && !exHit))) || (freeWays < maxFreeWays - 1 || (nextExShip && (!wasShip || (wasShip && !exHit)))))));


                        if (fields[y][x].getBackground() == Color.BLUE) try {
                            Thread.sleep(1100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
            else if (!talalat || (fields[y][x].getBackground() != Color.orange && !wasEnemyHit && enemyHitsX > 0 && vel.nextInt(2) == 0) || ((talalat || fields[y][x].getBackground() == Color.orange) && szabadIrany == 0)){
                // Ha volt találat, de már minden mellette
                // levő "szomszéd" mező ki lett lőve... (felette, alatta, balra, jobbra)

                //boolean exHit;
                boolean wasShip;

                boolean follower = false;

                boolean nextExShip = false;
                if (enemyHitsX > 0 && vel.nextInt(2) == 0) nextExShip = true;

                int freeWays = 0;
                maxFreeWays = freeWayCount(fields); //freeWay(fields, x, y);

                do {

                    x = vel.nextInt(10) + 1;
                    y = vel.nextInt(10) + 1;

                    wasShip = neighborIsAShip(fields, x, y);

                    /*exHit = false;
                    if (wasShip && enemyHitsX > 0) {

                        int X = 0;
                        int Y = 0;

                        for (int i = 0; i < enemyHits.size() && !exHit; i++) {

                            X = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(1))) + 1;
                            Y = Integer.parseInt(String.valueOf(enemyHits.get(i).charAt(0))) + 1;

                            if ((Math.abs(X - x) == 1 && Y == y) || (Math.abs(Y - y) == 1 && X == x)) exHit = true;

                        }

                    }*/

                    follower = false;
                    if (x + 1 < 11 && enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x))) follower = true;
                    else if (x - 1 > 0 && enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x - 2))) follower = true;
                    else if (y + 1 < 11 && enemyHits.contains(Integer.toString(y) + Integer.toString(x - 1))) follower = true;
                    else if (y - 1 > 0 && enemyHits.contains(Integer.toString(y - 2) + Integer.toString(x - 1))) follower = true;


                    //if (freeWays < maxFreeWays || !((!nextExShip && (fields[y][x].getBackground() != Color.BLUE )) || (fields[y][x].getBackground() != Color.BLUE && nextExShip)))
                    //if (freeWays < maxFreeWays || !((!nextExShip && (fields[y][x].getBackground() != Color.BLUE )) || (nextExShip)))
                    if ((freeWays < maxFreeWays || !(!nextExShip && (fields[y][x].getBackground() != Color.BLUE) )) || (freeWays < maxFreeWays - 1 || !(fields[y][x].getBackground() != Color.BLUE && nextExShip)))
                        freeWays = freeWay(fields, x, y);

                //} while (freeWays < maxFreeWays || ((!nextExShip && (fields[y][x].getBackground() != Color.BLUE )) || (nextExShip)));
                //} while (freeWays < maxFreeWays || ((!nextExShip && (fields[y][x].getBackground() != Color.BLUE )) || (fields[y][x].getBackground() != Color.BLUE && nextExShip)));

                    if (neighborIsAShip(fields, x, y) && !neighborIsNOTAFULLShip(fields, x, y)) neighborISAFULLShip = true;
                    else if (neighborISAFULLShip) neighborISAFULLShip = false;


                } while ((wasShip && !follower) || neighborISAFULLShip || (!follower && ((freeWays < maxFreeWays || (!nextExShip && fields[y][x].getBackground() != Color.BLUE)) || (freeWays < maxFreeWays - 1 || (fields[y][x].getBackground() != Color.BLUE && nextExShip)))));
                //} while (neighborISAFULLShip || (!follower && ((freeWays < maxFreeWays || (!nextExShip && (fields[y][x].getBackground() != Color.BLUE) || (wasShip && !exHit))) || (freeWays < maxFreeWays - 1 || (fields[y][x].getBackground() != Color.BLUE && nextExShip && (!wasShip || (wasShip && !exHit)))))));


                if (fields[y][x].getBackground() == Color.BLUE) try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            else if (!stopSor || fields[y][x].getBackground() == Color.orange) {


                int rand = -1;

                ArrayList<Integer> iranyProbak = new ArrayList<Integer>();


                if (fields[y][x].getBackground() == Color.orange) szabadIrany = freeWay(fields, x, y);
                while (szabadIrany == 0 && fields[y][x].getBackground() == Color.orange) {

                    if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange)
                        while (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange) y--;
                    else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange)
                        while (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange) x--;
                    else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange)
                        while (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange) y++;
                    else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange)
                        while (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange) x++;

                    szabadIrany = freeWay(fields, x, y);
                    if (szabadIrany == 0)
                    {
                        fullShipSize(fields, x, y, true);
                        if (enemyHitsX > 0) {

                            do{

                                szabadIrany = 0;
                                x = vel.nextInt(10) + 1;
                                y = vel.nextInt(10) + 1;
                                if (fields[y][x].getBackground() == Color.orange) szabadIrany = freeWay(fields, x, y);

                                if (neighborIsAShip(fields, x, y) && !neighborIsNOTAFULLShip(fields, x, y)) neighborISAFULLShip = true;
                                else if (neighborISAFULLShip) neighborISAFULLShip = false;

                            } while (neighborISAFULLShip || (fields[y][x].getBackground() != Color.orange || (fields[y][x].getBackground() == Color.orange && (fullShipSize(fields, x, y, false) < 0 || szabadIrany < 1))));

                        }
                    }

                }


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

                    Integer irany = 10 * newY + newX;
                    if (!iranyProbak.contains(irany)) iranyProbak.add(irany);

                }while ((x + newX < 1 || x + newX >= 11 || y + newY < 1 || y + newY >= 11 ||
                        fields[y + newY][x + newX].getBackground() == Color.cyan ||
                        fields[y + newY][x + newX].getBackground() == Color.orange ||
                        //(fields[y][x].getBackground() != Color.orange && joIrany[1] == 0 && neighborIsAShip(fields, x + newX, y + newY)) ||
                        //(fields[y][x].getBackground() == Color.orange && joIrany[1] != 0 && !Arrays.asList(joIrany).contains(rand)));
                        (fields[y][x].getBackground() != Color.orange && joIrany[1] == 0 && neighborIsAShip(fields, x + newX, y + newY) && iranyProbak.size() < szabadIrany) ||
                        (fields[y][x].getBackground() == Color.orange && joIrany[1] != 0 && !Arrays.asList(joIrany).contains(rand) && iranyProbak.size() < szabadIrany)));

                if ((newX != 0 || newY != 0) && fields[y + newY][x + newX].getBackground() == Color.BLUE) {

                    x += newX;
                    y += newY;
                    if (exexColor == Color.orange && !enemyMap[y - 1][x - 1]) exexColor = Color.green;

                    if (fields[y][x].getBackground() == Color.BLUE) try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } else if (iranyProbak.size() == 4 && fields[y][x].getBackground() != Color.BLUE) {

                    //wasEnemyHit = false;
                    joIrany[0] = 0;
                    joIrany[1] = 0;
                    fullSize = 0;
                    talalat = false;

                } if (iranyProbak.size() == 4 || fields[y][x].getBackground() == Color.orange) talalat = true;



            }



            exColor = fields[y][x].getBackground();

            if (fields[y][x].getBackground() == Color.BLUE) {

                if (enemyMap[y - 1][x - 1])
                {
                    fields[y][x].setBackground(Color.orange);

                    if (fullShipSize(fields, x, y, false) < 0) fullShipSize(fields, x, y, true);

                    exexColor = Color.orange;

                    if (enemyShipCount > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/splash.wav");


                    if (!enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x - 1))) {

                        fullSize = fullShipSize(fields, x, y, false);

                        if (fullSize > 0) enemyHits.add(Integer.toString(y - 1) + Integer.toString(x - 1));
                        else if (fullSize < 0) {

                            if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x, y - 1, false);
                                if (fullSize < 0) fullSize = fullShipSize(fields, x, y - 1, true);
                            }
                            else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x, y + 1, false);
                                if (fullSize < 0) fullSize = fullShipSize(fields, x, y + 1, true);
                            }
                            else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x - 1, y, false);
                                if (fullSize < 0) fullSize = fullShipSize(fields, x - 1, y, true);
                            }
                            else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange)
                            {
                                fullSize = fullShipSize(fields, x + 1, y, false);
                                if (fullSize < 0) fullSize = fullShipSize(fields, x + 1, y, true);
                            }

                        }


                    }


                    enemyShipCount--;
                    if (enemyShipCount > 0) popUp("Hátralévő ellenséges találat: " + enemyShipCount, false);
                    else {
                        if (shipCounter > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/boomm.mp3");
                        popUp("SAJNOS EZT A JÁTSZMÁT MOST ELVESZTETTED!!!", true);

                    }

                }
                else {

                    playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/splash1.wav");
                    fields[y][x].setBackground(Color.cyan);

                    if (y - 1 > 0 && fields[y - 1][x].getBackground() == Color.orange)
                    {
                        fullSize = fullShipSize(fields, x, y - 1, false);
                        if (fullSize < 0) fullSize = fullShipSize(fields, x, y - 1, true);
                    }
                    else if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.orange)
                    {
                        fullSize = fullShipSize(fields, x, y + 1, false);
                        if (fullSize < 0) fullSize = fullShipSize(fields, x, y + 1, true);
                    }
                    else if (x - 1 > 0 && fields[y][x - 1].getBackground() == Color.orange)
                    {
                        fullSize = fullShipSize(fields, x - 1, y, false);
                        if (fullSize < 0) fullSize = fullShipSize(fields, x - 1, y, true);
                    }
                    else if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.orange)
                    {
                        fullSize = fullShipSize(fields, x + 1, y, false);
                        if (fullSize < 0) fullSize = fullShipSize(fields, x + 1, y, true);
                    }

                }

                eClicks++;
                enemyClicks++;

            }


            if (!wasEnemyHit || ((wasEnemyHit || enemyMap[y - 1][x - 1]) && exColor == Color.BLUE)){

                if (enemyMap[y - 1][x - 1]) exexColor = Color.orange;
                else if (exexColor == Color.orange) exexColor = Color.green;

                if (wasEnemyHit) {


                    if (newY != 0) { joIrany[0] = 0; joIrany[1] = 2; }       // Függőleges irányú "dupla találat" történt => Innentől csak felfele és lefele irányban próbálkozhat tovább!!!
                    else if (newX != 0) { joIrany[0] = 1; joIrany[1] = 3; }  // Vízszintes irányú "dupla találat" történt => Innentől csak balra és jobbra irányban próbálkozhat tovább!!!
                    else if (enemyMap[y - 1][x - 1]) {

                        if (x - 1 >= 1 && fields[y][x - 1].getBackground() == Color.orange) { joIrany[0] = 1; joIrany[1] = 3; }
                        else if (y + 1 <= 10 && fields[y + 1][x].getBackground() == Color.orange) { joIrany[0] = 0; joIrany[1] = 2; }
                        else if (x + 1 <= 10 && fields[y][x + 1].getBackground() == Color.orange) { joIrany[0] = 1; joIrany[1] = 3; }
                        else if (y - 1 >= 1 && fields[y - 1][x].getBackground() == Color.orange) { joIrany[0] = 0; joIrany[1] = 2; }

                    }
                    else if (!enemyMap[y - 1][x - 1]) { joIrany[0] = 0; joIrany[1] = 0; }

                }
                else if (!enemyMap[y - 1][x - 1]) { joIrany[0] = 0; joIrany[1] = 0; }


                if (!talalat || ((newX != 0 || newY != 0) && fields[y + newY][x + newX].getBackground() == Color.BLUE))
                    talalat = enemyMap[y - 1][x - 1];

            }


            if (talalat && !enemyMap[y - 1][x - 1]) talalat = false;
            else if (talalat || (enemyMap[y - 1][x - 1] && exColor == Color.BLUE)) {

                if (newX != 0) { joIrany[0] = 1; joIrany[1] = 3; }  // Vízszintes irányú "dupla találat" történt => Innentől csak balra és jobbra irányban próbálkozhat tovább stb!!!
                else if (newY != 0) { joIrany[0] = 0; joIrany[1] = 2; }  // Függőleges irányú "dupla találat" történt!
                else if (enemyMap[y - 1][x - 1]) {

                    if (y - 1 >= 1 && fields[y - 1][x].getBackground() == Color.orange) { joIrany[0] = 0; joIrany[1] = 2; }
                    else if (x + 1 <= 10 && fields[y][x + 1].getBackground() == Color.orange) { joIrany[0] = 1; joIrany[1] = 3; }
                    else if (y + 1 <= 10 && fields[y + 1][x].getBackground() == Color.orange) { joIrany[0] = 0; joIrany[1] = 2; }
                    else if (x - 1 >= 1 && fields[y][x - 1].getBackground() == Color.orange) { joIrany[0] = 1; joIrany[1] = 3; }

                }

            }


            if (joIrany[1] == 0 && fields[y][x].getBackground() == Color.orange && vel.nextInt(3) >= 1) hasANearbyShip_2 = wasANearbyShip(fields, x, y, 2);
            else if (joIrany[1] == 0 && fields[y][x].getBackground() == Color.orange && vel.nextInt(5) <= 1) hasANearbyShip_3 = wasANearbyShip(fields, x, y, 3);

            if (joIrany[1] == 0 && (hasANearbyShip_2 != -1 || hasANearbyShip_3 != -1) && enemyShipCount > 0)
            {

                if (hasANearbyShip_2 > -1) iranyTipp = hasANearbyShip_2;
                else if (hasANearbyShip_3 > -1) iranyTipp = hasANearbyShip_3;



                    if (iranyTipp == 0 && y - 1 > 0 && fields[y - 1][x].getBackground() == Color.BLUE) {

                        y--;
                        talalat = true;
                        joIrany[0] = 0;
                        joIrany[1] = 2;
                    }
                    else if (iranyTipp == 1 && x + 1 < 11 && fields[y][x + 1].getBackground() == Color.BLUE)  {

                        x++;
                        talalat = true;
                        joIrany[0] = 1;
                        joIrany[1] = 3;
                    }
                    else if (iranyTipp == 2 && y + 1 < 11 && fields[y + 1][x].getBackground() == Color.BLUE) {

                        y++;
                        talalat = true;
                        joIrany[0] = 0;
                        joIrany[1] = 2;
                    }
                    else if (iranyTipp == 3 && x - 1 > 0 && fields[y][x - 1].getBackground() == Color.BLUE) {

                        x--;
                        talalat = true;
                        joIrany[0] = 1;
                        joIrany[1] = 3;
                    }
                    else talalat = false;

                if (talalat) {

                    if (!enemyMap[y - 1][x - 1]) talalat = false;

                    if (talalat) enemyHits.add(Integer.toString(y - 1) + Integer.toString(x - 1));

                    if (fields[y][x].getBackground() == Color.BLUE) try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (talalat && enemyShipCount > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/splash.wav");
                    else if (!talalat) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/splash1.wav");

                    if (talalat) fields[y][x].setBackground(Color.orange);
                    else { fields[y][x].setBackground(Color.cyan); talalat = false; joIrany[0] = 0; joIrany[1] = 0; }

                    if (talalat && fullShipSize(fields, x, y, false) < 0) fullShipSize(fields, x, y, true);

                    if (talalat) enemyShipCount--;

                    if (enemyShipCount <= 0) {
                        if (shipCounter > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/boomm.mp3");
                        popUp("SAJNOS EZT A JÁTSZMÁT MOST ELVESZTETTED!!!", true);

                    } else if (talalat) popUp("Hátralévő ellenséges találat: " + enemyShipCount, false);

                }

            }


            if ((talalat || enemyMap[y - 1][x - 1] || fields[y][x].getBackground() == Color.orange) && !enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x - 1))) { talalat = true; enemyHits.add(Integer.toString(y - 1) + Integer.toString(x - 1)); }

            if (enemyHitsX > 0 && joIrany[1] == 0 && (exexColor == Color.orange || (enemyMap[y - 1][x - 1] && fields[y][x].getBackground() == Color.orange))) {

                final int[] n = {0, -1, -1, 0, 0};   //Miért kell ahhoz tömb, hogy a lambdás esetben használható legyen???
                enemyHits.forEach(h -> {

                    if (n[1] == -1 && n[2] == -1 && !h.contains("-") && (n[0] == 0 || vel.nextInt(5) == 0)) {

                        int X = Integer.parseInt(String.valueOf(h.charAt(1))) + 1;
                        int Y = Integer.parseInt(String.valueOf(h.charAt(0))) + 1;

                        if (fullShipSize(fields, X, Y, false) == 1) {

                            n[1] = X;
                            n[2] = Y;

                            n[0]++;

                            int diffX = 0;
                            int diffY = 0;

                            while (fields[Y + diffY][X + diffX].getBackground() != Color.BLUE) {

                                diffX = 0;
                                diffY = 0;

                                switch (vel.nextInt(4)){

                                    case 0: diffY--; break;
                                    case 1: diffX++; break;
                                    case 2: diffY++; break;
                                    case 3: diffX--; break;
                                    default:  break;

                                }

                            }

                            if (diffX != 0) { joIrany[0] = 1; joIrany[1] = 3; }
                            else if (diffY != 0) { joIrany[0] = 0; joIrany[1] = 2; }

                            if (joIrany[1] != 0) { n[3] = diffX; n[4] = diffY; }
                        }
                    }


                });

                if (joIrany[1] != 0 && n[1] != -1) { newX = n[3]; newY = n[4]; exexColor = Color.orange; talalat = true; x = n[1]; y = n[2]; }

                if (n[0] > 0)
                    System.out.print("1-es találat..");

            }


            if ((talalat || enemyMap[y - 1][x - 1] || fields[y][x].getBackground() == Color.orange) && exexColor != Color.orange) exexColor = Color.orange;

            if (fields[y][x].getBackground() == Color.orange) {

                if (x + 1 < 11 && enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x))) { talalat = true; joIrany[0] = 1; joIrany[1] = 3; }
                else if (x - 1 > 0 && enemyHits.contains(Integer.toString(y - 1) + Integer.toString(x - 2))) { talalat = true; joIrany[0] = 1; joIrany[1] = 3; }
                else if (y + 1 < 11 && enemyHits.contains(Integer.toString(y) + Integer.toString(x - 1))) { talalat = true; joIrany[0] = 0; joIrany[1] = 2; }
                else if (y - 1 > 0 && enemyHits.contains(Integer.toString(y - 2) + Integer.toString(x - 1))) { talalat = true; joIrany[0] = 0; joIrany[1] = 2; }
                else  { talalat = false; joIrany[0] = 0; joIrany[1] = 0; }

            }


        }while (talalat || joIrany[1] != 0 || fields[y][x].getBackground() == Color.orange || exColor == fields[y][x].getBackground() || exexColor == Color.orange);
        //}while (clicks - eClicks > ownHits || talalat || fields[y][x].getBackground() == Color.orange || exColor == fields[y][x].getBackground());


    }

    JPanel panel = new JPanel();


    private void initGUI(boolean intelliEnemy, boolean[][] gamerMap1, boolean[][] gamerMap2, JButton[][] enemyFields){

        if (intelliEnemy) enemyClicks = clicks;
        else enemyClicks = exClicks;

        setSize(600, 600);
        if (intelliEnemy) setTitle("Torpedó Klasszik - SAJÁT TERÜLET");
        else setTitle("Torpedó Klasszik - ELLENSÉGES TERÜLET");

        setLocationRelativeTo(null);
        if (intelliEnemy) setLocation(650, 50);
        else setLocation(50, 50);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Container area = getContentPane();

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

                    fields[i][j].setBackground(Color.BLUE);
                    fields[i][j].setForeground(Color.darkGray);

                    fields[i][j].setBorder(BorderFactory.createLineBorder(Color.black));


                    final int i2 = i;
                    final int j2 = j;

                    if (!intelliEnemy) fields[i][j].addActionListener((ActionEvent event) -> {

                        Color exColor = fields[i2][j2].getBackground();


                        if (fields[i2][j2].getBackground() == Color.BLUE) {

                            try {

                                Thread.sleep(250);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            clicks++;
                            if (ownMap[i2 - 1][j2 - 1])
                            {
                                fields[i2][j2].setBackground(Color.orange);

                                if (shipCounter > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/splash.wav");

                                shipCounter--;
                                if (shipCounter > 0) popUp("Szükséges találat: " + shipCounter, false);
                                else {
                                    if (shipCounter > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/boomm.mp3");
                                    popUp("GRATULÁLOK, EZT A JÁTSZMÁT TE NYERTED!!!", true);

                                }

                            }
                            else {

                                playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/splash1.wav");
                                fields[i2][j2].setBackground(Color.cyan);

                            }

                            //clicks++;


                        } else if (i2 > 1 && j2 > 1) playSound("C:/Users/Okoska/Documents/JAVA_projektek/Torpedo/src/sounds/clap.wav");

                        if (!ownMap[i2 - 1][j2 - 1] && exColor == Color.BLUE) {
                            intelliPlay(enemyFields);
                            //intelliPlay(enemyFields);
                            //intelliPlay(enemyFields); ...
                        }
                        else if (ownMap[i2 - 1][j2 - 1]) ownHits++;


                    });

                }

            }

        }


        if (!intelliEnemy && vel.nextInt(2) == 1) intelliPlay(enemyFields);  //50-50% hogy mi kezdhetünk vagy az ellenség

    }


    public JButton[][] getFields(){

        return fields;

    }

    private void setFields(JButton[][] fields){

        this.fields = fields;

    }


    private int freeWay(JButton[][] fields, int x, int y){

        int szabadIrany = 0;

        if (y - 1 >= 1 && fields[y - 1][x].getBackground() == Color.BLUE) szabadIrany++;
        if (y + 1 < 11 && fields[y + 1][x].getBackground() == Color.BLUE) szabadIrany++;
        if (x - 1 >= 1 && fields[y][x - 1].getBackground() == Color.BLUE) szabadIrany++;
        if (x + 1 < 11 && fields[y][x + 1].getBackground() == Color.BLUE) szabadIrany++;

        return szabadIrany;
    }


    private int freeWayCount(JButton[][] fields){

        int szabadIrany = 0;
        int newMaxFreeWays = 1;

        for (int Y = 1; Y <= 10 && newMaxFreeWays < maxFreeWays; Y++){

            for (int X = 1; X <= 10 && newMaxFreeWays < maxFreeWays; X++){

                if (fields[Y][X].getBackground() == Color.BLUE) {

                    szabadIrany = 0;
                    if (Y - 1 >= 1 && fields[Y - 1][X].getBackground() == Color.BLUE) szabadIrany++;
                    if (Y + 1 < 11 && fields[Y + 1][X].getBackground() == Color.BLUE) szabadIrany++;
                    if (X - 1 >= 1 && fields[Y][X - 1].getBackground() == Color.BLUE) szabadIrany++;
                    if (X + 1 < 11 && fields[Y][X + 1].getBackground() == Color.BLUE) szabadIrany++;

                    if (szabadIrany > newMaxFreeWays) {
                        newMaxFreeWays = szabadIrany;
                        xFreeWays = Y * 10 + X;
                    }

                }
            }
        }

        return newMaxFreeWays;
    }


}
