package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.controller.BobController;
import net.obviam.nanaquest.controller.BossController;
import net.obviam.nanaquest.controller.ShootEnemyController;
import net.obviam.nanaquest.controller.WalkEnemyController;
import net.obviam.nanaquest.controller.ShipController;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Boss;
import net.obviam.nanaquest.model.Bullet;
import net.obviam.nanaquest.model.WalkEnemy;
import net.obviam.nanaquest.model.Ship;
import net.obviam.nanaquest.model.Ship.State;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.utils.GamePreferences;
import net.obviam.nanaquest.utils.MyTextInputListener;
import net.obviam.nanaquest.view.WorldRenderer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameScreen implements Screen, InputProcessor {

	private static boolean pause = false;
	static boolean newGame = true;
	public static int prefNumber;
	
	public NanaQuest 				game;
	public PauseScreen				pauseScreen;
	private World 					world;
	private WorldRenderer 			renderer;
	private BobController			bobController;
	private WalkEnemyController 	walkEnemyController;
	private ShootEnemyController 	shootEnemyController;
	private BossController			bossController;
	private ShipController 			shipController;
	
	public GameScreen(NanaQuest game, boolean newGame) {
		this.game = game;
		GameScreen.newGame = newGame;
		pauseScreen = new PauseScreen(game, this);
	}
	
	@Override
	public void show() {
		
		if (newGame) { 
			Bob.bobDied = false;
			Boss.bossBattle = false;
			ShipController.shipFlight = false;
			Bob.setPosition(Bob.getPosition());
			
			// Remove other enemies
			if (WalkEnemy.enemyPositions != null) {
				WalkEnemy.removeAll();
			}

		
			// Start new Game
			world = new World(game);
			renderer = new WorldRenderer(world, false);
			bobController = new BobController(world);
			walkEnemyController = new WalkEnemyController(world);
			shootEnemyController = new ShootEnemyController(world);
			shipController = new ShipController(world);
			bossController = new BossController(world);
			
		}
		Gdx.input.setInputProcessor(this);
	}
	

	@Override
	public void render(float delta) {

		
		if (newGame) {
			if (!pause) {
				Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				bobController.update(delta);
				walkEnemyController.update(delta);
				shootEnemyController.update(delta);
				shipController.update(delta);
				renderer.render();	
			} else {
				// draw pause screen
			}
		}
		
		if (Boss.bossBattle) {
			bossController.update(delta);
		}
		
//		System.out.println("Player 1 " + GamePreferences.prefs.getString("Player Name 1"));
//		System.out.println("Player 2 " + GamePreferences.prefs.getString("Player Name 2"));
//		System.out.println("Player 3 " + GamePreferences.prefs.getString("Player Name 3"));
//		System.out.println("Player 4 " + GamePreferences.prefs.getString("Player Name 4"));
//		System.out.println("Player 5 " + GamePreferences.prefs.getString("Player Name 5"));

		
	}
	
	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);		
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		renderer.dispose();
		Gdx.input.setInputProcessor(null);
	}

	// * InputProcessor methods ***************************//

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.SPACE)
      //      ((Game) Gdx.app.getApplicationListener()).setScreen(pauseScreen);
			pause = true;
		if (ShipController.shipFlight) {
			if (keycode == Keys.LEFT) 
				shipController.upPressed = true;
			if (keycode == Keys.RIGHT)
				shipController.downPressed = true;
		} else {
			if (keycode == Keys.LEFT)
				bobController.leftPressed();
			if (keycode == Keys.RIGHT)
				bobController.rightPressed();
			if (keycode == Keys.Z)
				bobController.jumpPressed();
			if (keycode == Keys.X){
				Bullet.firePressed = true;
				Bullet.newBullet();
			}
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.SPACE)
		      //      ((Game) Gdx.app.getApplicationListener()).setScreen(pauseScreen);
				pause = false;
		if (ShipController.shipFlight) {
			if (keycode == Keys.LEFT) 
				shipController.upPressed = false;
			if (keycode == Keys.RIGHT)
				shipController.downPressed = false;
		} else {
			if (keycode == Keys.LEFT)
				bobController.leftReleased();
			if (keycode == Keys.RIGHT)
				bobController.rightReleased();
			if (keycode == Keys.Z)
				bobController.jumpReleased();
			if (keycode == Keys.X)
				Bullet.firePressed = false;
			if (keycode == Keys.D)
				renderer.setDebug(!renderer.isDebug());
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		if (y < 300) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(game, this));
		}
		if (ShipController.shipFlight) {
			if (x >= 1 && x <= 100 && y >= 500 && y <= 600) {
				shipController.upPressed = true;
			}
			if (x >= 100 && x <= 250 && y >= 600 && y <= 700) {
				shipController.downPressed = true;
			}
		} else {
			if (x >= 1 && x <= 100 && y >= 500 && y <= 600) {
				bobController.leftPressed();
			}
			if (x >= 100 && x <= 250 && y >= 600 && y <= 700) {
				bobController.rightPressed();
			}
			if (x >= 1150 && x <= 1270 && y >= 460 && y <= 600) {
				bobController.jumpPressed();
			}
			if (x >= 1000 && x <= 1150 && y >= 600 && y <= 700) {
				Bullet.firePressed = true;
				Bullet.newBullet();
			}
		}
		
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		if (ShipController.shipFlight) {
			if (x >= 1 && x <= 100 && y >= 500 && y <= 600) {
				shipController.upPressed = false;
			}
			if (x >= 100 && x <= 250 && y >= 600 && y <= 700) {
				shipController.downPressed = false;
			}
		} else {
			if (x >= 1 && x <= 100 && y >= 500 && y <= 600) {
				bobController.leftReleased();
			}
			if (x >= 100 && x <= 250 && y >= 600 && y <= 700) {
				bobController.rightReleased();
			}
			if (x >= 1150 && x <= 1270 && y >= 460 && y <= 600) {
				bobController.jumpReleased();
			}
			if (x >= 1000 && x <= 1150 && y >= 600 && y <= 700) {
				Bullet.firePressed = false;
			}
		}
		return true;
	}
	

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean touchMoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	

}

