package net.obviam.nanaquest.model;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Boss {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}
	
	public static final float WIDTH = 6f;
	public static final float HEIGHT = 4f; 
	
	static float gravity = (float) 0.1;


	public static Vector2 	position = new Vector2();
	static Vector2 			velocity = new Vector2();
	static Vector2 			acceleration = new Vector2();
	static boolean			facingLeft = true;
	boolean					longJump = false;
	float					stateTime = 0;
	public static boolean 	bossBattle = false;
	public static boolean 	bossDied = false;
	
	// Bullet lists
	public static List<Vector2> bulletPositions;
	public static List<Texture> bulletTextures;
	public static List<String> 	bulletDirections;
	public static List<Float>	bulletAdds;
	public static List<Float> 	bulletGravity;
	
	static Rectangle 		bounds = new Rectangle();
	static State			state = State.IDLE;

	public Boss(Vector2 position) {
		Boss.position = position;
		Boss.bounds.x = position.x;
		Boss.bounds.y = position.y;
		Boss.bounds.height = HEIGHT;
		Boss.bounds.width = WIDTH;
		
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
			bulletAdds = new LinkedList<Float>();
		}
		if (bulletGravity == null) {
			bulletGravity = new LinkedList<Float>();
		}
		
	}
	
	public static boolean isFacingLeft() {
		return facingLeft;
	}

	public static void setFacingLeft(boolean facingLeft) {
		Boss.facingLeft = facingLeft;
	}

	public static Vector2 getPosition() {
		return position;
	}

	public static Vector2 getAcceleration() {
		return acceleration;
	}

	public static Vector2 getVelocity() {
		return velocity;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public static State getState() {
		return state;
	}
	
	public void setState(State newState) {
		state = newState;
	}

	public float getStateTime() {
		return stateTime;
	}

	public boolean isLongJump() {
		return longJump;
	}


	public void setLongJump(boolean longJump) {
		this.longJump = longJump;
	}
	
	public static void newBullet() {
		// Add Bullet Characteristics
		addBullet(new Vector2(0, 0));
//		addDirection(direction);
//		addTexture(texture);
	
	}
	
	
	public static void addBullet(Vector2 position) {
		float bulletAdd = (float) (isFacingLeft() ? -0.05 : 0.05);

		position = mapToBoss(position);
		bulletPositions.add(position);
		bulletGravity.add(gravity);
		bulletAdds.add(bulletAdd);
	}


	public static void setPosition(Vector2 position) {
		Boss.position = position;
		bounds.setX(position.x);
		bounds.setY(position.y);
	}


	public static void setAcceleration(Vector2 acceleration) {
		Boss.acceleration = acceleration;
	}


	public static void setVelocity(Vector2 velocity) {
		Boss.velocity = velocity;
	}


	public void setBounds(Rectangle bounds) {
		Boss.bounds = bounds;
	}


	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}
	
	
	public void bossAttack(Vector2 position) {
		position = mapToBoss(position);
		bulletPositions.add(position);
	}


	public static Vector2 mapToBoss(Vector2 position) {
		
		// Set position based on Bob's direction 
		position.x = (float) (Boss.position.x);
		position.y = (float) (Boss.position.y + 1);
		
		return position;
	}
	
	public static void removeBullet(int bullet) {
		Boss.bulletPositions.remove(bullet);
		Boss.bulletGravity.remove(bullet);
		Boss.bulletAdds.remove(bullet);
//		Boss.bulletTextures.remove(bullet);
//		Boss.bulletDirections.remove(bullet);
	}

	public void update(float delta) {
		stateTime += delta;
	}
	
}
