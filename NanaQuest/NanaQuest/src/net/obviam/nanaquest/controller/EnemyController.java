package net.obviam.nanaquest.controller;

import java.util.LinkedList;
import java.util.List;

import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Enemy;
import net.obviam.nanaquest.model.Enemy.State;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class EnemyController {
	private static final float ACCELERATION 	= 5f;
	private static final float GRAVITY 			= -20f;
	private static final float DAMP 			= 0.90f;
	private static final float MAX_VEL 			= 3f;
	private World 	world;
	private Enemy 	enemy;
	private boolean grounded = false;

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
	
	public EnemyController(World world) {
		this.world = world;
		this.enemy = world.getEnemy();
		
		if (rectList == null) {
			rectList = new LinkedList<Rectangle>();
		}
	}
	
	/** The main update method **/
	@SuppressWarnings("deprecation")
	public void update(float delta) {
				
		for (int i = 0; i < Enemy.enemyPositions.size(); i++) {
						
			// Set behavior of enemy
			setBehavior();
			
			// If enemy is grounded then reset the state to IDLE 
			if (grounded && Enemy.enemyStates.get(i).equals(State.JUMPING)) {
				Enemy.enemyStates.set(i, State.IDLE);
			}
			
			// Setting initial vertical acceleration 
			Enemy.enemyAccelerations.get(i).y = GRAVITY;
			
			// Convert acceleration to frame time
			Enemy.enemyAccelerations.get(i).mul(delta);
						
			// apply acceleration to change velocity
			Enemy.enemyVelocities.get(i).add(Enemy.enemyAccelerations.get(i));
			
			// checking collisions with the surrounding blocks depending on enemy's velocity
			checkCollisionWithBlocks(delta, i);
	
			// apply damping to halt enemy nicely 
			Enemy.enemyVelocities.get(i).x *= DAMP;
			
			// ensure terminal velocity is not exceeded
			if (Enemy.enemyVelocities.get(i).x > MAX_VEL) {
				Enemy.enemyVelocities.get(i).x = MAX_VEL;
			}
			if (Enemy.enemyVelocities.get(i).x < -MAX_VEL) {
				Enemy.enemyVelocities.get(i).x = -MAX_VEL;
			}

			enemy.update(delta);		// simply updates the state time
	
			/** Prevents Actor from Leaving Game Window **/
			if (Enemy.enemyPositions.get(i).y < 1) {
				Enemy.enemyPositions.get(i).y = 1f;
				
				if (Enemy.enemyStates.get(i).equals(State.JUMPING)) {
						Enemy.enemyStates.set(i, State.IDLE);
				}
			}
			if (Enemy.enemyPositions.get(i).x < 0) {
				Enemy.enemyPositions.get(i).x = 0;

				if (!Enemy.enemyStates.get(i).equals(State.JUMPING)) {
					Enemy.enemyStates.set(i, State.IDLE);
				}
			}	
			if (Enemy.enemyPositions.get(i).x > (float) Level.getWidth() - 0.5) {
				Enemy.enemyPositions.get(i).x = (float) (Level.getWidth() - 0.5);

				if (!Enemy.enemyStates.get(i).equals(State.JUMPING)) {
					Enemy.enemyStates.set(i, State.IDLE);
				}
			}
		}
	}

	/** Collision checking **/
	@SuppressWarnings("deprecation")
	private void checkCollisionWithBlocks(float delta, int j) {
		// scale velocity to frame units 
		Enemy.enemyVelocities.get(j).mul(delta);
		
		rectList.add(null);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle enemyRect = rectPool.obtain();
		// set the rectangle to enemy's bounding box
		enemyRect.set(Enemy.enemyBounds.get(j).x, Enemy.enemyBounds.get(j).y, Enemy.enemyBounds.get(j).width, Enemy.enemyBounds.get(j).height);
		rectList.set(j, enemyRect);
		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) Enemy.enemyBounds.get(j).y;
		int endY = (int) (Enemy.enemyBounds.get(j).y + Enemy.enemyBounds.get(j).height);
		// if enemy is heading left then we check if he collides with the block on his left
		// we check the block on his right otherwise
		if (enemy.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(Enemy.enemyBounds.get(j).x + Enemy.enemyVelocities.get(j).x);
		} else {
			startX = endX = (int) Math.floor(Enemy.enemyBounds.get(j).x + Enemy.enemyBounds.get(j).width + Enemy.enemyVelocities.get(j).x);
		}

		// get the block(s) enemy can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate enemy's movement on the X
		rectList.get(j).x += Enemy.enemyVelocities.get(j).x;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if enemy collides, make his horizontal velocity 0
		for (Block block : collidable) {
			if (block == null) continue;
			if (rectList.get(j).overlaps(block.getBounds())) {
				Enemy.enemyVelocities.get(j).x = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		rectList.get(j).x = Enemy.enemyPositions.get(j).x;
		
		// the same thing but on the vertical Y axis
		startX = (int) Enemy.enemyBounds.get(j).x;
		endX = (int) (Enemy.enemyBounds.get(j).x + Enemy.enemyBounds.get(j).width);
		if (Enemy.enemyVelocities.get(j).y < 0) {
			startY = endY = (int) Math.floor(Enemy.enemyBounds.get(j).y + Enemy.enemyVelocities.get(j).y);
		} else {
			startY = endY = (int) Math.floor(Enemy.enemyBounds.get(j).y + Enemy.enemyBounds.get(j).height + Enemy.enemyVelocities.get(j).y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		rectList.get(j).y += Enemy.enemyVelocities.get(j).y;

		for (Block block : collidable) {
			if (block == null) 
				continue;
			if (rectList.get(j).overlaps(block.getBounds())) {
				if (Enemy.enemyVelocities.get(j).y < 0) {
					grounded = true;
				}
				Enemy.enemyVelocities.get(j).y = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		rectList.get(j).y = Enemy.enemyPositions.get(j).y;
		
		// update enemy's position
		enemy.update(delta);
		
		
		Enemy.enemyPositions.get(j).add(Enemy.enemyVelocities.get(j));
		Enemy.enemyBounds.get(j).x = Enemy.enemyPositions.get(j).x;
		Enemy.enemyBounds.get(j).y = Enemy.enemyPositions.get(j).y;
		
		// un-scale velocity (not in frame time)
		Enemy.enemyVelocities.get(j).mul(1 / delta);
		
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
		
		
		for (int i = 0; i < Enemy.enemyPositions.size(); i++) {
			
			// Walk to Bob
			if (Enemy.enemyPositions.get(i).x > Bob.getPosition().x + 0.5) {
				Enemy.enemyStates.set(i, State.WALKING);
				Enemy.enemyAccelerations.get(i).set(-ACCELERATION, GRAVITY);
			} else if (Enemy.enemyPositions.get(i).x < Bob.getPosition().x - 0.5) {
				Enemy.enemyStates.set(i, State.WALKING);
				Enemy.enemyAccelerations.get(i).set(ACCELERATION, GRAVITY);
			}
			
			// Near eachother
			if (Enemy.enemyPositions.size() > 1) {
				if (i == 0) {
					if (Enemy.enemyPositions.get(i+1).x - Enemy.enemyPositions.get(i).x < 1) {
						Enemy.enemyStates.set(i, State.IDLE);
					} 
				} else if (i == Enemy.enemyPositions.size()) {
					if (Enemy.enemyPositions.get(i).x - Enemy.enemyPositions.get(i-1).x < 1) {
						Enemy.enemyStates.set(i, State.IDLE);
					}
				} else if (Enemy.enemyPositions.get(i).x - Enemy.enemyPositions.get(i-1).x < 1) {
					Enemy.enemyStates.set(i, State.IDLE); 
				}
				
				
				// If enemy touches bob, restart game ***TEMPORARY***
//				if (Enemy.enemyPositions.get(i).epsilonEquals(Bob.getPosition(), (float) 0.5) ) {
//
//					// Reset enemies
//					for (int j = 0; j < Enemy.enemyPositions.size(); j++) {
//						Enemy.removeEnemy(j);
//					}
//					
//					// Reset game
//					((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
//				}
			}
		}
	}	
}
