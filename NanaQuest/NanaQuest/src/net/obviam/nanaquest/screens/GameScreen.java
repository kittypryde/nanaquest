package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.controller.BobController;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.view.WorldRenderer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * SOURCES:
 * 
 * PAUSE: http://stackoverflow.com/questions/11466496/libgdx-game-how-to-display-pause-screen-when-user-click-on-pause-icon
 */

public class GameScreen implements Screen, InputProcessor {

	private World 			world;
	private WorldRenderer 	renderer;
	private BobController	controller;
//	final NanaQuest game;
	
	private int width, height;
	final Preferences pref;
	
	public static final int RUNNING = 0;
	public static final int PAUSED = 0;
	private int gamestatus;
	
	public GameScreen(final Preferences prefer){
		pref = prefer;
	}
	
	@Override
	public void show() {

		world = new World();
		renderer = new WorldRenderer(world, false);
		controller = new BobController(world);
		Gdx.input.setInputProcessor(this);
	}
	

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(isPaused()){
			//If pause button is pressed
			pauseGame();
		}		
		
		if (gamestatus == PAUSED) {
			//draw pause screen
		}
			
		
		controller.update(delta);
		renderer.render();
	}
	
	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
		this.width = width;
		this.height = height;
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
		
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	// * InputProcessor methods ***************************//

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.LEFT)
			controller.leftPressed();
		if (keycode == Keys.RIGHT)
			controller.rightPressed();
		if (keycode == Keys.Z)
			controller.jumpPressed();
		if (keycode == Keys.X)
			renderer.firePressed = true;
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT)
			controller.leftReleased();
		if (keycode == Keys.RIGHT)
			controller.rightReleased();
		if (keycode == Keys.Z)
			controller.jumpReleased();
		if (keycode == Keys.X)
			renderer.firePressed = false;
		if (keycode == Keys.D)
			renderer.setDebug(!renderer.isDebug());
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
	
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		if (x >= 1 && x <= 100 && y >= 500 && y <= 600) {
			controller.leftPressed();
		}
		if (x >= 100 && x <= 250 && y >= 600 && y <= 700) {
			controller.rightPressed();
		}
		if (x >= 1150 && x <= 1270 && y >= 460 && y <= 600) {
			controller.jumpPressed();
		}
		if (x >= 1000 && x <= 1150 && y >= 600 && y <= 700) {
			renderer.firePressed = true;
		}
		
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		if (x >= 1 && x <= 100 && y >= 500 && y <= 600) {
			controller.leftReleased();
		}
		if (x >= 100 && x <= 250 && y >= 600 && y <= 700) {
			controller.rightReleased();
		}
		if (x >= 1150 && x <= 1270 && y >= 460 && y <= 600) {
			controller.jumpReleased();
		}
		if (x >= 1000 && x <= 1150 && y >= 600 && y <= 700) {
			renderer.firePressed = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		
		return false;
	}

	public boolean touchMoved(int x, int y) {
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
	
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
	
		return false;
	}
	
	public boolean isPaused(){
		//No arguments
		//Returns boolean that says whether or not the pause button has been pressed
		return true;
	}
	
	public void pauseGame(){
		//Sets current game status to pause
		gamestatus = PAUSED;
	}
	

}
