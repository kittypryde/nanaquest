package net.obviam.nanaquest.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bob {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}
	
	public static final float WIDTH = 0.8f; // half a unit
	public static final float HEIGHT = 0.9f;

	public static Vector2 	position = new Vector2();
	static Vector2 			velocity = new Vector2();
	static Vector2 			acceleration = new Vector2();
	static boolean			facingLeft = true;
	boolean					longJump = false;
	float					stateTime = 0;
	
	public static boolean 	bobDied = false;
	
	static Rectangle 		bounds = new Rectangle();
	static State			state = State.IDLE;

	public Bob(Vector2 position) {
		Bob.position = position;
		Bob.bounds.x = position.x;
		Bob.bounds.y = position.y;
		Bob.bounds.height = HEIGHT;
		Bob.bounds.width = WIDTH;
	}

	
	public static boolean isFacingLeft() {
		return facingLeft;
	}

	public static void setFacingLeft(boolean facingLeft) {
		Bob.facingLeft = facingLeft;
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

	public static Rectangle getBounds() {
		return bounds;
	}

	public static State getState() {
		return state;
	}
	
	public static void setState(State newState) {
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


	public static void setPosition(Vector2 position) {
		Bob.position = position;
		bounds.setX(position.x);
		bounds.setY(position.y);
	}


	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}


	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}


	public void setBounds(Rectangle bounds) {
		Bob.bounds = bounds;
	}


	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}


	public void update(float delta) {
		stateTime += delta;
	}
	
}
