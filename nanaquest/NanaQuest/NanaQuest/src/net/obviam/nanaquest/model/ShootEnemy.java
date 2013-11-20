package net.obviam.nanaquest.model;

import java.util.LinkedList;
import java.util.List;

import net.obviam.nanaquest.model.WalkEnemy.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ShootEnemy {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}
	
	public static final float WIDTH = 3f;
	public static final float HEIGHT = 2f; 
	
	public static float gravity = (float) 0.1;

	public static Vector2 	position = new Vector2();
	public static Vector2 	bulletPosition = new Vector2();
	static Vector2 			velocity = new Vector2();
	static Vector2 			acceleration = new Vector2();
	static boolean			facingLeft = true;
	boolean					longJump = false;
	TextureRegion 			frame = new TextureRegion();
	static float			stateTime = 0;
	public static boolean 	shootEnemyDied = false;
	
	public static Texture spiderAttackTexture;
	
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
	public static List<Vector2> 		bulletPositions;
	public static List<Texture> 		bulletTextures;
	public static List<String> 			bulletDirections;
	public static List<Float>			bulletAdds;
	public static List<Float>			bulletGravity;
	
	static Rectangle 		bounds = new Rectangle();
	static State			state = State.IDLE;
	public static boolean showBullet;

	public ShootEnemy(Vector2 position) {
		ShootEnemy.position = position;
		ShootEnemy.bounds.x = position.x;
		ShootEnemy.bounds.y = position.y;
		ShootEnemy.bounds.height = HEIGHT;
		ShootEnemy.bounds.width = WIDTH;
		
		// Initialize Lists
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
		
		// Add characteristics to each enemy
		loadTextures();
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
		bulletPositions.add(bulletPosition);
		bulletTextures.add(spiderAttackTexture);
		bulletAdds.add((float) 0);
		bulletGravity.add(gravity);
	}
	
	public void loadTextures() {
		spiderAttackTexture = new Texture (Gdx.files.internal("images/enemy_attack.fw.png"));
	}

	public void update(float delta) {
		stateTime += delta;
	}

	public static void newBullets() {
		for (int i = 0; i < enemyPositions.size(); i++) {
			float bulletAdd = (float) (enemyDirections.get(i) == "left" ? -0.03 : 0.03);
			bulletPosition = mapToShootEnemy(i);
			bulletPositions.add(new Vector2(0, 0));
			bulletPositions.set(i, bulletPosition);
			bulletTextures.add(spiderAttackTexture);
			bulletAdds.add(bulletAdd);
			bulletGravity.add(gravity);
		}
	}
	
	public static Vector2 mapToShootEnemy(int enemy) {
			
		// Set position based on Bob's direction 
		bulletPosition.x = (float) (ShootEnemy.enemyPositions.get(enemy).x);
		bulletPosition.y = (float) (ShootEnemy.enemyPositions.get(enemy).y + 1);
		
		return bulletPosition;
	}
	
	public static void removeBullets() {
		bulletPositions.clear();
		bulletTextures.clear();
		bulletAdds.clear();
		bulletGravity.clear();
	}

	public static void removeBullet(int enemy) {
		bulletPositions.remove(enemy);
		bulletTextures.remove(enemy);
		bulletAdds.remove(enemy);
		bulletGravity.remove(enemy);
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
		bulletPositions.remove(enemy);
		bulletAdds.remove(enemy);
		bulletGravity.remove(enemy);
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
		bulletPositions.clear();
		bulletTextures.clear();
		bulletAdds.clear();
		bulletGravity.clear();
	}
	
}
