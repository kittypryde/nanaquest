package net.obviam.nanaquest.model;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}
	
	public static final float SIZE = 0.5f; // half a unit

	static Vector2 		position = new Vector2();
	static boolean		facingLeft = true;
	float				stateTime = 0;
	boolean				longJump = false;
	Vector2 			acceleration = new Vector2();
	Vector2 			velocity = new Vector2();
	TextureRegion		frame = new TextureRegion();
	Rectangle 			bounds = new Rectangle();
	State				state = State.IDLE;
	
	/** Lists **/
	public static List <Vector2> 		enemyPositions;
	public static List <State> 			enemyStates;
	public static List <TextureRegion> 	enemyFrames;
	public static List <String>			enemyDirections;
	public static List <Vector2> 		enemyAccelerations;
	public static List <Vector2> 		enemyVelocities;
	public static List <Rectangle> 		enemyBounds;
	
	public Enemy(Vector2 position) {
		Enemy.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.bounds.height = SIZE;
		this.bounds.width = SIZE;	
		
		if (enemyPositions == null) {
			enemyPositions = new LinkedList<Vector2>();
		}
		if (enemyStates == null) {
			enemyStates = new LinkedList<State>();
		}
		if (enemyFrames == null) {
			enemyFrames = new LinkedList<TextureRegion>();
		}
		if (enemyDirections == null) {
			enemyDirections = new LinkedList<String>();
		}
		if (enemyAccelerations == null) {
			enemyAccelerations = new LinkedList<Vector2>();
		}
		if (enemyVelocities == null) {
			enemyVelocities = new LinkedList<Vector2>();
		}
		if (enemyBounds == null) {
			enemyBounds = new LinkedList<Rectangle>();
		}
		
		addEnemy(position);
	}

	public void addEnemy(Vector2 position) {
		enemyPositions.add(position);
		enemyStates.add(State.IDLE);
		enemyFrames.add(frame);
		enemyDirections.add("");
		enemyAccelerations.add(acceleration);
		enemyVelocities.add(velocity);
		enemyBounds.add(bounds);
	}

	public static boolean isFacingLeft() {
		return facingLeft;
	}

	public static void setFacingLeft(boolean facingLeft) {
		Enemy.facingLeft = facingLeft;
	}

	public static Vector2 getPosition() {
		return position;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public State getState() {
		return state;
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


	public void setPosition(Vector2 position, int i) {
		Enemy.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
		Enemy.enemyPositions.set(i, position);
	}


	public static void setAcceleration(Vector2 acceleration, int i) {
		acceleration = acceleration;
		enemyAccelerations.set(i, acceleration);
	}


	public static void setVelocity(Vector2 velocity, int i) {
		enemyVelocities.set(i, velocity);
	}
	

	public void setBounds(Rectangle bounds, int i) {
		this.bounds = bounds;
		enemyBounds.set(i, bounds);
	}


	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}


	public void update(float delta) {
		stateTime += delta;
	}
	
	// Removes parameter index integer in all lists
	public static void removeEnemy(int enemy) {
		enemyPositions.remove(enemy);
		enemyStates.remove(enemy);
		enemyFrames.remove(enemy);
		enemyDirections.remove(enemy);
		enemyAccelerations.remove(enemy);
		enemyVelocities.remove(enemy);
		enemyBounds.remove(enemy);
	}
	
	
}
