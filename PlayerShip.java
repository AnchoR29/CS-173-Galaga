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


public class PlayerShip extends Sprites{
    //********************************************************************
    //  Constructor - Assigns Variables as needed and starts the massive
    //    for-loops that initialize the image
    //********************************************************************
    public PlayerShip(int x, int y, BufferedImage image){
        //  Assigns position of upper-left corner
        this.x = x;
        this.y = y;
        this.image = image;

        // Creates dimension
        this.d = new Dimension(50, 37);

        //  Assigns width and height
        int w = d.width;
        int h = d.height;


        // Set image icon
        setIcon(new ImageIcon(this.image));

        // Set starting position
        setBounds(300,300,w,h);
    }

    public void drawPlayerShip(Graphics g){
        g.drawImage(this.image, x, y, null);
        setBounds(this.x,this.y,50,37);
    }


    //********************************************************************
    //  Method only implemented to prevent the class from becoming
    //    abstract.  It essentially does nothing since it is never called
    //********************************************************************
    public int getScore(){
        return 0;
    }
}
