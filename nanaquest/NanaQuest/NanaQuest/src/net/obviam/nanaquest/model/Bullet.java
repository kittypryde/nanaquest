package net.obviam.nanaquest.model;

import java.util.LinkedList;
import java.util.List;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
	
	/** Bullet Characteristics in Lists **/
	public static List<Vector2> bulletPositions;
	public static List<Texture> bulletTextures;
	public static List<String> bulletDirections;
	public static List<Vector2> bulletAdds;
	
	public static Rectangle bounds = new Rectangle();
	
	public static boolean firePressed;
	
	public static Vector2 bullet_position;
	
	private static Texture rightBulletTexture;
	private static Texture leftBulletTexture;
	
	public Bullet() {
		
		// Initialize Lists
		if (bulletPositions == null) {
			bulletPositions = new LinkedList<Vector2>();
		}
		if (bulletTextures == null) {
			bulletTextures = new LinkedList<Texture>();
		}
		if (bulletDirections == null) {
			bulletDirections = new LinkedList<String>();
		}
		if (bulletAdds == null) {
			bulletAdds = new LinkedList<Vector2>();
		}
	}
	
	
	public static void loadTextures() {
		leftBulletTexture = new Texture (Gdx.files.internal("images/bullet_goldball_nq.fw.png"));
		rightBulletTexture = new Texture (Gdx.files.internal("images/bullet_goldball_nq.fw.png"));
	}
	
	
	public static void newBullet() {
		loadTextures();
		Texture texture = Bob.isFacingLeft() ? leftBulletTexture : rightBulletTexture;
		Vector2 bulletAdd = Bob.isFacingLeft() ? (new Vector2((float)-0.3, 0)) : (new Vector2((float)0.3, 0));
		String direction = Bob.isFacingLeft() ? "Left" : "Right";
	
		// Add Bullet Characteristics
		addBullet(new Vector2(0, 0));
		addDirection(direction);
		addTexture(texture);
		bulletAdds(bulletAdd);
	
	}
	
	
	public static void addBullet(Vector2 position) {
		position = mapToBob(position);
		bulletPositions.add(position);
	}
	
	
	public static void addTexture(Texture texture) {
		bulletTextures.add(texture);
	}
	
	
	public static void addDirection(String direction) {
		bulletDirections.add(direction);
	}
	
	public static void bulletAdds(Vector2 bulletAdd) {
		bulletAdds.add(bulletAdd);
	}
	
	
	public static Vector2 mapToBob(Vector2 position) {
		
		// Set position based on Bob's direction 
		position.x = (float) (Bob.isFacingLeft() ? Bob.position.x : Bob.position.x + 0.2);
		position.y = (float) (Bob.position.y + 0.2);
		
		return position;
	}
	
	
	public static void removeBullet(int bullet) {
		Bullet.bulletPositions.remove(bullet);
		Bullet.bulletTextures.remove(bullet);
		Bullet.bulletAdds.remove(bullet);
		Bullet.bulletDirections.remove(bullet);
	}

}
	



