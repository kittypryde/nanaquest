package net.obviam.nanaquest.controller;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.HealthBar;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.Ship;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.model.Ship.State;

public class ShipController {

	private World 	world;
	private Ship	ship;
	private Bob 	bob;
	public static boolean 	shipFlight = false;
	public boolean  grounded = false;
	
	private static final float 	GRAVITY 		= -20f;
	private static final float 	LIFT_SPEED 		= 3f;
	private static final float 	DAMP 			= 0.98f;
	// Ship Controls
	public boolean upPressed = false;
	public boolean downPressed = false;
	
	Vector2 startShip = new Vector2(Ship.getPosition().x, Ship.getPosition().y);
	
	enum Keys {
		LEFT, RIGHT, JUMP, FIRE
	}
	
	static Map<Keys, Boolean> keys = new HashMap<ShipController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};
	
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	public Array<Block> collidable = new Array<Block>();
	
	public ShipController(World world) {
		this.world = world;
		this.ship = world.getShip();
	}
	
	public static void leftPressed() {
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
	
	@SuppressWarnings("deprecation")
	public void update(float delta) {

		Vector2 shipPosition = new Vector2((Ship.getPosition().x + 2), Ship.getPosition().y);

		// Bob walks to ship
		if (Bob.getPosition().x <= shipPosition.x + 0.3 && Bob.getPosition().x >= shipPosition.x - 0.3) {
			if (shipPosition.y < 10 && shipPosition.x <= startShip.x + 2 ) {
				controlShip(delta);
			// Ship stops at 100 x-coordinate
			} else if (shipPosition.x >= (float) 102 ) {
				landShip();
				if (Ship.getState() == State.IDLE) {
					exitBob(delta);
				}
			} else {
				controlShip(delta);
			}
		}
		
		// scale velocity to frame units 
		ship.getVelocity().mul(delta);
		
		
		// Convert acceleration to frame time
		ship.getAcceleration().mul(delta);
		
		// apply acceleration to change velocity
		ship.getVelocity().add(ship.getAcceleration().x, ship.getAcceleration().y);
		
		checkCollisionWithBlocks(delta);
		
		// apply damping to halt Ship nicely 
		ship.getVelocity().y *= DAMP;

		// update ship position
		Ship.getPosition().add(ship.getVelocity());
		
		// un-scale velocity (not in frame time)
		ship.getVelocity().mul(1 / delta);
	}
	
	@SuppressWarnings("deprecation")
	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units 
		ship.getVelocity().mul(delta);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle shipRect = rectPool.obtain();
		// set the rectangle to ship's bounding box
		shipRect.set(ship.getBounds().x, ship.getBounds().y, ship.getBounds().width, ship.getBounds().height);
		
		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) ship.getBounds().y;
		int endY = (int) (ship.getBounds().y + ship.getBounds().height);
		// if ship is heading left then we check if he collides with the block on his left
		// we check the block on his right otherwise
		if (ship.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(ship.getBounds().x + ship.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(ship.getBounds().x + ship.getBounds().width + ship.getVelocity().x);
		}

		// get the block(s) ship can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate ship's movement on the X
		shipRect.x += ship.getVelocity().x;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if ship collides, make horizontal velocity 0
		for (Block block : collidable) {
			if (block == null) continue;
			if (shipRect.overlaps(block.getBounds())) {
				world.getCollisionRects().add(block.getBounds());
				if (ship.getVelocity().y < 0) {
					grounded = true;
				}
				if (shipFlight) {
					Bob.bobDied = true;
				}
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		shipRect.x = Ship.getPosition().x;
		
		// the same thing but on the vertical Y axis
		startX = (int) ship.getBounds().x;
		endX = (int) (ship.getBounds().x + ship.getBounds().width);
		if (ship.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(ship.getBounds().y + ship.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(ship.getBounds().y + ship.getBounds().height + ship.getVelocity().y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		shipRect.y += ship.getVelocity().y;
		
		for (Block block : collidable) {
			if (block == null) continue;
			if (shipRect.overlaps(block.getBounds())) {
				if (ship.getVelocity().y < 0) {
					grounded = true;
				}
				if (shipFlight) {
					Bob.bobDied = true;
				}
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		shipRect.y = Ship.getPosition().y;
		
		// update ship's position
		ship.update(delta);
		Ship.getPosition().add(ship.getVelocity());
		ship.getBounds().x = Ship.getPosition().x;
		ship.getBounds().y = Ship.getPosition().y;
		
		// un-scale velocity (not in frame time)
		ship.getVelocity().mul(1 / delta);
		
		
		/** Prevents Ship from Leaving Game Window **/
		if (Ship.getPosition().y < 1) {
			Ship.getPosition().y = 1f;
			Ship.setPosition(Ship.getPosition());
				ship.getVelocity().y = 0;
		}
		if (Ship.getPosition().x < 0) {
			Ship.getPosition().x = 0;
			Ship.setPosition(Ship.getPosition());
			ship.getVelocity().x = 0;
			ship.getVelocity().y = 0;
		}
		if (Ship.getPosition().x + Ship.width > (float) Level.getWidth()) {
			Ship.getPosition().x = (float) (Level.getWidth() - Ship.width);
			Ship.setPosition(Ship.getPosition());
			ship.getVelocity().x = 0;
			ship.getVelocity().y = 0;
		}
		
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
	
	/* User Takes Control */
	public void controlShip(float delta) {
		
		ship.setState(State.FLYING);
		shipFlight = true;
		
		// Map bob to ship position
		Bob.getPosition().x = Ship.getPosition().x + 2;
		Bob.getPosition().y = Ship.getPosition().y + (float) 0.5;
		Bob.setPosition(Bob.getPosition());
		
		// Forward acceleration and no gravity
		if (ship.getVelocity().x < 5f) {
			ship.getVelocity().x += 0.05; /// Default 0.3 - Change Eventually ****
		}
				
		/* User Controls */
		// take-off
		if (Ship.getPosition().x < startShip.x + 5 && Ship.getPosition().y < 10) {
			ship.getVelocity().y += 0.2;
		// user is flying
		} else if (upPressed) {
			ship.getVelocity().y += 0.5; 
		} else if (downPressed) {
			ship.getVelocity().y += -0.5;
		} else {
			if (ship.getVelocity().y > 0.1) {
				ship.getVelocity().y *= 0.97f;
			}
			if (ship.getVelocity().y < -0.1) {
				ship.getVelocity().y *= 0.97f;
			}
		}
				
	}
	
	/* Automated Ship Landing */
	public void landShip() {
		shipFlight = false;

		ship.setState(State.LANDING);
		
		// slow x velocity
		ship.getVelocity().x *= DAMP;
		
		// lower ship safely
		if (Ship.getPosition().y > 10) {
			ship.getVelocity().y -= 0.3;
		} else if (Ship.getPosition().y > 3) {
			ship.getVelocity().y -= 0.1;
		} else {
			if (ship.getVelocity().y < -0.5) {
				ship.getVelocity().y *= 0.97;
			} else {
				ship.getVelocity().y = (float) -0.3;
			}
		}
		
		
		// Map bob to ship position
		Bob.getPosition().x = Ship.getPosition().x + 2;
		Bob.getPosition().y = Ship.getPosition().y + (float) 0.5;
		
		if (Ship.getPosition().y == 1) {
			ship.getVelocity().x = 0;
			ship.setState(State.IDLE);
		}
		
	}
	
	public void exitBob(float delta) {
		
		Bob.getPosition().add((float) 0.5, 0);
	}
	
}
