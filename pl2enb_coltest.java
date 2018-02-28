import static org.junit.Assert.assertEquals;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

class pl2enb_coltest {

	@Test
	void test() {
		PlayerShip player = new PlayerShip(327, 550, resize(getImage("/sprites/player.png"), 50, 37));
		EnemyBullet eBullet = new EnemyBullet(0, 0, resize(getImage("/sprites/enemyBullet.png"), 9, 24));
		assertEquals(intersects(player, eBullet), false);
		EnemyBullet eBullet2 = new EnemyBullet(327, 550, resize(getImage("/sprites/enemyBullet.png"), 9, 24));
		assertEquals(intersects(player, eBullet2), true); //SMTH WRONG HERE
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
	
	public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
	
	public boolean intersects(JLabel a, JLabel b){
		Area areaA = new Area(a.getBounds());
		Area areaB = new Area(b.getBounds());

		return areaA.intersects(areaB.getBounds2D());
}
	
}
