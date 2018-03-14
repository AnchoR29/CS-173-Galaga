package Galaga;

import static org.junit.Assert.*;

import java.awt.Graphics;

import org.junit.Test;

public class MainTest {

	@Test
	public void test() {
		FreeMode main = new FreeMode();
		Graphics g = main.imageBuffer.getGraphics();
		
		//Player Bullet to Enemy
		
		PlayerBullet[] pbtest = new PlayerBullet[2];
		pbtest[0] = new PlayerBullet(0, 0, main.resize(main.getImage("sprites/playerbullet.png"), 9, 24));
		pbtest[1] = new PlayerBullet(30, 30, main.resize(main.getImage("sprites/playerbullet.png"), 9, 24));
		EnemyBlack enemy = new EnemyBlack(30, 30, main.resize(main.getImage("sprites/black.png"), 47, 42));
		pbtest[0].drawPlayerBullets(g);
		pbtest[1].drawPlayerBullets(g);
		enemy.drawEnemyBlack(g);
		assertEquals(main.intersects(enemy, pbtest[0]), false);
		assertEquals(main.intersects(enemy, pbtest[1]), true);
		
		//Enemy Bullet to Player
		EnemyBullet[] ebtest = new EnemyBullet[2];
		ebtest[0] = new EnemyBullet(0, 0, main.resize(main.getImage("sprites/enemybullet.png"), 9, 24));
		ebtest[1] = new EnemyBullet(30, 30, main.resize(main.getImage("sprites/enemybullet.png"), 9, 24));
		PlayerShip player = new PlayerShip(30, 30, main.resize(main.getImage("sprites/player.png"), 47, 42));
		ebtest[0].drawEnemyBullets(g);
		ebtest[1].drawEnemyBullets(g);
		player.drawPlayerShip(g);
		assertEquals(main.intersects(player, ebtest[0]), false);
		assertEquals(main.intersects(player, ebtest[1]), true);
		
		//Enemy to Player
		EnemyBlack enemy2 = new EnemyBlack(0, 0, main.resize(main.getImage("sprites/black.png"), 47, 42));
		assertEquals(main.intersects(player, enemy), true);
		assertEquals(main.intersects(player, enemy2), false);
	}

}
