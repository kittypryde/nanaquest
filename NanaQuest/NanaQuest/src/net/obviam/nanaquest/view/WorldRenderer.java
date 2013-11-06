package net.obviam.nanaquest.view;

import java.util.LinkedList;
import java.util.List;

import net.obviam.nanaquest.controller.BobController;
import net.obviam.nanaquest.controller.EnemyController;
import net.obviam.nanaquest.controller.ShipController;
import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Bob.State;
import net.obviam.nanaquest.model.Bullet;
import net.obviam.nanaquest.model.Enemy;
import net.obviam.nanaquest.model.HealthBar;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.Ship;
import net.obviam.nanaquest.model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class WorldRenderer {

	private static final float CAMERA_WIDTH = 20f;
	private static final float CAMERA_HEIGHT = 14f;
	private static final float RUNNING_FRAME_DURATION = 0.2f;
	
	/** Bullet stuff **/
	private static final int BULLET_SIZE = 1;
	public Vector2 bullet_pos;
	private Vector2 bulletDirection = new Vector2(0, 0);;
	public List<Vector2> blockList;
	
	/** Enemy **/ 
	public boolean enemyKilled = false;
	
	/** Special Jump Blocks **/
	public boolean specialJump = false;
	
	/** Other Classes **/
	private World 	world;
	/** Camera **/
	public OrthographicCamera cam;

	/** for debug rendering **/
	ShapeRenderer debugRenderer = new ShapeRenderer();

	/** Textures **/
	private Texture shipStopTexture;
	private Texture shipPlatform;
	private Texture shipTexture;
	private Texture background;
	private Texture blockTexture;
	private Texture jumpBlockTexture;
	public Texture leftBulletTexture;
	public Texture rightBulletTexture;
	// Bob
	private TextureRegion bobIdleLeft;
	private TextureRegion bobIdleRight;
	private TextureRegion bobFrame;
	private TextureRegion bobJumpLeft;
	private TextureRegion bobFallLeft;
	private TextureRegion bobJumpRight;
	private TextureRegion bobFallRight;
	// Enemy
	private TextureRegion enemyIdleLeft;
	private TextureRegion enemyIdleRight;
	private TextureRegion enemyFrame;
	private TextureRegion enemyJumpLeft;
	private TextureRegion enemyFallLeft;
	private TextureRegion enemyJumpRight;
	private TextureRegion enemyFallRight;
	// Button
	public Texture rightTexture;
	public Texture leftTexture;
	public Texture upTexture;
	public Texture downTexture;
	public Texture jumpTexture;
	public Texture fireTexture;
	
	
	/** Animations **/
	// bob
	private Animation bobWalkLeftAnimation;
	private Animation bobWalkRightAnimation;
	// enemy
	private Animation enemyWalkLeftAnimation;
	private Animation enemyWalkRightAnimation;
	
	/** SpriteBatches **/
	private SpriteBatch viewport;
	
	private boolean debug = false;
	public void setSize (int w, int h) {
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public WorldRenderer(World world, boolean debug) {
		this.world = world;
		this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
		this.cam.position.set(CAMERA_WIDTH, CAMERA_HEIGHT, 0);
		this.debug = debug;
		viewport = new SpriteBatch();
		bullet_pos = null;
		bulletDirection = new Vector2(0, 0);
		
		if (blockList == null) {
			blockList = new LinkedList<Vector2>();
		}
		
		loadTextures();
	}
	
	public void render(){
		
		updateBullets();
		
		world.getBob();
		world.getEnemy();
		
		viewport = new SpriteBatch();
		viewport.setProjectionMatrix(cam.combined);
		viewport.begin();
		
		// Background - repeated to fill level
		viewport.draw(background, 0, 0, 40, 20);
		viewport.draw(background, 40,0, 40, 20);
		viewport.draw(background, 80,0, 40, 20);
		viewport.draw(background, 120,0, 40, 20);
		
		drawBlocks(); 		// Blocks
		drawBob(); 			// Bob
		drawEnemy();		// Enemies
		drawShip();
		drawBullets(); 		// Bullets
		drawButtons(); 		// Tablet Buttons

		viewport.end();
		if (debug)
			drawDebug();
		
		/** Lock Camera if Actor is near Edge **/
		if (Bob.getPosition().x <= 10) { // far left
			this.cam.position.set(10, Bob.getPosition().y + 1, 0);
		} else if (Bob.getPosition().x >= Level.getWidth() - 10) { // far right
			this.cam.position.set(Level.getWidth() - 10, Bob.getPosition().y + 1, 0);	
		} else { // in-between
			this.cam.position.set(Bob.getPosition().x, Bob.getPosition().y + 1, 0);  // change camera position
		}
		
		
		cam.update();
		dispose();
	}
	
	private void dispose() {
		viewport.dispose();
	}
	
	private void loadTextures() {
		loadDPadTextures();
		loadBobTextures();
		loadEnemyTextures();
			
		shipTexture = new Texture (Gdx.files.internal("images/tempship.fw.png"));
		shipStopTexture = new Texture (Gdx.files.internal("images/stop_flight.fw.png"));
		background = new Texture (Gdx.files.internal("images/background_forest.png"));
		blockTexture = new Texture (Gdx.files.internal("images/block_grey_round_forest.png"));
		jumpBlockTexture = new Texture (Gdx.files.internal("images/jump_block_nq.fw.png"));
		
	}
	
	private void loadBobTextures() {
		TextureAtlas bobAtlas = new TextureAtlas(Gdx.files.internal("images/textures/nanaPack.txt"));
		
		bobIdleLeft = bobAtlas.findRegion("nana_run-01");
		bobIdleRight = new TextureRegion(bobIdleLeft);
		bobIdleRight.flip(false, false);
		
		TextureRegion[] bobWalkLeftFrames = new TextureRegion[5];
		
		for (int i = 0; i < 5; i++) {
			bobWalkLeftFrames[i] = bobAtlas.findRegion("nana_run-0" + (i + 1));
			bobWalkLeftFrames[i].flip(true, false);
		}
		
		bobWalkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, bobWalkLeftFrames);
		
		TextureRegion[] bobWalkRightFrames = new TextureRegion[5];
		
		for (int i = 0; i < 5; i++) {
			bobWalkRightFrames[i] = new TextureRegion(bobWalkLeftFrames[i]);
			bobWalkRightFrames[i].flip(true, false);
		}
		
		bobWalkRightAnimation = new Animation(RUNNING_FRAME_DURATION, bobWalkRightFrames);
		bobJumpLeft = bobAtlas.findRegion("nana_jump_up");
		bobJumpLeft.flip(true, false);
		bobJumpRight = new TextureRegion(bobJumpLeft);
		bobJumpRight.flip(true, false);
		bobFallLeft = bobAtlas.findRegion("nana_jump_down");
		bobFallLeft.flip(true, false);
		bobFallRight = new TextureRegion(bobFallLeft);
		bobFallRight.flip(true, false);
	}
	
	private void loadEnemyTextures() {
		TextureAtlas enemyAtlas = new TextureAtlas(Gdx.files.internal("images/textures/enemyPack.txt"));
		enemyIdleLeft = enemyAtlas.findRegion("enemy-01.fw");
		enemyIdleRight = new TextureRegion(enemyIdleLeft);
		enemyIdleRight.flip(true, false);
		
		TextureRegion[] enemyWalkLeftFrames = new TextureRegion[5];

		for (int i = 0; i < 5; i++) {
			enemyWalkLeftFrames[i] = enemyAtlas.findRegion("enemy-0" + (i+2) + ".fw");
		}
		
		enemyWalkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, enemyWalkLeftFrames);

		TextureRegion[] enemyWalkRightFrames = new TextureRegion[5];

		for (int i = 0; i < 5; i++) {
			enemyWalkRightFrames[i] = new TextureRegion(enemyWalkLeftFrames[i]);
			enemyWalkRightFrames[i].flip(true, false);
		}
		
		enemyWalkRightAnimation = new Animation(RUNNING_FRAME_DURATION, enemyWalkRightFrames);
		enemyJumpLeft = enemyAtlas.findRegion("enemy-up.fw");
		enemyJumpRight = new TextureRegion(enemyJumpLeft);
		enemyJumpRight.flip(true, false);
		enemyFallLeft = enemyAtlas.findRegion("enemy-down.fw");
		enemyFallRight = new TextureRegion(enemyFallLeft);
		enemyFallRight.flip(true, false);
		
	}

	public void loadDPadTextures() {
		rightTexture = new Texture(Gdx.files.internal("images/right_arrow_nq.png"));
		leftTexture = new Texture(Gdx.files.internal("images/left_arrow_nq.png"));
		upTexture = new Texture(Gdx.files.internal("images/down_arrow_nq.png"));
		downTexture = new Texture(Gdx.files.internal("images/up_arrow_nq.png"));
		jumpTexture = new Texture(Gdx.files.internal("images/jump_button_nq.png"));
		fireTexture = new Texture(Gdx.files.internal("images/fire_button_nq.png"));
	}
	
	public void drawButtons() {
		// Buttons
		if (ShipController.shipFlight) {
			viewport.draw(upTexture, cam.position.x - 8, cam.position.y - 6, 2, 2);	
			viewport.draw(downTexture, cam.position.x - 10, cam.position.y - 4, 2, 2);
		} else {	
			viewport.draw(rightTexture, cam.position.x - 8, cam.position.y - 6, 2, 2);	
			viewport.draw(leftTexture, cam.position.x - 10, cam.position.y - 4, 2, 2);
		}
		viewport.draw(jumpTexture, cam.position.x + 8, cam.position.y - 4, 2, 2);	
		viewport.draw(fireTexture, cam.position.x + 6, cam.position.y - 6, 2, 2);
	}
	

	private void drawBlocks() {
		blockList.clear();
		for (Block block : world.getDrawableBlocks(Level.getWidth(), Level.getHeight())) {
			viewport.draw(blockTexture, block.getPosition().x, block.getPosition().y, Block.SIZE, Block.SIZE);
			blockList.add(block.getPosition());
			
			// add special jump block
			viewport.draw(jumpBlockTexture, 8, 0, Block.SIZE, Block.SIZE);
		}
	}
	

	private void drawBob() {
		Bob bob = world.getBob();
		bobFrame = Bob.isFacingLeft() ? bobIdleLeft : bobIdleRight;
		if(Bob.getState().equals(State.WALKING)) {
			bobFrame = Bob.isFacingLeft() ? bobWalkLeftAnimation.getKeyFrame(bob.getStateTime(), true) : bobWalkRightAnimation.getKeyFrame(bob.getStateTime(), true);
		} else if (Bob.getState().equals(State.JUMPING)) {
			if (Bob.getVelocity().y > 0) {
				bobFrame = Bob.isFacingLeft() ? bobJumpLeft : bobJumpRight;
			} else {
				bobFrame = Bob.isFacingLeft() ? bobFallLeft : bobFallRight;
			}
		}
		viewport.draw(bobFrame, Bob.getPosition().x, Bob.getPosition().y, Bob.SIZE, Bob.SIZE);
		
		// special jump
		if ((int)(Bob.getPosition().x + 0.2) == 8 && (int)Bob.getPosition().y == 1) {
			Bob.setState(State.JUMPING);
			Bob.getVelocity().y = 7f; 
		}
	}
	

	private void drawEnemy() {
		Enemy enemy = world.getEnemy();
		
		for (int i = 0; i < Enemy.enemyPositions.size(); i++) {
		
			if (Enemy.enemyPositions.get(i).x > Bob.getPosition().x + 0.5) {
				enemyFrame = enemyWalkLeftAnimation.getKeyFrame(enemy.getStateTime(), true);
				Enemy.enemyFrames.set(i, enemyIdleLeft);
			} else if (Enemy.enemyPositions.get(i).x < Bob.getPosition().x - 0.5) {
				enemyFrame = enemyWalkRightAnimation.getKeyFrame(enemy.getStateTime(), true);
				Enemy.enemyFrames.set(i, enemyIdleRight);
			}
			viewport.draw(Enemy.enemyFrames.get(i), Enemy.enemyPositions.get(i).x, Enemy.enemyPositions.get(i).y, Enemy.SIZE, Enemy.SIZE);
		}
	}

	private void drawShip() {
		// Ship
		viewport.draw(shipTexture, Ship.getPosition().x, Ship.getPosition().y, Ship.width, Ship.height);
	
		// Ship-End
		viewport.draw(shipStopTexture, 100, -2, 4, 20);
	}
	
	/** Bullet Motion **/
	private void updateBullets() {
		if (Bullet.firePressed) {
			
			for (int i = 0; i < Bullet.bulletPositions.size(); i++) {
			
				if (Bob.isFacingLeft()) {  // bob facing left
					bullet_pos = (new Vector2(Bob.getPosition().cpy().add( 
							(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) + 0.6),
							(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) - 0.1)))); 
					bulletDirection.set(new Vector2((float)-0.1, 0)); 
				} else {
					bullet_pos = (new Vector2(Bob.getPosition().cpy().add( 
							(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) + 0.9),
							(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) - 0.1))));
					bulletDirection.set(new Vector2((float)0.1, 0)); 
				}		
			
				if (bullet_pos != null) { 
					if (bullet_pos.x < 0 || bullet_pos.x > Level.getWidth() || bullet_pos.y < 0 || bullet_pos.y > Level.getHeight()) {
						bullet_pos = null;
					}
				}
			}
					
		}
	}
	
	
	/** Draw Bullets **/   					// FIX THIS
	private void drawBullets() {
		
		new Bullet();
		new EnemyController(world);
		new BobController(world);
		
		for (int i = 0; i < Bullet.bulletPositions.size(); i++) {	
				
			if (blockList.size() > 0 && Bullet.bulletPositions.size() > 0) {
			
				// Remove bullet if bullet collides with blocks
				if (blockList.contains(new Vector2((int)Bullet.bulletPositions.get(i).x, (int) Bullet.bulletPositions.get(i).y))) {
					Bullet.removeBullet(i);
					continue;
				}
				
				if (blockList.contains(new Vector2((int)(Bullet.bulletPositions.get(i).x + 0.6), (int) Bullet.bulletPositions.get(i).y))) {
					Bullet.removeBullet(i);
					continue;
				}
			}
			
	
			// Remove bullet if bullet leaves screen
			if (Bob.getPosition().x <= 10) { // far left
				if (Bullet.bulletPositions.get(i).x >= 20) {
					Bullet.removeBullet(i);
					continue;
				}
			} else if (Bob.getPosition().x >= Level.getWidth() - 10) { // far right
				if (Bullet.bulletPositions.get(i).x <= Level.getWidth() - 20) {
					Bullet.removeBullet(i);
					continue;
				}
			} else if (Bullet.bulletPositions.get(i).x > Bob.getPosition().x + 11 || Bullet.bulletPositions.get(i).x > Level.getWidth()) {
				Bullet.removeBullet(i);
				continue;
			} else if (Bullet.bulletPositions.get(i).x < Bob.getPosition().x - 11 || Bullet.bulletPositions.get(i).x <= 0) {
				Bullet.removeBullet(i);
				continue;
			}
			
			// Bullet Killed Enemy
			try {
				for (int e = 0; e < Enemy.enemyPositions.size(); e++) {
					if (Bullet.bulletPositions.size() == 0) {
						continue;
					} else if (Bullet.bulletPositions.get(i).epsilonEquals(Enemy.enemyPositions.get(e), (float) 0.3)) {
						Enemy.removeEnemy(e);
						Bullet.removeBullet(i);
						continue;
					}
				}
				
				if (Bullet.bulletPositions.size() == 0) {
					continue;
				}
				
				/* Draw Bullets */
				if (bullet_pos != null) {
					bullet_pos.add(bulletDirection);
	
					Bullet.bulletPositions.set(i, (Bullet.bulletPositions.get(i).add(Bullet.bulletAdds.get(i))));
					viewport.draw(Bullet.bulletTextures.get(i), (float)(Bullet.bulletPositions.get(i).x),
							(float)(Bullet.bulletPositions.get(i).y), (float) 0.5, (float) 0.5);
				}
			} catch (IndexOutOfBoundsException e) {
					// ignore glitch
			}
		}
	}	

	
	private void drawDebug() {
		
		try {
			// render blocks
			debugRenderer.end();
			debugRenderer.begin(ShapeType.Line);
			debugRenderer.setProjectionMatrix(cam.combined);
			for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
				Rectangle rect = block.getBounds();
				debugRenderer.setColor(new Color(1, 0, 0, 1));
				debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			}
			
			// render enemies
			debugRenderer.setProjectionMatrix(cam.combined);
			for (int i = 0; i < Enemy.enemyPositions.size(); i++) {
				Rectangle rect = Enemy.enemyBounds.get(i);
				debugRenderer.setColor(new Color(1, 0, 0, 1));
				debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			}
			
			// render ship
			Ship ship = world.getShip();
			Rectangle shipRect = ship.getBounds();
			debugRenderer.setColor(new Color(0, 1, 0, 1));
			debugRenderer.rect(shipRect.x, shipRect.y, shipRect.width, shipRect.height);
			
			// render Bob
			Bob bob = world.getBob();
			Rectangle rect = bob.getBounds();
			debugRenderer.setColor(new Color(0, 1, 0, 1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			debugRenderer.end();
			
		} catch (ArrayIndexOutOfBoundsException a) {
			System.out.println("oh well");
		}
	}
}
