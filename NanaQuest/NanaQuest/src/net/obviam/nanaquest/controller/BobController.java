package net.obviam.nanaquest.controller;

import java.util.HashMap;
import java.util.Map;

import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Ship;
import net.obviam.nanaquest.model.Bob.State;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BobController {

	enum Keys {
		LEFT, RIGHT, JUMP, FIRE, NONE
	}

	private static final long 	LONG_JUMP_PRESS = 150l;
	private static final float	ACCELERATION 	= 20f;
	private static final float 	GRAVITY 		= -20f;
	private static final float 	MAX_JUMP_SPEED	= 7f;
	private static final float 	DAMP 			= 0.90f;
	private static final float 	MAX_VEL 		= 3f;
	
	private World 	world;
	private Bob 	bob;
	private long	jumpPressedTime;
	private boolean jumpingPressed;
	private boolean grounded = false;
	
	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	static Map<Keys, Boolean> keys = new HashMap<BobController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};

	// Blocks that Bob can collide with any given frame
	public Array<Block> collidable = new Array<Block>();
	
	public BobController(World world) {
		this.world = world;
		this.bob = world.getBob();
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
		// Processing the input - setting the states of Bob
		processInput();
		
		// If Bob is grounded then reset the state to IDLE 
		if (grounded && Bob.getState().equals(State.JUMPING)) {
			Bob.setState(State.IDLE);
		}
		
		if (ShipController.shipFlight == true) {
			keys.put(Keys.LEFT, false);
			keys.put(Keys.RIGHT, false);
			keys.put(Keys.JUMP, false);
			keys.put(Keys.FIRE, false);
		}
		
		if (Bob.getPosition().x > Ship.getPosition().x + 2 && Bob.getPosition().x < Ship.getPosition().x + 4) {
			exitBob(delta);
		} 
		
		if (Bob.getPosition().x > Ship.getPosition().x + 4 && Bob.getPosition().x < Ship.getPosition().x + 5) {
			ShipController.shipFlight = false;
			if (Bob.getAcceleration().x > 0) {
				Bob.getAcceleration().x -= 0.5;
			}
			Bob.getAcceleration().y = GRAVITY;
		}
		
		// Setting initial vertical acceleration 
		Bob.getAcceleration().y = GRAVITY;
		
		// Convert acceleration to frame time
		Bob.getAcceleration().mul(delta);
		
		// apply acceleration to change velocity
		Bob.getVelocity().add(Bob.getAcceleration().x, Bob.getAcceleration().y);

		// checking collisions with the surrounding blocks depending on Bob's velocity
		checkCollisionWithBlocks(delta);

		// apply damping to halt Bob nicely 
		Bob.getVelocity().x *= DAMP;
		
		// ensure terminal velocity is not exceeded
		
		
		if (Bob.getVelocity().x > MAX_VEL) {
			Bob.getVelocity().x = MAX_VEL;
		}
		if (Bob.getVelocity().x < -MAX_VEL) {
			Bob.getVelocity().x = -MAX_VEL;
		}
		
		

		bob.update(delta);		// simply updates the state time

		/** Prevents Actor from Leaving Game Window **/
		if (Bob.getPosition().y < 1) {
			Bob.getPosition().y = 1f;
			Bob.setPosition(Bob.getPosition());
			if (Bob.getState().equals(State.JUMPING)) {
					Bob.setState(State.IDLE);
			}
		}
		if (Bob.getPosition().x < 0) {
			Bob.getPosition().x = 0;
			Bob.setPosition(Bob.getPosition());
			if (!Bob.getState().equals(State.JUMPING)) {
				Bob.setState(State.IDLE);
			}
		}
		if (Bob.getPosition().x > (float) Level.getWidth() - 0.5) {
			Bob.getPosition().x = (float) (Level.getWidth() - 0.5);
			Bob.setPosition(Bob.getPosition());
			if (!Bob.getState().equals(State.JUMPING)) {
				Bob.setState(State.IDLE);
			}
		}
	}

	/** Collision checking **/
	@SuppressWarnings("deprecation")
	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units 
		Bob.getVelocity().mul(delta);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle bobRect = rectPool.obtain();
		// set the rectangle to bob's bounding box
		bobRect.set(bob.getBounds().x, bob.getBounds().y, bob.getBounds().width, bob.getBounds().height);
		
		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) bob.getBounds().y;
		int endY = (int) (bob.getBounds().y + bob.getBounds().height);
		// if Bob is heading left then we check if he collides with the block on his left
		// we check the block on his right otherwise
		if (Bob.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(bob.getBounds().x + Bob.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(bob.getBounds().x + bob.getBounds().width + Bob.getVelocity().x);
		}

		// get the block(s) bob can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate bob's movement on the X
		bobRect.x += Bob.getVelocity().x;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if bob collides, make his horizontal velocity 0
		for (Block block : collidable) {
			if (block == null) continue;
			if (bobRect.overlaps(block.getBounds())) {
				Bob.getVelocity().x = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		bobRect.x = Bob.getPosition().x;
		
		// the same thing but on the vertical Y axis
		startX = (int) bob.getBounds().x;
		endX = (int) (bob.getBounds().x + bob.getBounds().width);
		if (Bob.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(bob.getBounds().y + Bob.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(bob.getBounds().y + bob.getBounds().height + Bob.getVelocity().y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		bobRect.y += Bob.getVelocity().y;
		
		for (Block block : collidable) {
			if (block == null) 
				continue;
			if (bobRect.overlaps(block.getBounds())) {
				if (Bob.getVelocity().y <= 0) {
					grounded = true;
				}
				Bob.getVelocity().y = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		bobRect.y = Bob.getPosition().y;
		
		// update Bob's position
		bob.update(delta);
		Bob.getPosition().add(Bob.getVelocity());
		bob.getBounds().x = Bob.getPosition().x;
		bob.getBounds().y = Bob.getPosition().y;
		
		// un-scale velocity (not in frame time)
		Bob.getVelocity().mul(1 / delta);
		
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
	

	/** Change Bob's state and parameters based on input controls **/
	private boolean processInput() {
		if (keys.get(Keys.JUMP)) {
			if (!Bob.getState().equals(State.JUMPING)) {
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				Bob.setState(State.JUMPING);
				Bob.getVelocity().y = MAX_JUMP_SPEED; 
				grounded = false;
			} else {
				if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						Bob.getVelocity().y = MAX_JUMP_SPEED;
					}
				}
			}
		}
		if (keys.get(Keys.LEFT)) {						// left is pressed
			Bob.setFacingLeft(true);
			if (!Bob.getState().equals(State.JUMPING)) {
		 		Bob.setState(State.WALKING);
			}
			Bob.getAcceleration().x = -ACCELERATION;
		} else if (keys.get(Keys.RIGHT)) {				// right is pressed
			Bob.setFacingLeft(false);
			if (!Bob.getState().equals(State.JUMPING)) {
				Bob.setState(State.WALKING);
			}
			Bob.getAcceleration().x = ACCELERATION;
		} else {
			if (!Bob.getState().equals(State.JUMPING)) {
				Bob.setState(State.IDLE);
			}
			Bob.getAcceleration().x = 0;	
		}
		return false;
	}

	public static void exitBob(float delta) {
		
		Bob.setFacingLeft(false);
		Bob.setState(State.WALKING);
		Bob.getAcceleration().x += ACCELERATION;
		
	}
}