
import java.util.*;
//import java.io.*;
//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.net.*;
import java.awt.geom.CubicCurve2D;
//import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Area;

public class Main extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2185745315084852434L;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem[] menuItem = new JMenuItem[4];

	private MenuListener ml = new MenuListener();
	private KeyListen kl = new KeyListen();

	final int SCREEN_WIDTH = 700;
    final int SCREEN_HEIGHT = 700;
	final Dimension d = new Dimension(SCREEN_HEIGHT, SCREEN_WIDTH);
	final static int RANDOM_FIRE_SEED = 30;  //  Seed for firing a bullet
	private JFrame frame;
	private Background starfield;

	private Thread thread_updater;
	private Thread thread_delay;
	private Thread thread_painter;

	private Image imageBuffer;

	private PlayerShip player;
	private PlayerBullet[] pBullet = new PlayerBullet[3];

	private EnemyBlack[] black = new EnemyBlack[12];
	private EnemyGreen[] green = new EnemyGreen[12];
	private EnemyRed[] red = new EnemyRed[12];
	private EnemyBullet[] eBullet = new EnemyBullet[2];
	private BufferedImage[] playerLife = new BufferedImage[3];

	private int life = 3;
	private long strt = 0;
	private long score = 0;
	private int level = 1;
	private int enemySize = 8;
	private int attackers = 1;

	private boolean right = false;
	private boolean left = false;
