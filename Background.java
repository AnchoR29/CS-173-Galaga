import java.awt.*;
import java.util.*;
import java.io.*;

public class Background{
	final int NUM_STARS = 28;
	final int SPEED = 3;
	Point[] stars = new Point[NUM_STARS];
	Dimension d;

	// Constructor

	public Background(Dimension d){
		this.d = d;

		for(int i = 0; i < NUM_STARS; i++){
			int tempX, tempY;
			Point tempPoint;

			tempX = 25 + i * 25;
			tempY = (int) ((Math.random() * d.height - 1) + 1);

			tempPoint = new Point(tempX, tempY);
			stars[i] =  tempPoint;
		}
	}

	public void moveBackground(){
		for(int i = 0; i < NUM_STARS; i++){
			Point tempPoint = stars[i];

			if(tempPoint.y + 1 > d.height - (SPEED * 2)){
				tempPoint.y = 0;
			}
			else{
				tempPoint.y += SPEED;
			}
		}
	}

	public void drawBackground(Graphics g){
		g.setColor(Color.WHITE);
		for(int i = 0; i < NUM_STARS; i++){
			Point tempPoint = stars[i];
			g.drawRect(tempPoint.x, tempPoint.y, 1,1);
		}
	}
}