package net.obviam.nanaquest.controller;

import java.util.LinkedList;
import java.util.List;

import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.WalkEnemy;
import net.obviam.nanaquest.model.WalkEnemy.State;
import net.obviam.nanaquest.model.HealthBar;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.screens.GameScreen;
import net.obviam.nanaquest.screens.PauseScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class WalkEnemyController {
	private static final float ACCELERATION 	= 3f;
	private static final float GRAVITY 			= -20f;
	private static final float DAMP 			= 0.90f;
	private static final float MAX_VEL 			= 3f;
	private World 		world;
	private WalkEnemy 	walkEnemy;
	private boolean 	grounded = false;

	public Array<Block> collidable = new Array<Block>();
	public List<Rectangle> 	rectList;

	
	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	public WalkEnemyController(World world) {
		this.world = world;
		
		if (rectList == null) {
			rectList = new LinkedList<Rectangle>();
		}
	}
	
	/** The main update method **/
	@SuppressWarnings("deprecation")
	public void update(float delta) {
				
		rectList.clear();
		for (int i = 0; i < WalkEnemy.enemyPositions.size(); i++) {
						
			// Set behavior of enemy
			setBehavior();
			
			// If enemy is grounded then reset the state to IDLE 
			if (grounded && WalkEnemy.enemyStates.get(i).equals(State.JUMPING)) {
				WalkEnemy.enemyStates.set(i, State.IDLE);
			}
			
			// Setting initial vertical acceleration 
			WalkEnemy.enemyAccelerations.get(i).y = GRAVITY;
			
			// Convert acceleration to frame time
			WalkEnemy.enemyAccelerations.get(i).mul(delta);
						
			// apply acceleration to change velocity
			WalkEnemy.enemyVelocities.get(i).add(WalkEnemy.enemyAccelerations.get(i));
			
			// checking collisions with the surrounding blocks depending on enemy's velocity
			checkCollisionWithBlocks(delta, i);
	
			// apply damping to halt enemy nicely 
			WalkEnemy.enemyVelocities.get(i).x *= DAMP;
			
			// ensure terminal velocity is not exceeded
			if (WalkEnemy.enemyVelocities.get(i).x > MAX_VEL) {
				WalkEnemy.enemyVelocities.get(i).x = MAX_VEL;
			}
			if (WalkEnemy.enemyVelocities.get(i).x < -MAX_VEL) {
				WalkEnemy.enemyVelocities.get(i).x = -MAX_VEL;
			}

			// Update State Time
			WalkEnemy.enemyStateTimes.set(i, walkEnemy.enemyStateTimes.get(i) + delta);		
	
			/** Prevents Actor from Leaving Game Window **/
			if (WalkEnemy.enemyPositions.get(i).y < 1) {
				WalkEnemy.enemyPositions.get(i).y = 1f;
				
				if (WalkEnemy.enemyStates.get(i).equals(State.JUMPING)) {
						WalkEnemy.enemyStates.set(i, State.IDLE);
				}
			}
			if (WalkEnemy.enemyPositions.get(i).x < 0) {
				WalkEnemy.enemyPositions.get(i).x = 0;

				if (!WalkEnemy.enemyStates.get(i).equals(State.JUMPING)) {
					WalkEnemy.enemyStates.set(i, State.IDLE);
				}
			}	
			if (WalkEnemy.enemyPositions.get(i).x > (float) Level.getWidth() - 0.5) {
				WalkEnemy.enemyPositions.get(i).x = (float) (Level.getWidth() - 0.5);

				if (!WalkEnemy.enemyStates.get(i).equals(State.JUMPING)) {
					WalkEnemy.enemyStates.set(i, State.IDLE);
				}
			}
		}
	}

	/** Collision checking **/
	@SuppressWarnings("deprecation")
	private void checkCollisionWithBlocks(float delta, int j) {
		// scale velocity to frame units 
		WalkEnemy.enemyVelocities.get(j).mul(delta);
		
		rectList.add(null);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle enemyRect = rectPool.obtain();
		// set the rectangle to enemy's bounding box
		enemyRect.set(WalkEnemy.enemyBounds.get(j).x, WalkEnemy.enemyBounds.get(j).y, WalkEnemy.enemyBounds.get(j).width, WalkEnemy.enemyBounds.get(j).height);
		rectList.set(j, enemyRect);
		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) WalkEnemy.enemyBounds.get(j).y;
		int endY = (int) (WalkEnemy.enemyBounds.get(j).y + WalkEnemy.enemyBounds.get(j).height);
		// if enemy is heading left then we check if he collides with the block on his left
		// we check the block on his right otherwise
		if (WalkEnemy.enemyVelocities.get(j).x < 0) {
			startX = endX = (int) Math.floor(WalkEnemy.enemyBounds.get(j).x + WalkEnemy.enemyVelocities.get(j).x);
		} else {
			startX = endX = (int) Math.floor(WalkEnemy.enemyBounds.get(j).x + WalkEnemy.enemyBounds.get(j).width + WalkEnemy.enemyVelocities.get(j).x);
		}

		// get the block(s) enemy can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate enemy's movement on the X
		rectList.get(j).x += WalkEnemy.enemyVelocities.get(j).x;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if enemy collides, make his horizontal velocity 0
		for (Block block : collidable) {
			if (block == null) continue;
			if (rectList.get(j).overlaps(block.getBounds())) {
				WalkEnemy.enemyVelocities.get(j).x = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		rectList.get(j).x = WalkEnemy.enemyPositions.get(j).x;
		
		// the same thing but on the vertical Y axis
		startX = (int) WalkEnemy.enemyBounds.get(j).x;
		endX = (int) (WalkEnemy.enemyBounds.get(j).x + WalkEnemy.enemyBounds.get(j).width);
		if (WalkEnemy.enemyVelocities.get(j).y < 0) {
			startY = endY = (int) Math.floor(WalkEnemy.enemyBounds.get(j).y + WalkEnemy.enemyVelocities.get(j).y);
		} else {
			startY = endY = (int) Math.floor(WalkEnemy.enemyBounds.get(j).y + WalkEnemy.enemyBounds.get(j).height + WalkEnemy.enemyVelocities.get(j).y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		rectList.get(j).y += WalkEnemy.enemyVelocities.get(j).y;

		for (Block block : collidable) {
			if (block == null) 
				continue;
			if (rectList.get(j).overlaps(block.getBounds())) {
				if (WalkEnemy.enemyVelocities.get(j).y < 0) {
					grounded = true;
				}
				WalkEnemy.enemyVelocities.get(j).y = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		rectList.get(j).y = WalkEnemy.enemyPositions.get(j).y;
		
		// update enemy's position
		WalkEnemy.enemyStateTimes.set(j, walkEnemy.enemyStateTimes.get(j) + delta);		
		
		WalkEnemy.enemyPositions.get(j).add(WalkEnemy.enemyVelocities.get(j));
		WalkEnemy.enemyBounds.get(j).x = WalkEnemy.enemyPositions.get(j).x;
		WalkEnemy.enemyBounds.get(j).y = WalkEnemy.enemyPositions.get(j).y;
		
		// un-scale velocity (not in frame time)
		WalkEnemy.enemyVelocities.get(j).mul(1 / delta);
		
	}
	

	/** populate the collidable array with the blocks found in the enclosing coordinates **/
	private void populateCollidableBlocks(int startX, int startY, int endX, int endY) {
		collidable.clear();
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				world.getLevel();
				world.getLevel();
				if (x >= 0 && x < Level.getWidth() && y >=0 && y < Level.getHeight()) {
					collidable.add(world.getLevel().get(x, y));
				}
			}
		}
	}
	
	
	private void setBehavior() {
		
		// Iterates through each enemy
		for (int i = 0; i < WalkEnemy.enemyPositions.size(); i++) {
			
			// Walk to Bob
			if (WalkEnemy.enemyPositions.get(i).x > Bob.getPosition().x + 0.5) {
				WalkEnemy.enemyDirections.set(i, "left");
				WalkEnemy.enemyStates.set(i, State.WALKING);
				WalkEnemy.enemyAccelerations.get(i).set(-ACCELERATION, GRAVITY);
			} else if (WalkEnemy.enemyPositions.get(i).x < Bob.getPosition().x - 0.5) {
				WalkEnemy.enemyDirections.set(i, "right");
				WalkEnemy.enemyStates.set(i, State.WALKING);
				WalkEnemy.enemyAccelerations.get(i).set(ACCELERATION, GRAVITY);
			}
						
			// If enemy touches bob, bob loses health
			if (WalkEnemy.enemyBounds.get(i).overlaps(Bob.getBounds())) {
				
				// Bob Lose Health
				if (HealthBar.bobHealth > 0) {
					HealthBar.bobHealth -= 0.001;
				}
				
				// Bob Dies
				if (HealthBar.bobHealth <= 0.1){
					Bob.bobDied = true;
				}
			}
		}
	}	
}
