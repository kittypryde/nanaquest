package net.obviam.nanaquest.model;

import net.obviam.nanaquest.model.Bob.State;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ship {

	public static Vector2 		position = new Vector2();
	private Vector2 			acceleration = new Vector2(0, 0);
	private Vector2 			velocity = new Vector2();
	Rectangle 					bounds = new Rectangle();
	
	// size
	public static int width = 4;
	public static int height = 2;
	
	float		stateTime = 0;
	static State		state = State.IDLE;
	
	public enum State {
		IDLE, TAKEOFF, FLYING, LANDING
	}
	
	public Ship(Vector2 position) {
		Ship.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.bounds.height = height;
		this.bounds.width = width;
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
	
	public static State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void update(float delta) {
		stateTime += delta;
	}

	public static void setPosition(Vector2 position) {
		Ship.position = position;
	}

}
