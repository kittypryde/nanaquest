package net.obviam.nanaquest.model;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class WalkEnemy {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}

	public static String enemyType; 
	
	public static float gravity = (float) 0.1;
	
	static Vector2 		position = new Vector2();
	static boolean		facingLeft = true;
	float				stateTime = 0;
	boolean				longJump = false;
	Vector2 			acceleration = new Vector2();
	Vector2 			velocity = new Vector2();
	TextureRegion 		frame = new TextureRegion();
	Rectangle 			bounds = new Rectangle();
	State				state = State.IDLE;
	
	/** Lists **/
	public static List <Vector2> 		enemyPositions;
	public static List <State> 			enemyStates;
	public static List <Float>			enemyStateTimes;
	public static List <TextureRegion> 	enemyFrames;
	public static List <String>			enemyDirections;
	public static List <Vector2> 		enemyAccelerations;
	public static List <Vector2> 		enemyVelocities;
	public static List <Rectangle> 		enemyBounds;
	public static List <Float> 			enemyWidths;
	public static List <Float> 			enemyHeights;
	public static List <Integer>		enemyHits;

	public WalkEnemy(Vector2 position) {
		WalkEnemy.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.bounds.height = (float) 1;
		this.bounds.width = (float) 1.4;
		
		if (enemyPositions == null) {
			enemyPositions = new LinkedList<Vector2>();
		}
		if (enemyStates == null) {
			enemyStates = new LinkedList<State>();
		}
		if (enemyStateTimes == null) {
			enemyStateTimes = new LinkedList<Float>();
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
		if (enemyWidths == null) {
			enemyWidths = new LinkedList<Float>();
		}
		if (enemyHeights == null) {
			enemyHeights = new LinkedList<Float>();
		}
		if (enemyHits == null) {
			enemyHits = new LinkedList<Integer>();
		}
		
		// Add characteristics to each enemy
		addEnemy(position);
	}
	
	public void addEnemy(Vector2 position) {
		enemyPositions.add(position);
		enemyStates.add(State.IDLE);
		enemyFrames.add(frame);
		enemyStateTimes.add(stateTime);
		enemyDirections.add("");
		enemyAccelerations.add(acceleration);
		enemyVelocities.add(velocity);
		enemyBounds.add(bounds);
		enemyHits.add(0);
		enemyWidths.add((float) 1.4);
		enemyHeights.add((float) 1);
	}
	
	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}
	
	// Removes parameter index integer in all lists
	public static void removeEnemy(int enemy) {
		enemyPositions.remove(enemy);
		enemyStates.remove(enemy);
		enemyStateTimes.remove(enemy);
		enemyFrames.remove(enemy);
		enemyDirections.remove(enemy);
		enemyAccelerations.remove(enemy);
		enemyVelocities.remove(enemy);
		enemyBounds.remove(enemy);
		enemyWidths.remove(enemy);
		enemyHeights.remove(enemy);
		enemyHits.remove(enemy);
	}
	
	public static void removeAll(){
		enemyPositions.clear();
		enemyStates.clear();
		enemyStateTimes.clear();
		enemyFrames.clear();
		enemyDirections.clear();
		enemyAccelerations.clear();
		enemyVelocities.clear();
		enemyBounds.clear();
		enemyWidths.clear();
		enemyHeights.clear();
		enemyHits.clear();
	}
	
	
}
