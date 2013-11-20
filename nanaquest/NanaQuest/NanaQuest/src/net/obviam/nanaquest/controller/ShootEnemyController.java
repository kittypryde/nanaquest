package net.obviam.nanaquest.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.ShootEnemy;
import net.obviam.nanaquest.model.HealthBar;
import net.obviam.nanaquest.model.WalkEnemy;
import net.obviam.nanaquest.model.ShootEnemy.State;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ShootEnemyController {

	enum Keys {
		LEFT, RIGHT, JUMP, FIRE, NONE
	}

	private static final float	ACCELERATION 	= 3f;
	private static final float 	GRAVITY 		= -20f;
	private static final float 	DAMP 			= 0.90f;
	private static final float 	MAX_VEL 		= 2f;
	
	private World 			world;
	private ShootEnemy 		shootEnemy;
	private boolean 		grounded = false;
	
	public List<Rectangle> 	rectList;
	
	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	static Map<Keys, Boolean> keys = new HashMap<ShootEnemyController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};

	// Blocks that ShootEnemy can collide with any given frame
	public Array<Block> collidable = new Array<Block>();
	
	public ShootEnemyController(World world) {
		this.world = world;
		this.shootEnemy = world.getShootEnemy();
		
		if (rectList == null) {
			rectList = new LinkedList<Rectangle>();
		}
	}

	// ** Key presses and touches **************** //
	
	public void leftPressed() {
		keys.get(keys.put(Keys.LEFT, true));
	}
	
	public void rightPressed() {
		keys.get(keys.put(Keys.RIGHT, true));
	}
	
	public void jumpPressed() {
		keys.get(keys.put(Keys.JUMP, true));
	}
	
	public void firePressed() {
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	public void leftReleased() {
		keys.get(keys.put(Keys.LEFT, false));
	}
	
	public void rightReleased() {
		keys.get(keys.put(Keys.RIGHT, false));
	}
	
	public void jumpReleased() {
		keys.get(keys.put(Keys.JUMP, false));
	}
	
	public void fireReleased() {
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	/** The main update method **/
	@SuppressWarnings("deprecation")
	public void update(float delta) {
		
		rectList.clear();
		for (int i = 0; i < ShootEnemy.enemyPositions.size(); i++) {
				
			// setting the states of ShootEnemy
			setBehavior();
			
			// If ShootEnemy is grounded then reset the state to IDLE 
			if (grounded && ShootEnemy.enemyStates.get(i).equals(State.JUMPING)) {
				ShootEnemy.enemyStates.set(i, State.IDLE);
			}
				
			// Setting initial vertical acceleration 
			ShootEnemy.enemyAccelerations.get(i).y = GRAVITY;
			
			// Convert acceleration to frame time
			ShootEnemy.enemyAccelerations.get(i).mul(delta);
			
			// apply acceleration to change velocity
			ShootEnemy.enemyVelocities.get(i).add(ShootEnemy.enemyAccelerations.get(i).x, ShootEnemy.enemyAccelerations.get(i).y);
	
			// checking collisions with the surrounding blocks depending on ShootEnemy's velocity
			checkCollisionWithBlocks(delta, i);
	
			// apply damping to halt ShootEnemy nicely 
			ShootEnemy.enemyVelocities.get(i).x *= DAMP;
			
			// ensure terminal velocity is not exceeded
			
			if (ShootEnemy.enemyVelocities.get(i).x > MAX_VEL) {
				ShootEnemy.enemyVelocities.get(i).x = MAX_VEL;
			}
			if (ShootEnemy.enemyVelocities.get(i).x < -MAX_VEL) {
				ShootEnemy.enemyVelocities.get(i).x = -MAX_VEL;
			}
			
			shootEnemy.update(delta);		// simply updates the state time
	
			/** Prevents Actor from Leaving Game Window **/
			if (ShootEnemy.enemyPositions.get(i).y < 1) {
				ShootEnemy.enemyPositions.get(i).y = 1f;
				shootEnemy.enemyPositions.set(i, ShootEnemy.enemyPositions.get(i));
				if (ShootEnemy.enemyStates.get(i).equals(State.JUMPING)) {
						shootEnemy.enemyStates.set(i, State.IDLE);
				}
			}
			if (ShootEnemy.enemyPositions.get(i).x < 0) {
				ShootEnemy.enemyPositions.get(i).x = 0;
				shootEnemy.enemyPositions.set(i, ShootEnemy.enemyPositions.get(i));
				if (!ShootEnemy.enemyStates.get(i).equals(State.JUMPING)) {
					shootEnemy.enemyStates.set(i, State.IDLE);
				}
			}
			if (ShootEnemy.enemyPositions.get(i).x > (float) Level.getWidth() - 0.5) {
				ShootEnemy.enemyPositions.get(i).x = (float) (Level.getWidth() - 0.5);
				shootEnemy.enemyPositions.set(i, ShootEnemy.enemyPositions.get(i));
				if (!ShootEnemy.enemyStates.get(i).equals(State.JUMPING)) {
					shootEnemy.enemyStates.set(i, State.IDLE);
				}
			}	
		}
	}
	
	/** Collision checking **/
	@SuppressWarnings("deprecation")
	private void checkCollisionWithBlocks(float delta, int j) {
		// scale velocity to frame units 
		ShootEnemy.enemyVelocities.get(j).mul(delta);
		
		rectList.add(null);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle enemyRect = rectPool.obtain();
		// set the rectangle to enemy's bounding box
		enemyRect.set(ShootEnemy.enemyBounds.get(j).x, ShootEnemy.enemyBounds.get(j).y, ShootEnemy.enemyBounds.get(j).width, ShootEnemy.enemyBounds.get(j).height);
		rectList.set(j, enemyRect);
		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) ShootEnemy.enemyBounds.get(j).y;
		int endY = (int) (ShootEnemy.enemyBounds.get(j).y + ShootEnemy.enemyBounds.get(j).height);
		// if enemy is heading left then we check if he collides with the block on his left
		// we check the block on his right otherwise
		if (ShootEnemy.enemyVelocities.get(j).x < 0) {
			startX = endX = (int) Math.floor(ShootEnemy.enemyBounds.get(j).x + ShootEnemy.enemyVelocities.get(j).x);
		} else {
			startX = endX = (int) Math.floor(ShootEnemy.enemyBounds.get(j).x + ShootEnemy.enemyBounds.get(j).width + ShootEnemy.enemyVelocities.get(j).x);
		}

		// get the block(s) enemy can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate enemy's movement on the X
		rectList.get(j).x += ShootEnemy.enemyVelocities.get(j).x;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if enemy collides, make his horizontal velocity 0
		for (Block block : collidable) {
			if (block == null) continue;
			if (rectList.get(j).overlaps(block.getBounds())) {
				ShootEnemy.enemyVelocities.get(j).x = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		rectList.get(j).x = ShootEnemy.enemyPositions.get(j).x;
		
		// the same thing but on the vertical Y axis
		startX = (int) ShootEnemy.enemyBounds.get(j).x;
		endX = (int) (ShootEnemy.enemyBounds.get(j).x + ShootEnemy.enemyBounds.get(j).width);
		if (ShootEnemy.enemyVelocities.get(j).y < 0) {
			startY = endY = (int) Math.floor(ShootEnemy.enemyBounds.get(j).y + ShootEnemy.enemyVelocities.get(j).y);
		} else {
			startY = endY = (int) Math.floor(ShootEnemy.enemyBounds.get(j).y + ShootEnemy.enemyBounds.get(j).height + ShootEnemy.enemyVelocities.get(j).y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		rectList.get(j).y += ShootEnemy.enemyVelocities.get(j).y;

		for (Block block : collidable) {
			if (block == null) 
				continue;
			if (rectList.get(j).overlaps(block.getBounds())) {
				if (ShootEnemy.enemyVelocities.get(j).y < 0) {
					grounded = true;
				}
				ShootEnemy.enemyVelocities.get(j).y = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		rectList.get(j).y = ShootEnemy.enemyPositions.get(j).y;
		
		// update enemy's position
		ShootEnemy.enemyStateTimes.set(j, ShootEnemy.enemyStateTimes.get(j) + delta);		
		
		ShootEnemy.enemyPositions.get(j).add(ShootEnemy.enemyVelocities.get(j));
		ShootEnemy.enemyBounds.get(j).x = ShootEnemy.enemyPositions.get(j).x;
		ShootEnemy.enemyBounds.get(j).y = ShootEnemy.enemyPositions.get(j).y;
		
		// un-scale velocity (not in frame time)
		ShootEnemy.enemyVelocities.get(j).mul(1 / delta);
		
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

	/** Change ShootEnemy's state and parameters based on input controls **/
	@SuppressWarnings("static-access")
	private void setBehavior() {
		
		// Iterates through each enemy
		for (int i = 0; i < ShootEnemy.enemyPositions.size(); i++) {
			
			// Walk to Bob
			if (ShootEnemy.enemyPositions.get(i).x > Bob.getPosition().x + 0.5) {
				ShootEnemy.enemyDirections.set(i, "left");
				ShootEnemy.enemyStates.set(i, State.WALKING);
				ShootEnemy.enemyAccelerations.get(i).set(-ACCELERATION, GRAVITY);
			} else if (ShootEnemy.enemyPositions.get(i).x < Bob.getPosition().x - 0.5) {
				ShootEnemy.enemyDirections.set(i, "right");
				ShootEnemy.enemyStates.set(i, State.WALKING);
				ShootEnemy.enemyAccelerations.get(i).set(ACCELERATION, GRAVITY);
			}
						
			// If enemy touches bob, bob loses health
			if (ShootEnemy.enemyBounds.get(i).overlaps(Bob.getBounds())) {
				
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
