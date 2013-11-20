package net.obviam.nanaquest.controller;

import java.util.HashMap;
import java.util.Map;

import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Boss;
import net.obviam.nanaquest.model.HealthBar;
import net.obviam.nanaquest.model.Boss.State;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BossController {

	enum Keys {
		LEFT, RIGHT, JUMP, FIRE, NONE
	}

	private static final float	ACCELERATION 	= 3f;
	private static final float 	GRAVITY 		= -20f;
	private static final float 	DAMP 			= 0.90f;
	private static final float 	MAX_VEL 		= 2f;
	
	private World 			world;
	private Boss 			boss;
	private boolean 		grounded = false;
	
	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	static Map<Keys, Boolean> keys = new HashMap<BossController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};

	// Blocks that Boss can collide with any given frame
	public Array<Block> collidable = new Array<Block>();
	
	public BossController(World world) {
		this.world = world;
		this.boss = world.getBoss();
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
		// Processing the input - setting the states of Boss
		setBehavior();
		
		// If Boss is grounded then reset the state to IDLE 
		if (grounded && Boss.getState().equals(State.JUMPING)) {
			boss.setState(State.IDLE);
		}
		
		if (ShipController.shipFlight == true) {
			keys.put(Keys.LEFT, false);
			keys.put(Keys.RIGHT, false);
			keys.put(Keys.JUMP, false);
			keys.put(Keys.FIRE, false);
		}
				
		// Setting initial vertical acceleration 
		Boss.getAcceleration().y = GRAVITY;
		
		// Convert acceleration to frame time
		Boss.getAcceleration().mul(delta);
		
		// apply acceleration to change velocity
		Boss.getVelocity().add(Boss.getAcceleration().x, Boss.getAcceleration().y);

		// checking collisions with the surrounding blocks depending on Boss's velocity
		checkCollisionWithBlocks(delta);

		// apply damping to halt Boss nicely 
		Boss.getVelocity().x *= DAMP;
		
		// ensure terminal velocity is not exceeded
		
		
		if (Boss.getVelocity().x > MAX_VEL) {
			Boss.getVelocity().x = MAX_VEL;
		}
		if (Boss.getVelocity().x < -MAX_VEL) {
			Boss.getVelocity().x = -MAX_VEL;
		}
		
		

		boss.update(delta);		// simply updates the state time

		/** Prevents Actor from Leaving Game Window **/
		if (Boss.getPosition().y < 1) {
			Boss.getPosition().y = 1f;
			Boss.setPosition(Boss.getPosition());
			if (Boss.getState().equals(State.JUMPING)) {
					boss.setState(State.IDLE);
			}
		}
		if (Boss.getPosition().x < 0) {
			Boss.getPosition().x = 0;
			Boss.setPosition(Boss.getPosition());
			if (!Boss.getState().equals(State.JUMPING)) {
				boss.setState(State.IDLE);
			}
		}
		if (Boss.getPosition().x > (float) Level.getWidth() - 0.5) {
			Boss.getPosition().x = (float) (Level.getWidth() - 0.5);
			Boss.setPosition(Boss.getPosition());
			if (!Boss.getState().equals(State.JUMPING)) {
				boss.setState(State.IDLE);
			}
		}
	}

	/** Collision checking **/
	@SuppressWarnings("deprecation")
	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units 
		Boss.getVelocity().mul(delta);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle bossRect = rectPool.obtain();
		// set the rectangle to boss's bounding box
		bossRect.set(boss.getBounds().x, boss.getBounds().y, boss.getBounds().width, boss.getBounds().height);
		
		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) boss.getBounds().y;
		int endY = (int) (boss.getBounds().y + boss.getBounds().height);
		// if boss is heading left then we check if he collides with the block on his left
		// we check the block on his right otherwise
		if (Boss.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(boss.getBounds().x + Boss.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(boss.getBounds().x + boss.getBounds().width + Boss.getVelocity().x);
		}

		// get the block(s) boss can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate boss's movement on the X
		bossRect.x += Boss.getVelocity().x;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if boss collides, make his horizontal velocity 0
		for (Block block : collidable) {
			if (block == null) continue;
			if (bossRect.overlaps(block.getBounds())) {
				Boss.getVelocity().x = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		bossRect.x = Boss.getPosition().x;
		
		// the same thing but on the vertical Y axis
		startX = (int) boss.getBounds().x;
		endX = (int) (boss.getBounds().x + boss.getBounds().width);
		if (Boss.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(boss.getBounds().y + Boss.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(boss.getBounds().y + boss.getBounds().height + Boss.getVelocity().y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		bossRect.y += Boss.getVelocity().y;
		
		for (Block block : collidable) {
			if (block == null) 
				continue;
			if (bossRect.overlaps(block.getBounds())) {
				if (Boss.getVelocity().y <= 0) {
					grounded = true;
				}
				Boss.getVelocity().y = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		bossRect.y = Boss.getPosition().y;
		
		// update boss's position
		boss.update(delta);
		Boss.getPosition().add(Boss.getVelocity());
		boss.getBounds().x = Boss.getPosition().x;
		boss.getBounds().y = Boss.getPosition().y;
		
		// un-scale velocity (not in frame time)
		Boss.getVelocity().mul(1 / delta);
		
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

	/** Change boss's state and parameters based on input controls **/
	@SuppressWarnings("static-access")
	private void setBehavior() {
	
		// boss touching bob
		if (boss.getBounds().overlaps(Bob.getBounds())) {
			boss.setState(State.IDLE);
			boss.getAcceleration().x = 0;
		} else if (boss.getPosition().x > Bob.getPosition().x + 0.5) { 	// boss walk left
			boss.setState(State.WALKING);
			boss.getAcceleration().x = -ACCELERATION;
			boss.setFacingLeft(true);
		} else if (boss.getPosition().x < Bob.getPosition().x - 0.5) { 	// boss walk right
			boss.setState(State.WALKING);
			boss.getAcceleration().x = ACCELERATION;
			boss.setFacingLeft(false);
		}
		
		if (boss.getBounds().overlaps(Bob.getBounds())) {
			boss.setState(State.IDLE);
			boss.getAcceleration().x = 0;
		}
		
		if (HealthBar.bobHealth > 0 && !boss.bossDied) {
			// boss touches Bob - Bob loses health
			if (boss.getBounds().overlaps(Bob.getBounds())) {
				HealthBar.bobHealth -= 0.1;
			}
			
			// Bob Dies
			if (HealthBar.bobHealth <= 0){
				Bob.bobDied = true;
			}
		}
	}
}