//	private boolean up = false;
//	private boolean down = false;

	private boolean isGameStart = false;
	private boolean isWon = false;
	private boolean isLose = false;
	private boolean dead = false;
	private boolean isDelayRunning = false;
	private boolean isLoading = false;
	private boolean isNewGame = true;	
		
	private boolean isDeadEnemies = false;

	int randomFireTime = (int) (((Math.random() * RANDOM_FIRE_SEED) + 1) + 30);
	int currentFireTime = 0;  //  Time tracking variable

	// Constructor
	public Main(){
		
		// Set Frame properties
		frame = new JFrame();
		frame.setTitle("Galaga Woo");
		frame.setPreferredSize(new Dimension(SCREEN_HEIGHT, SCREEN_WIDTH));
		frame.setBackground(Color.BLACK);

		// Create Menu (Upper-Left of Frame)
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("Game Menu");
		menuBar.add(menu);

		// Add Items to Menu
		menuItem[0] =  new JMenuItem("Exit");

		// Add functionality to items (listener), add to menu

		menuItem[0].setMnemonic(KeyEvent.VK_D);
		menuItem[0].addActionListener(ml);
		menu.add(menuItem[0]);

		// Set menuBar as Frame's JMenuBar
		frame.setJMenuBar(menuBar);

		// Load Game assets
		loadComponents();

		// Add Keyboard listener to frame
		frame.addKeyListener(kl);

		// Add default functionalities to JFrame frame
		frame.pack();
		frame.add(this);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Set width & height of image buffer
		imageBuffer = createImage(SCREEN_HEIGHT, SCREEN_WIDTH);


		// Thread Initialization
		// Creates 3 threads for the game
		thread_updater = new Thread(this);
		thread_delay = new Thread(new Runnable() {
        public void run(){
        	while(true){

        		// Idk why but it needs this microsleep to start working for some reason.
	        	try{
	                Thread.sleep(8);
	            }
	            catch(InterruptedException e){
	            }

	        	while(isDelayRunning == true){
					//System.out.println("Once every hit");
					try{
					Thread.sleep(3500);
					}catch(InterruptedException e){
					}

					dead = false;
					isDelayRunning = false;
				}
			}
		}
		});
		thread_painter = new Thread(new Runnable(){
			public void run(){
				while(true){
					prepareImageBuffer();
					repaint();
					try{
		                Thread.sleep(8);   //  Sleep the thread for 8 milliseconds
		            }
		            catch(InterruptedException e){
		                //  Catch the potential interrupted exception and do nothing
		            }
				}
			}
		});

		// Starts the threads
		thread_updater.start();
		thread_painter.start();
		thread_delay.start();

		frame.setVisible(true);
	}

	// Thread for updating background, preparing buffer, and repainting
	public void run(){
		while(true){
			// this can be put in other threads
			starfield.moveBackground();
			if(isGameStart == true){
				score = (System.currentTimeMillis() - strt)/1000;
				moveEnemyShips(green, enemySize);
				moveEnemyShips(black, enemySize);
				moveEnemyShips(red, enemySize);
				updatePlayerPos();
				updatePlayerBullets();
				updateEnemyBullets();
			}

			// if no more enemies are moving, it means you've won right
			if(isDeadEnemies == true){
				isGameStart = false;
				isWon = true;
			}

			if(!dead){
				collisioncheck();
			}
			else{
				if(isDelayRunning == false){
					isDelayRunning = true;
				}
			}

			try{
                Thread.sleep(8);   //  Sleep the thread for 8 milliseconds
            }
            catch(InterruptedException e){
                //  Catch the potential interrupted exception and do nothing
            }
		}

	}

	public void loadComponents(){
		starfield = new Background(new Dimension(SCREEN_HEIGHT, SCREEN_WIDTH));
		player = new PlayerShip(327, 550, resize(getImage("/sprites/player.png"), 50, 37));

		if(level <= 4){
			enemySize = 8;
		}
		else if(level >= 5 && level <= 9){
			enemySize = 10;
		}
		else{
			enemySize = 12;
		}

		if( level <= 2) attackers = 1;
		else if (level >= 3 && level <7){
			attackers = 2;
		}
		else if(level >= 7 && level < 11){
			attackers = 3;
		}
		else if(level >= 11 && level <= 20){
			attackers = 4;
		}
		else{
			attackers = 5;
		}

		int size = enemySize / 2;

		BufferedImage blackimg = resize(getImage("/sprites/black.png"), 47, 42);
		for (int i = 0; i < size; i++){
			black[i] = new EnemyBlack((d.width/2)+(48*i),100, blackimg);
			black[i].setVisible(true);
		}
		for (int i = 0; i < size; i++){
			black[i+size] = new EnemyBlack((d.width/2)-(48*(i+1)),100, blackimg);
			black[i+size].setVisible(true);
		}
		BufferedImage redimg = resize(getImage("/sprites/red.png"), 47, 42);
		for (int i = 0; i < size; i++){
			red[i] = new EnemyRed((d.width/2)+(48*i),150, redimg);
			red[i].setVisible(true);
		}
		for (int i = 0; i < size; i++){
			red[i+size] = new EnemyRed((d.width/2)-(48*(i+1)),150, redimg);
			red[i+size].setVisible(true);
		}
		BufferedImage greenimg = resize(getImage("/sprites/green.png"), 47, 42);
		for (int i = 0; i < size; i++){
			green[i] = new EnemyGreen((d.width/2)+(48*i),200, greenimg);
			green[i].setVisible(true);
		}
		for (int i = 0; i < size; i++){
			green[i+size] = new EnemyGreen((d.width/2)-(48*(i+1)),200, greenimg);
			green[i+size].setVisible(true);
		}


		pBullet[0] = null;
		pBullet[1] = null;
		pBullet[2] = null;

		playerLife[0] = resize(getImage("/sprites/playerLife.png"), 50, 37);
		playerLife[1] = resize(getImage("/sprites/playerLife.png"), 50, 37);
		playerLife[2] = resize(getImage("/sprites/playerLife.png"), 50, 37);

		for (int i = 0; i < 2 ; i++ ) {
			eBullet[i] = null;
		}


		player.setVisible(true);
		setBounds(0,0,700,700);

	}

	public void clear(){
		player = null;
		for(int i = 0; i < 12; i++){
			black[i] = null;
			red[i] = null;
			green[i] = null;
		}

		pBullet[0] = null;
		pBullet[1] = null;
		pBullet[2] = null;

		eBullet[0] = null;
		eBullet[1] = null;

		life = 3;
		score = 0;
		attackers = 1;
		enemySize = 8;

		isGameStart = false;
		isWon = false;
		isLose = false;
		dead = false;
		isDelayRunning = false;
		level = 1;

		isDeadEnemies = false;
	}

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

	public void paint(Graphics g){
		g.drawImage(imageBuffer,0,0, null);

	}

	protected void prepareImageBuffer(){
		Graphics g = imageBuffer.getGraphics();

		// Clear Screen
		g.setColor(Color.BLACK);
		g.fillRect(0,0,SCREEN_WIDTH, SCREEN_HEIGHT);

		// Draw new stuff
		starfield.drawBackground(g);
		if(player != null){
			player.drawPlayerShip(g);
		}

		Boolean temp = true;

		for (int i = 0; i < enemySize ; i++ ) {
			if(black[i] != null){
				black[i].drawEnemyBlack(g);
				temp = false;
			}
		}
		for (int i = 0; i < enemySize ; i++ ) {
			if(green[i] != null){
				green[i].drawEnemyGreen(g);
				temp = false;
			}

		}
		for (int i = 0; i < enemySize ; i++ ) {
			if(red[i] != null){
				red[i].drawEnemyRed(g);
				temp = false;
			}
		}

		if(temp == true && isLoading == false) isDeadEnemies = true;

		for(int i = 0; i < 3; i++){
			if(pBullet[i] != null)
				pBullet[i].drawPlayerBullets(g);
		}

		for(int i = 0; i < 2; i++){
			if(eBullet[i] != null)
				eBullet[i].drawEnemyBullets(g);
		}

		for(int i = 0; i < life; i++){
			g.drawImage(playerLife[i], 5+i*50, 600, null);
		}

		// Text stuff

		Font text = new Font("KenVector Future Thin", Font.PLAIN, 14);
        g.setColor(Color.BLUE);
        g.setFont(text);

		if(isDelayRunning && !dead){
			g.drawString("You got hit. Be careful!", d.width / 4 + 75, d.height / 2);
		}

		g.setColor(Color.WHITE);
		if(score > 1) {
			g.drawString("Time Elapsed:  " + score + " seconds", 20, 20);
		}
		else {
			g.drawString("Time Elapsed:  " + score + " second", 20, 20);
		}
		
		if(isGameStart == false && isWon == false && isLose == false){
			Font start = new Font("KenVector Future Thin", Font.PLAIN, 24);
			g.setFont(start);
			g.setColor(Color.RED);
			g.drawString("Press Enter to Start!", d.width / 4 + 25, d.height / 2);
		}

		if(isWon == true){
			Font win = new Font("KenVector Future Thin", Font.PLAIN, 24);
			g.setFont(win);
			g.setColor(Color.WHITE);
			g.drawString("Congratulations, you won!", d.width / 4 , d.height / 2);
			g.drawString("Press C to continue to level " + (1+level), d.width / 4 - 25, d.height / 2 + 30);
		}

		if(isLose == true){
			Font lose = new Font("KenVector Future Thin", Font.PLAIN, 24);
			g.setFont(lose);
			g.setColor(Color.RED);
			if(score > 1) {
				g.drawString("Time Elapsed: " + score + " seconds", d.width / 2 - 150, d.height / 2);
			}
			else {
				g.drawString("Time Elapsed: " + score + " second", d.width / 2 - 150, d.height / 2);
			}
			g.drawString("You just lost. Weakling.", d.width / 4 + 40, d.height / 2 + 30);
		}

		Font controls = new Font("Calibri", Font.PLAIN, 12);
		g.setFont(controls);
		g.setColor(Color.WHITE);
		g.drawString("R - restart", d.width - 100, d.height - 100);

	}


	public void update(Graphics g){
		paint(g);
	}

	// Exclusive Listener for Menu
	private class MenuListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Object comp = e.getSource();

			if(comp == menuItem[0]){
				System.exit(0);
			}
		}
	}

	// Exclusive keyboard listener for playership
	private class KeyListen implements KeyListener{

		public void keyPressed(KeyEvent e){
			int keyCode = e.getKeyCode();

			if(player != null){
				if(keyCode == KeyEvent.VK_LEFT){
					left = true;
				}

				if(keyCode == KeyEvent.VK_RIGHT){
					right = true;
				}

				if(keyCode == KeyEvent.VK_SPACE){
					for(int i = 0; i < 3; i++){
						if(pBullet[i] == null){
							pBullet[i] = new PlayerBullet(player.getXCenter()-5, player.getYCoordinate(), resize(getImage("/sprites/playerbullet.png"), 9, 24));
							break;
						}
					}
				}
			}

			if(isWon == true){
				if(keyCode == KeyEvent.VK_C){
					isNewGame = false;
					// Save level, because it is cleared in clear
					int temp = level + 1;
					long temp2 = score;
					//int temp3 = attackers;
					isLoading = true;

					//clear
					clear();

					// load saved temps
					level = temp;
					score = temp2;
					//attackers = temp3;
					loadComponents();
					isLoading = false;
				}
			}

			if(keyCode == KeyEvent.VK_ENTER){
				if(!dead){
					isGameStart = true;
					if(isNewGame) {
						strt = System.currentTimeMillis();
					}
				}
			}

			if(keyCode == KeyEvent.VK_R){
				isNewGame = true;
				isGameStart = false;
				isLoading = true;
				clear();
				loadComponents();
				isLoading = false;
			}
		}
		public void keyReleased(KeyEvent e){
			int keycode = e.getKeyCode();
//			if(keycode == KeyEvent.VK_DOWN)
//				down = false;
//			if(keycode == KeyEvent.VK_UP)
//				up = false;
			if(keycode == KeyEvent.VK_RIGHT)
				right = false;
			if(keycode == KeyEvent.VK_LEFT)
				left = false;

		}

		public void keyTyped(KeyEvent e){
//			int keyCode = e.getKeyCode();

		}
	}

	public void updatePlayerPos(){
		if(player != null){
			if(player.getXCenter()<670)
				if(right) player.translate(5,0);
			if(player.getXCenter()>30)
				if(left) player.translate(-5,0);
		}
	}

	public void updatePlayerBullets(){
		for(int i = 0; i < 3; i++){
			if((pBullet[i] != null) && (pBullet[i].getYCenter() < 10)){
				pBullet[i] = null;
			}
			if(pBullet[i] != null){
				pBullet[i].translate(0,-12);
			}
		}
	}
	public void updateEnemyBullets(){
		for(int i = 0; i < 2; i++){
			if((eBullet[i] != null) && (eBullet[i].getYCenter() > 650)){
				eBullet[i] = null;
			}
			if(eBullet[i] != null){
				eBullet[i].translate(0,5);
			}
		}
	}
	public void collisioncheck(){
		playerbulletEnemy_collision(black, enemySize);
		playershipEnemy_collision(black, enemySize);

		playerbulletEnemy_collision(green, enemySize);
		playershipEnemy_collision(green, enemySize);

		playerbulletEnemy_collision(red, enemySize);
		playershipEnemy_collision(red, enemySize);

		enemyBulletPlayer_collision();

	}
	public void playerbulletEnemy_collision(Sprites[] enemyShip, int size){
		for(int i = 0; i < 3; i++){
			if(pBullet[i] != null){
				for(int j = 0; j < size; j++){
					if(enemyShip[j] != null){
						if(intersects(enemyShip[j], pBullet[i]) == true){
							enemyShip[j] = null;
							pBullet[i] = null;
							break;
						}
					}
				}
			}
		}
	}
	public void enemyBulletPlayer_collision(){
		for(int i = 0; i < 2; i++){
			if(eBullet[i] != null && player != null){
					if(intersects(player, eBullet[i]) == true){
							// pag may lives na, live-- gawin,di yung null agad.
							minus_life();
							eBullet[i] = null;
							break;
					}
			}
		}
	}


	public void playershipEnemy_collision(Sprites[] enemyShip, int size){
		for(int i = 0; i < size; i++){
			if(enemyShip[i] != null && player != null){
				if(intersects(enemyShip[i], player) == true){
					// pag may lives na, live-- gawin,di yung null agad.
					minus_life();
					enemyShip[i] = null;
					break;
				}
			}
		}
	}
	public boolean intersects(JLabel a, JLabel b){
			Area areaA = new Area(a.getBounds());
			Area areaB = new Area(b.getBounds());

			return areaA.intersects(areaB.getBounds2D());
	}

	public void minus_life(){
		if(life == 0){
			player = null;
			isGameStart = false;
			isLose = true;
			dead = true;
			// do stuff here

		}
		else{
			life--;
		}
	}

	protected void attackShip(Sprites tempShip){
		if(randomFireTime < currentFireTime){
			currentFireTime = 0;
			//  Fire a bullet
			for(int i = 0; i < 2; i++){
				if(eBullet[i] == null){
					eBullet[i] = new EnemyBullet(tempShip.getXCenter()-5, tempShip.getYCoordinate(), resize(getImage("/sprites/enemyBullet.png"), 9, 24));
				break;
				//  Reset the random time
				}
				randomFireTime = (int) (((Math.random() * RANDOM_FIRE_SEED) + 1) + 60);
			}
		}
		currentFireTime++;
	}


	protected boolean moveEnemyShips(Sprites[] enemyShip, int size)
	{
			//  Number of ships of a given class that are moving
		//	if(player == null) return false;
			int counter = 0;

			//  Check how many ships are moving at a given time
			//    i = each ship in the Vector
			for(int i = 0; i < size; i++)
			{
					//  Temp variable for accessing the ship from the Vector
					if(enemyShip[i] != null){
	        	Sprites tempShip = enemyShip[i];
	            //  If the ship is moving or attacking
	            if(tempShip.isAttacking() || tempShip.isRetreating())
	                //  Update the counter
	                counter++;
	        }
        }


//			int index = (int)(Math.random() * size); // pick one random ship from the array

			//  For each ship in the vector
			for(int i = 0; i < size; i++)
			{
				if(enemyShip[i] != null){
					//  Temp variable for accessing the ship from the Vector
						Sprites tempShip =  enemyShip[i];

						//  Calls the CheckTime method of each ship.  This goes through and
						//    checks if enough time has passed for a ship to start moving.
						//    If it has, the ship will be set to attack, and it will
						//    return true.
						if(tempShip.checkTime()){
								//  If the ship is attacking and a null array
								if(tempShip.isAttacking() && tempShip.attackPoints == null)
								{
									if(counter < attackers)
										assignCurve(tempShip);     //  Assign a curve

										//  Move the ship along its curve
										// edited stuff here, null from enemybullet

										tempShip.attackShip(null, player);
								}
								//  If the ship is attacking and has no points assigned
								if(tempShip.isAttacking() && tempShip
													 .attackPoints[tempShip.attackPoints.length - 1] == null)
								{
										//  If there are fewer ships on screen than the level
										if(counter < attackers)
										assignCurve(tempShip);     //  Assign a curve

										//  Move the ship along its curve

										tempShip.attackShip(null, player);
								}
								//  If the ship is attacking
								if(tempShip.isAttacking())
								{
										//  Move the ship along its curve
																				attackShip(tempShip);
										tempShip.attackShip(null, player);

								}

								//  If the ship is retreating
								if(tempShip.isRetreating())
								{
										//  Move the ship along its curve
										tempShip.retreatShip();
								}
						}
				}
			}
			if(counter == 0)   //  If no ships are moving, return true
					return true;
			else       		   //  Otherwise, return false
					return false;
	}

	//******************************************************************************
	//  Method that assigns the curve to the enemy ship
	//******************************************************************************
	protected void assignCurve(Sprites tempShip)
	{
		ArrayList<Point> tempPoints = new ArrayList<Point>(); //  Creates a temporary vector of points
		Point points2[];                  //  Creates a temporary array of points

			//  Sets the enemy ship moving
			tempShip.setMoving(true);

			//  Creates the Bezier Curve into a Shape Object
			Shape curve = generateCurve((PlayerShip) player, tempShip);

			//  Creates a path iterator which will go point by point over the boundary
			PathIterator p = curve.getPathIterator(null);
			//  Creates a flattening path iterator which extracts the points
			FlatteningPathIterator f = new FlatteningPathIterator(p, 0.009);

			//  While it is not done iterating
			while(!f.isDone())
			{
					//  Create a temp object to extract the points
					double[] points = new double[6];
					switch(f.currentSegment(points))
					{
							//  When the iterator has found a point
							case PathIterator.SEG_MOVETO:
							case PathIterator.SEG_LINETO:
									// Add the point to the vector
									tempPoints.add(new Point((int) points[0], (int) points[1]));
					}
					//  Go to the next point
					f.next();
			}
			//  Creates an array based on the size of the Vector
			points2 = new Point[tempPoints.size()];

			//  Goes through the Vector and assigns the points to the array
			for(int i = 0; i < tempPoints.size(); i++)
			{
					points2[i] = (Point) tempPoints.get(i);
					//System.out.println(points2[i]);
			}

			//  Passes the array to the ship, which will set the attack and
			//    retreat points
			tempShip.setCurve(points2);
			//  Sets ship to attack
			tempShip.setAttack(true);
	}

	//******************************************************************************
	//  Generates the Curve when passed the player and enemy ships
	//******************************************************************************
	protected Shape generateCurve(Sprites player, Sprites enemy)
	{
			//  Sets a null curve:  Temporary
			Shape curve = null;

			//  If the Enemy is on the left side of the screen,
			//    use one set of control points
			if(enemy.getXCenter() < d.width / 2)
					curve =  new CubicCurve2D.Double(enemy.getXCenter(), enemy.getYCenter(),
													d.width, enemy.getYCenter(),
													enemy.getXCenter(), d.height,
													player.getXCenter(), player.getYCenter());

			//  If the enemy is on the right side of the screen,
			//    use an alternate set of points
			if(enemy.getXCenter() >= d.width / 2)
					curve =  new CubicCurve2D.Double(enemy.getXCenter(), enemy.getYCenter(),
													0, enemy.getYCenter(),
													enemy.getXCenter(), d.height,
													player.getXCenter(), player.getYCenter());

			return curve;       //  Return the generate curve
	}


	public static void main(String[] args){
		new Main();
	}

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

	public BufferedImage getImage(String filename) {
		try {
			//File fp = new File(filename);
			URL url = this.getClass().getResource(filename);
			BufferedImage img = ImageIO.read(url);
			img = resize(img, 120, 160);
			return img;
		} catch (Exception e) {
			System.out.println("Unable to read file!");
			return null;
		}
	}
}
