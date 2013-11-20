package net.obviam.nanaquest.view;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import net.obviam.nanaquest.controller.BobController;
import net.obviam.nanaquest.controller.BossController;
import net.obviam.nanaquest.controller.WalkEnemyController;
import net.obviam.nanaquest.controller.ShipController;
import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Bob.State;
import net.obviam.nanaquest.model.Boss;
import net.obviam.nanaquest.model.Bullet;
import net.obviam.nanaquest.model.ShootEnemy;
import net.obviam.nanaquest.model.WalkEnemy;
import net.obviam.nanaquest.model.HealthBar;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.Ship;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.screens.GameScreen;

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
	
	int bossCount = 0;
	int shootEnemyCount = 0;

	private static final float CAMERA_WIDTH = 20f;
	private static final float CAMERA_HEIGHT = 14f;
	private static final float BOB_RUNNING_FRAME_DURATION = 0.2f;
	private static final float ENEMY_RUNNING_FRAME_DURATION = 0.3f;
	private static final float BOSS_RUNNING_FRAME_DURATION = 0.3f;
	
	/** Health Bar **/
	HealthBar healthBar;
	
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
	private Texture winMessage;
	private Texture shipStopTexture;
	private Texture shipTexture;
	private Texture background;
	// Blocks
	private Texture blockTexture;
	private Texture jumpBlockTexture;
	// Bullets
	public Texture leftBulletTexture;
	public Texture rightBulletTexture;
	public Texture bossAttackTexture;
	// Health Bars
	private Texture bobHealthBar;
	private Texture bossHealthBar;
	// Bob
	private TextureRegion bobIdleLeft;
	private TextureRegion bobIdleRight;
	private TextureRegion bobFrame;
	private TextureRegion bobJumpLeft;
	private TextureRegion bobFallLeft;
	private TextureRegion bobJumpRight;
	private TextureRegion bobFallRight;
	// Enemies
	private TextureRegion bugIdleLeft;
	private TextureRegion bugIdleRight;
	private TextureRegion bugFrame;
	private TextureRegion spiderIdleLeft;
	private TextureRegion spiderIdleRight;
	private TextureRegion spiderFrame;
	// Boss
	private TextureRegion bossIdleLeft;
	private TextureRegion bossIdleRight;
	private TextureRegion bossFrame;
	private TextureRegion bossJumpLeft;
	private TextureRegion bossFallLeft;
	private TextureRegion bossJumpRight;
	private TextureRegion bossFallRight;
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
	// enemies
	private Animation bugWalkLeftAnimation;
	private Animation bugWalkRightAnimation;
	private Animation spiderWalkLeftAnimation;
	private Animation spiderWalkRightAnimation;
	// boss
	private Animation bossWalkLeftAnimation;
	private Animation bossWalkRightAnimation;
	
	/** SpriteBatches **/
	public static SpriteBatch viewport;
	
	private boolean debug = false;
	private boolean newShootEnemyBullets = false;
	
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
		world.getWalkEnemy();
		world.getShootEnemy();
		
		viewport = new SpriteBatch();
		viewport.setProjectionMatrix(cam.combined);
		viewport.begin();
		
		// Background - repeated to fill level
		viewport.draw(background, 0, -8, 50, 40);
		viewport.draw(background, 50, -8, 50, 40);
		viewport.draw(background, 100, -8, 50, 40);
		
		drawBlocks(); 		// Blocks
		drawBob(); 			// Bob
		drawWalkEnemy();	// Walk Enemies
		drawShootEnemy();	// Shoot Enemies
		drawShip();			// Ship
		drawBullets(); 		// Bullets
		drawHealthBar();	// Health Bar
		drawButtons(); 		// Tablet Buttons

		
		if (debug)
			drawDebug();
		
		/** Lock Camera if Actor is near Edge **/
		if (Bob.getPosition().x <= 10) { // far left
			this.cam.position.set(10, Bob.getPosition().y + 1, 0);
		} else if (Bob.getPosition().x >= Level.getWidth() - 10) { // far right - Boss
			this.cam.position.set(Level.getWidth() - 10, Bob.getPosition().y + 1, 0);	
			Boss.bossBattle = true;
			WalkEnemy.removeAll();
			drawBoss(); // Start boss fight
		} else if (Boss.bossBattle && Bob.getPosition().x < Level.getWidth() - 10) { // Boss Battle
			this.cam.position.set(Level.getWidth() - 10, Bob.getPosition().y + 1, 0);	
			drawBoss(); // Start boss fight
		} else { // in-between
			this.cam.position.set(Bob.getPosition().x, Bob.getPosition().y + 1, 0);  // change camera position
		}
		
		viewport.end();
		
		cam.update();
		dispose();
	}
	
	public void dispose() {
		viewport.dispose();
	}
	
	private void loadTextures() {
		loadDPadTextures();
		loadBobTextures();
		loadWalkEnemyTextures();
		loadShootEnemyTextures();
		loadBossTextures();
			
		bossAttackTexture = new Texture (Gdx.files.internal("images/enemy_attack.fw.png"));
		winMessage = new Texture (Gdx.files.internal("images/win_banner.fw.png"));
		bobHealthBar = new Texture (Gdx.files.internal("images/health_points.fw.png"));
		bossHealthBar = new Texture (Gdx.files.internal("images/health_points_boss.fw.png"));
		shipTexture = new Texture (Gdx.files.internal("images/tempship.fw.png"));
		shipStopTexture = new Texture (Gdx.files.internal("images/stop_flight.fw.png"));
		background = new Texture (Gdx.files.internal("images/background_forest.png"));
		blockTexture = new Texture (Gdx.files.internal("images/block_grey_round_forest.png"));
		jumpBlockTexture = new Texture (Gdx.files.internal("images/jump_block_nq.fw.png"));
		
	}
	
	private void loadBobTextures() {
		TextureAtlas bobAtlas = new TextureAtlas(Gdx.files.internal("images/textures/nanaPack.txt"));
		
		bobIdleLeft = bobAtlas.findRegion("nana_run_1");
		bobIdleRight = new TextureRegion(bobIdleLeft);
		bobIdleRight.flip(false, false);
		
		TextureRegion[] bobWalkLeftFrames = new TextureRegion[5];
		
		for (int i = 0; i < 5; i++) {
			bobWalkLeftFrames[i] = bobAtlas.findRegion("nana_run_" + (i + 1));
			bobWalkLeftFrames[i].flip(true, false);
		}
		
		bobWalkLeftAnimation = new Animation(BOB_RUNNING_FRAME_DURATION, bobWalkLeftFrames);
		
		TextureRegion[] bobWalkRightFrames = new TextureRegion[5];
		
		for (int i = 0; i < 5; i++) {
			bobWalkRightFrames[i] = new TextureRegion(bobWalkLeftFrames[i]);
			bobWalkRightFrames[i].flip(true, false);
		}
		
		bobWalkRightAnimation = new Animation(BOB_RUNNING_FRAME_DURATION, bobWalkRightFrames);
		bobJumpLeft = bobAtlas.findRegion("nana_jump_up");
		bobJumpLeft.flip(true, false);
		bobJumpRight = new TextureRegion(bobJumpLeft);
		bobJumpRight.flip(true, false);
		bobFallLeft = bobAtlas.findRegion("nana_jump_down");
		bobFallLeft.flip(true, false);
		bobFallRight = new TextureRegion(bobFallLeft);
		bobFallRight.flip(true, false);
	}
	
	private void loadWalkEnemyTextures() {
		
		TextureAtlas forestBugAtlas = new TextureAtlas(Gdx.files.internal("images/textures/forest_bugPack.txt"));
		TextureAtlas forestSpiderAtlas = new TextureAtlas(Gdx.files.internal("images/textures/forestSpiderPack.txt"));			
		
		bugIdleLeft = forestBugAtlas.findRegion("forest_bug-01");
		spiderIdleLeft = forestSpiderAtlas.findRegion("forest_spider-1");
		
		bugIdleRight = new TextureRegion(bugIdleLeft);
		bugIdleRight.flip(true, false);
		spiderIdleRight = new TextureRegion(spiderIdleLeft);
		spiderIdleRight.flip(true, false);
		
		TextureRegion[] bugWalkLeftFrames = new TextureRegion[3];
		TextureRegion[] spiderWalkLeftFrames = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			bugWalkLeftFrames[i] = forestBugAtlas.findRegion("forest_bug-0" + (i+1));
			spiderWalkLeftFrames[i] = forestSpiderAtlas.findRegion("forest_spider-" + (i+1));
		}
		
		bugWalkLeftAnimation = new Animation(ENEMY_RUNNING_FRAME_DURATION, bugWalkLeftFrames);
		spiderWalkLeftAnimation = new Animation(ENEMY_RUNNING_FRAME_DURATION, spiderWalkLeftFrames);

		TextureRegion[] bugWalkRightFrames = new TextureRegion[3];
		TextureRegion[] spiderWalkRightFrames = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			bugWalkRightFrames[i] = new TextureRegion(bugWalkLeftFrames[i]);
			bugWalkRightFrames[i].flip(true, false);
			spiderWalkRightFrames[i] = new TextureRegion(spiderWalkLeftFrames[i]);
			spiderWalkRightFrames[i].flip(true, false);
		}
		
		bugWalkRightAnimation = new Animation(ENEMY_RUNNING_FRAME_DURATION, bugWalkRightFrames);
		spiderWalkRightAnimation = new Animation(ENEMY_RUNNING_FRAME_DURATION, spiderWalkRightFrames);
	}
	
	private void loadShootEnemyTextures() {
		TextureAtlas spiderAtlas = new TextureAtlas(Gdx.files.internal("images/textures/forestSpiderPack.txt"));
		spiderIdleLeft = spiderAtlas.findRegion("forest_spider-1");
		spiderIdleRight = new TextureRegion(spiderIdleLeft);
		spiderIdleRight.flip(true, false);

		TextureRegion[] spiderWalkLeftFrames = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			spiderWalkLeftFrames[i] = spiderAtlas.findRegion("forest_spider-" + (i+1));
		}
		
		spiderWalkLeftAnimation = new Animation(ENEMY_RUNNING_FRAME_DURATION, spiderWalkLeftFrames);

		TextureRegion[] spiderWalkRightFrames = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			spiderWalkRightFrames[i] = new TextureRegion(spiderWalkLeftFrames[i]);
			spiderWalkRightFrames[i].flip(true, false);
		}
		
		spiderWalkRightAnimation = new Animation(ENEMY_RUNNING_FRAME_DURATION, spiderWalkRightFrames);
	}
	
	private void loadBossTextures() {
		TextureAtlas bossAtlas = new TextureAtlas(Gdx.files.internal("images/textures/forestSpiderPack.txt"));
		bossIdleLeft = bossAtlas.findRegion("forest_spider-1");
		bossIdleRight = new TextureRegion(bossIdleLeft);
		bossIdleRight.flip(true, false);

		TextureRegion[] bossWalkLeftFrames = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			bossWalkLeftFrames[i] = bossAtlas.findRegion("forest_spider-" + (i+1));
		}
		
		bossWalkLeftAnimation = new Animation(BOSS_RUNNING_FRAME_DURATION, bossWalkLeftFrames);

		TextureRegion[] bossWalkRightFrames = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			bossWalkRightFrames[i] = new TextureRegion(bossWalkLeftFrames[i]);
			bossWalkRightFrames[i].flip(true, false);
		}
		
		bossWalkRightAnimation = new Animation(BOSS_RUNNING_FRAME_DURATION, bossWalkRightFrames);
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
		viewport.draw(bobFrame, Bob.getPosition().x, Bob.getPosition().y, Bob.WIDTH, Bob.HEIGHT);
		
		// special jump
		if ((int)(Bob.getPosition().x + 0.2) == 8 && (int)Bob.getPosition().y == 1) {
			Bob.setState(State.JUMPING);
			Bob.getVelocity().y = 7f; 
		}
	}
	
	private void drawWalkEnemy() {
		WalkEnemy enemy = world.getWalkEnemy();

		for (int i = 0; i < WalkEnemy.enemyPositions.size(); i++) {
						
			WalkEnemy.enemyBounds.get(i).setHeight((float)0.6);
			WalkEnemy.enemyBounds.get(i).setWidth((float)0.7);
			
			if (WalkEnemy.enemyPositions.get(i).x > Bob.getPosition().x + 0.5) {
				bugFrame = bugWalkLeftAnimation.getKeyFrame(WalkEnemy.enemyStateTimes.get(i), true);
				WalkEnemy.enemyFrames.set(i, bugFrame);
			} else if (WalkEnemy.enemyPositions.get(i).x < Bob.getPosition().x - 0.5) {
				bugFrame = bugWalkRightAnimation.getKeyFrame(WalkEnemy.enemyStateTimes.get(i), true);
				WalkEnemy.enemyFrames.set(i, bugFrame);
			}
			
			viewport.draw(WalkEnemy.enemyFrames.get(i), WalkEnemy.enemyPositions.get(i).x, WalkEnemy.enemyPositions.get(i).y, WalkEnemy.enemyWidths.get(i), WalkEnemy.enemyHeights.get(i));
					
		}
	}
	
	private void drawShootEnemy() {
		ShootEnemy shootEnemy = world.getShootEnemy();
		
		shootEnemyCount += 1;
		
		for (int i = 0; i < ShootEnemy.enemyPositions.size(); i++) {
			if (ShootEnemy.enemyStates.get(i).equals(State.IDLE)){ 
				spiderFrame = ShootEnemy.enemyDirections.get(i) == "left" ? spiderIdleLeft : spiderIdleRight;
			} else {
				if (ShootEnemy.enemyDirections.get(i) == "left") {
					spiderFrame = spiderWalkLeftAnimation.getKeyFrame(ShootEnemy.enemyStateTimes.get(i), true);
				} else {
					spiderFrame = spiderWalkRightAnimation.getKeyFrame(ShootEnemy.enemyStateTimes.get(i), true);
				}	
			} 
	
			viewport.draw(spiderFrame, ShootEnemy.enemyPositions.get(i).x, (float)(ShootEnemy.enemyPositions.get(i).y), ShootEnemy.WIDTH, ShootEnemy.HEIGHT);
			
			// New Attack
			if (shootEnemyCount > 100) {
				if (ShootEnemy.bulletPositions.size() > 0) {
					ShootEnemy.removeBullets();
				}
				newShootEnemyBullets = true;
				shootEnemyCount = 0;
				System.out.println("attack!");
			}
			
			
			drawShootEnemyAttack();
			
		}
	}
	
	public void drawShootEnemyAttack() {
		
		try {
			if (ShootEnemy.bulletPositions.size() > 0) {
		
				for (int i = 0; i < ShootEnemy.enemyPositions.size(); i++) {
					System.out.println(ShootEnemy.bulletPositions.get(i) + " " + i);
					System.out.println(ShootEnemy.enemyPositions.get(i) + " " + i);

					if (newShootEnemyBullets) {
						ShootEnemy.newBullets();
					}
					
					ShootEnemy.bulletGravity.set(i, ShootEnemy.bulletGravity.get(i) - (float) 0.002);

					if (ShootEnemy.enemyDirections.get(i) == "left") {
						ShootEnemy.bulletPositions.get(i).add(ShootEnemy.bulletAdds.get(i), ShootEnemy.bulletGravity.get(i));
					} else {
						ShootEnemy.bulletPositions.get(i).add(ShootEnemy.bulletAdds.get(i), ShootEnemy.bulletGravity.get(i));
					}
					
					
					// If bullet hits ground
					if (ShootEnemy.bulletPositions.get(i).y <= 1) {
						ShootEnemy.removeBullet(i);
					
					// If bullet hits Bob
					} else if (ShootEnemy.bulletPositions.get(i).epsilonEquals(Bob.getPosition(), (float) 0.3)) {
						HealthBar.bobHealth -= 1;
						
						if (HealthBar.bobHealth <= 0) {
							Bob.bobDied = true;
						}
					}
							
					if (ShootEnemy.bulletPositions.size() > 0) {
						viewport.draw(ShootEnemy.bulletTextures.get(i), (float)(ShootEnemy.bulletPositions.get(i).x),
								(float)(ShootEnemy.bulletPositions.get(i).y), 1, 1);
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
				// ignore glitch
		}
	}

	private void drawBoss() {
		Boss boss = world.getBoss();
		
		if (Boss.bossDied) {
			// Player Won
			viewport.draw(winMessage, cam.position.x - 5, cam.position.y + 2, 10, 3);
		} else {
			if (Boss.getState().equals(State.IDLE)){ 
				bossFrame = Boss.isFacingLeft() ? bossIdleLeft : bossIdleRight;
			} else {
				if (Boss.isFacingLeft()) {
					bossFrame = bossWalkLeftAnimation.getKeyFrame(boss.getStateTime(), true);
				} else if (!Boss.isFacingLeft()) {
					bossFrame = bossWalkRightAnimation.getKeyFrame(boss.getStateTime(), true);
				}	
			} 
			viewport.draw(bossFrame, Boss.getPosition().x, (float)(Boss.getPosition().y - 0.2), Boss.WIDTH, Boss.HEIGHT);
		}
		
		bossCount += 1;
		
		// New Attack
		if (bossCount > 100) {
			Boss.newBullet();
			bossCount = 0;
		}
		
		// Boss Attack!
		drawBossAttack();
	}
	private void drawBossAttack() {
		
		try {
			if (Boss.bulletPositions.size() > 0) {
		
				for (int i = 0; i < Boss.bulletPositions.size(); i++) {
					
					Boss.bulletGravity.set(i, Boss.bulletGravity.get(i) - (float) 0.002);
					
					if (Boss.isFacingLeft()) {
						Boss.bulletPositions.get(i).add(Boss.bulletAdds.get(i), Boss.bulletGravity.get(i));
					} else {
						Boss.bulletPositions.get(i).add(Boss.bulletAdds.get(i), Boss.bulletGravity.get(i));
					}
					
					// If bullet hits ground
					if (Boss.bulletPositions.get(i).y <= 1) {
						Boss.removeBullet(i);
					
					// If bullet hits Bob
					} else if (Boss.bulletPositions.get(i).epsilonEquals(Bob.getPosition(), (float) 0.3)) {
						HealthBar.bobHealth -= 1;
						Boss.removeBullet(i);
						
						if (HealthBar.bobHealth <= 0) {
							Bob.bobDied = true;
						}
					}
		
					if (!Boss.bossDied && Boss.bulletPositions.size() > 0) {
					viewport.draw(bossAttackTexture, (float)(Boss.bulletPositions.get(i).x),
							(float)(Boss.bulletPositions.get(i).y), 1, 1);
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
				// ignore glitch
		}
	}
	
	private void drawHealthBar() {
	
		viewport.draw(bobHealthBar, cam.position.x - 5, cam.position.y + 6, (float) HealthBar.bobHealth, (float) 0.6);
		
		if (Boss.bossBattle) {
			viewport.draw(bossHealthBar, cam.position.x - 5, cam.position.y + 5, (float) HealthBar.bossHealth, (float) 0.6);
		}
	}

	private void drawShip() {
		// Ship
		viewport.draw(shipTexture, Ship.getPosition().x, Ship.getPosition().y, Ship.width, Ship.height);
	
		// Ship Finish Line
		viewport.draw(shipStopTexture, 100, -6, 4, 40);
	}
	
	/** Bullet Motion **/
	private void updateBullets() {
		
		if (Bullet.firePressed) {
			
			for (int i = 0; i < Bullet.bulletPositions.size(); i++) {
			
				if (Bob.isFacingLeft()) {  // bob facing left
					bullet_pos = (new Vector2(Bob.getPosition().cpy().add( 
							(float)((Bob.WIDTH / 2 - BULLET_SIZE / 2) + 0.6),
							(float)((Bob.HEIGHT / 2 - BULLET_SIZE / 2) - 0.1)))); 
					bulletDirection.set(new Vector2((float)-0.6, 0)); 
				} else {
					bullet_pos = (new Vector2(Bob.getPosition().cpy().add( 
							(float)((Bob.WIDTH / 2 - BULLET_SIZE / 2) + 0.9),
							(float)((Bob.HEIGHT / 2 - BULLET_SIZE / 2) - 0.1))));
					bulletDirection.set(new Vector2((float)0.6, 0)); 
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
		new WalkEnemyController(world);
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
			// During Boss Battle
			} else if (Boss.bossBattle && Bullet.bulletPositions.get(i).x > Bob.getPosition().x + 11 || Bullet.bulletPositions.get(i).x > Level.getWidth()) {
				if (Bullet.bulletPositions.get(i).x > Level.getWidth()) {
					Bullet.removeBullet(i);
					continue;
				} else if (Bullet.bulletPositions.get(i).x < Level.getWidth() - 21) {
					Bullet.removeBullet(i);
					continue;
				}
			// Middle of level
			} else if (Bullet.bulletPositions.get(i).x > Bob.getPosition().x + 11 || Bullet.bulletPositions.get(i).x > Level.getWidth()) {
				Bullet.removeBullet(i);
				continue;
			} else if (Bullet.bulletPositions.get(i).x < Bob.getPosition().x - 11 || Bullet.bulletPositions.get(i).x <= 0) {
				Bullet.removeBullet(i);
				continue;
			}
			
			/** Bullet Logic **/
			// Bullet hit Boss - Remove bullet and decrease Boss Health
			if (!Boss.bossDied && Bullet.bulletPositions.get(i).epsilonEquals((float)(Boss.getPosition().x + 1.2), (float)(Boss.getPosition().y + 0.5), (float) 1)) {
				Bullet.removeBullet(i);
				// decrease boss health
				if (HealthBar.bossHealth > 0) {
					HealthBar.bossHealth -= 0.5;
				} else if (HealthBar.bossHealth <= 0){
					Boss.bossDied = true;
				}
			}
			
			// Bullet Killed Enemy - Remove enemy and bullet
			try {
				for (int e = 0; e < ShootEnemy.enemyPositions.size(); e++) {
					if (Bullet.bulletPositions.size() == 0) {
						continue;
					} else if (Bullet.bulletPositions.get(i).epsilonEquals(ShootEnemy.enemyPositions.get(e), (float) 0.4)) {
						// Increment hit
						ShootEnemy.enemyHits.set(e, ShootEnemy.enemyHits.get(e) + 1);
						if (ShootEnemy.enemyHits.get(e) == 5) {
							ShootEnemy.removeEnemy(e);
						}
						Bullet.removeBullet(i);
						continue;
					} 
				}
				for (int e = 0; e < WalkEnemy.enemyPositions.size(); e++) {
					if (Bullet.bulletPositions.size() == 0) {
						continue;
					} else if (Bullet.bulletPositions.get(i).epsilonEquals(WalkEnemy.enemyPositions.get(e), (float) 0.4)) {
						// Increment hit
						WalkEnemy.enemyHits.set(e, WalkEnemy.enemyHits.get(e) + 1);
						if (WalkEnemy.enemyHits.get(e) == 3) {
							WalkEnemy.removeEnemy(e);
						}
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
			for (int i = 0; i < WalkEnemy.enemyPositions.size(); i++) {
				Rectangle rect = WalkEnemy.enemyBounds.get(i);
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
			Rectangle bobRect = bob.getBounds();
			debugRenderer.setColor(new Color(0, 1, 0, 1));
			debugRenderer.rect(bobRect.x, bobRect.y, bobRect.width, bobRect.height);
			
			// render Boss
			Boss boss = world.getBoss();
			Rectangle bossRect = boss.getBounds();
			debugRenderer.setColor(new Color(0, 1, 1, 1));
			debugRenderer.rect(bossRect.x, bossRect.y, bossRect.width, bossRect.height);
			debugRenderer.end();
			
		} catch (ArrayIndexOutOfBoundsException a) {
			System.out.println("oh well");
		}
	}
}

