package net.obviam.nanaquest.view;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.model.Block;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Bob.State;
import net.obviam.nanaquest.model.Level;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
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
	private static final float RUNNING_FRAME_DURATION = 0.06f;
	
	/** Bullet stuff **/
	private static final int BULLET_SIZE = 1;
	private static final int MOVEMENT_SPEED = 1;
	private Vector2 bullet_pos;
	private Vector2 bulletDirection;
	private Pixmap pixmap;
	public static boolean firePressed;
	
	/** Other Classes **/
	private World world;
	private Level level;
	
	/** Camera **/
	public OrthographicCamera cam;

	/** for debug rendering **/
	ShapeRenderer debugRenderer = new ShapeRenderer();

	/** Textures **/
	private Texture background;
	private Texture blockTexture;
	private Texture leftBulletTexture;
	private Texture rightBulletTexture;
	private TextureRegion bobIdleLeft;
	private TextureRegion bobIdleRight;
	private TextureRegion bobFrame;
	private TextureRegion bobJumpLeft;
	private TextureRegion bobFallLeft;
	private TextureRegion bobJumpRight;
	private TextureRegion bobFallRight;
	
	/** Button Textures **/
	public Texture rightTexture;
	public Texture leftTexture;
	public Texture jumpTexture;
	public Texture fireTexture;
	
	/** Button Rectangles **/
	public static Rectangle rightButton;
	public static Rectangle leftButton;
	public static Rectangle jumpButton;
	public static Rectangle fireButton;
	
	/** Animations **/
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	
	/** SpriteBatches **/
	private SpriteBatch viewport;
	
	private boolean debug = false;
	private int width;
	private int height;
	private float ppuX;	// pixels per unit on the X axis
	private float ppuY;	// pixels per unit on the Y axis
	
	public void setSize (int w, int h) {
		this.width = w;
		this.height = h;
		ppuX = (float)width / CAMERA_WIDTH;
		ppuY = (float)height / CAMERA_HEIGHT;
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
		bulletDirection = new Vector2(0, 0);
		bullet_pos = null;
		loadTextures();
		}
	
	private void loadTextures() {
		loadDPadTextures();
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("images/textures/textures.pack"));
		bobIdleLeft = atlas.findRegion("bob-01");
		bobIdleRight = new TextureRegion(bobIdleLeft);
		bobIdleRight.flip(true, false);
		background = new Texture (Gdx.files.internal("images/forest_background_nanaquest.png"));
		blockTexture = new Texture (Gdx.files.internal("images/block_grey_round_forest.png"));
		leftBulletTexture = new Texture (Gdx.files.internal("images/left_bullet_sample.fw.png"));
		rightBulletTexture = new Texture (Gdx.files.internal("images/right_bullet_sample.fw.png"));
		TextureRegion[] walkLeftFrames = new TextureRegion[5];
		for (int i = 0; i < 5; i++) {
			walkLeftFrames[i] = atlas.findRegion("bob-0" + (i + 2));
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);

		TextureRegion[] walkRightFrames = new TextureRegion[5];

		for (int i = 0; i < 5; i++) {
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);
		bobJumpLeft = atlas.findRegion("bob-up");
		bobJumpRight = new TextureRegion(bobJumpLeft);
		bobJumpRight.flip(true, false);
		bobFallLeft = atlas.findRegion("bob-down");
		bobFallRight = new TextureRegion(bobFallLeft);
		bobFallRight.flip(true, false);
	}
	
	
	public void render(){
		updateBullets();
		Bob bob = world.getBob();
		
		viewport = new SpriteBatch();
		viewport.setProjectionMatrix(cam.combined);
		viewport.begin();
		
		// Background
		viewport.draw(background, 0, 0, 80, 20);
		
		drawBlocks(); 		// Blocks
		drawBob(); 			// Bob
		drawBullets(); 		// Bullets
		drawButtons(); 		// Tablet Buttons
		
		viewport.end();
		if (debug)
			drawDebug();
		
		/** Lock Camera if Actor is near Edge **/
		if (bob.getPosition().x <= 10) { // far left
			this.cam.position.set(10, bob.getPosition().y + 1, 0);
		} else if (bob.getPosition().x >= level.getWidth() - 10) { // far right
			this.cam.position.set(level.getWidth() - 10, bob.getPosition().y + 1, 0);	
		} else { // in-between
			this.cam.position.set(bob.getPosition().x, bob.getPosition().y + 1, 0);  // change camera position
		}
		
		cam.update();
		viewport.dispose();
	}
	
	

	public void loadDPadTextures() {
		rightTexture = new Texture(Gdx.files.internal("images/right_arrow_nq.png"));
		leftTexture = new Texture(Gdx.files.internal("images/left_arrow_nq.png"));
		jumpTexture = new Texture(Gdx.files.internal("images/jump_button_nq.png.png"));
		fireTexture = new Texture(Gdx.files.internal("images/fire_button_nq.png.png"));
	}
	
	public void drawButtons() {
		// Buttons
		viewport.draw(rightTexture, cam.position.x - 8, cam.position.y - 6, 2, 2);	
		viewport.draw(leftTexture, cam.position.x - 10, cam.position.y - 4, 2, 2);	
		viewport.draw(jumpTexture, cam.position.x + 8, cam.position.y - 4, 2, 2);	
		viewport.draw(fireTexture, cam.position.x + 6, cam.position.y - 6, 2, 2);
	}

	private void drawBlocks() {
		for (Block block : world.getDrawableBlocks(Level.getWidth(), Level.getHeight())) {
			viewport.draw(blockTexture, block.getPosition().x, block.getPosition().y, Block.SIZE, Block.SIZE);
		}
	}

	private void drawBob() {
		Bob bob = world.getBob();
		bobFrame = bob.isFacingLeft() ? bobIdleLeft : bobIdleRight;
		if(bob.getState().equals(State.WALKING)) {
			bobFrame = bob.isFacingLeft() ? walkLeftAnimation.getKeyFrame(bob.getStateTime(), true) : walkRightAnimation.getKeyFrame(bob.getStateTime(), true);
		} else if (bob.getState().equals(State.JUMPING)) {
			if (bob.getVelocity().y > 0) {
				bobFrame = bob.isFacingLeft() ? bobJumpLeft : bobJumpRight;
			} else {
				bobFrame = bob.isFacingLeft() ? bobFallLeft : bobFallRight;
			}
		}

		viewport.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, Bob.SIZE, Bob.SIZE);
		
	}
	 
	/** FIX THIS FUCKING SHIT YO FUCK FIX THIS SHIT **/
	private void updateBullets() {
		float delta = Gdx.graphics.getDeltaTime() * MOVEMENT_SPEED;
		if (firePressed) {
			if (Bob.isFacingLeft()) {
				bullet_pos = new Vector2(Bob.getPosition().cpy().add( 
						(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) + 0.6),
						(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) - 0.1))); 
				bulletDirection.set((float)-0.1, 0); 
			} else {
				bullet_pos = new Vector2(Bob.getPosition().cpy().add( 
						(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) + 0.9),
						(float)((Bob.SIZE / 2 - BULLET_SIZE / 2) - 0.1)));
				bulletDirection.set((float)0.1, 0); 
			}		
		}
		
		if (bullet_pos != null) { 
			bullet_pos.add(bulletDirection);
			if (bullet_pos.x < 0 || bullet_pos.x > level.getWidth() || bullet_pos.y < 0 || bullet_pos.y > level.getHeight()) {
				bullet_pos = null;
			}
		}
	}
	
	private void drawBullets() {
		/** Draw Bullets **/
		if (bullet_pos != null) {
			bullet_pos.add(bulletDirection);
			if (Bob.isFacingLeft()) {
				viewport.draw(leftBulletTexture, (float)(bullet_pos.x - 1.5), (float)(bullet_pos.y - 0.1), 1, (float) 0.4);
			} else {
				viewport.draw(rightBulletTexture, (float)(bullet_pos.x - 1), (float)(bullet_pos.y - 0.1), 1, (float) 0.4);
			}
		}
	}

	private void drawDebug() {
		// render blocks
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Rectangle);
		for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
			Rectangle rect = block.getBounds();
			debugRenderer.setColor(new Color(1, 0, 0, 1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		// render Bob
		Bob bob = world.getBob();
		Rectangle rect = bob.getBounds();
		debugRenderer.setColor(new Color(0, 1, 0, 1));
		debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		debugRenderer.end();
	}
	
	private void drawCollisionBlocks() {
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.FilledRectangle);
		debugRenderer.setColor(new Color(1, 1, 1, 1));
		for (Rectangle rect : world.getCollisionRects()) {
			debugRenderer.filledRect(rect.x, rect.y, rect.width, rect.height);
		}
		debugRenderer.end();
		
	}
}
