import java.util.Vector;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.net.*;

public class EnemyRed extends Sprites
{
    //********************************************************************
    //  Constant
    //********************************************************************
    //  Score for each kill of this type of ship
    final static int SCORE = 100;

    //  Class ID Variable Counter - Used mainly in debugging
    static int ID = 1;
    //  Each instance has its own ID variable
    private int myID;

    //********************************************************************
    //  Constructor - Assigns Variables as needed and starts the massive
    //    for-loops that initialize the image
    //********************************************************************
    public EnemyRed(int x, int y, BufferedImage image){
        // Assigns position of upper-left corner
        this.x = x;
        this.y = y;
        this.image = image;

        //  Assigns positions of the initial position of this on screen
        this.initialX = x;
        this.initialY = y;

        //  Assigns and updates the ID Variables
        myID = ID;
        ID++;

        // Creates dimension
        d = new Dimension(47, 42);
    }

    public void drawEnemyRed(Graphics g){
        g.drawImage(this.image, x, y, null);
        setBounds(this.x,this.y,47,42);
    }

    //********************************************************************
    //  Method that returns the individual ID of this class
    //********************************************************************
    public int getID(){
        return myID;
    }

    //********************************************************************
    //  Method that returns the score for killing one instance of this
    //    class
    //********************************************************************
    public int getScore(){
        return SCORE;
    }
}
