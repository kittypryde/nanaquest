package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.controller.BobController;
import net.obviam.nanaquest.controller.EnemyController;
import net.obviam.nanaquest.controller.ShipController;
import net.obviam.nanaquest.model.Bob;
import net.obviam.nanaquest.model.Bullet;
import net.obviam.nanaquest.model.Ship;
import net.obviam.nanaquest.model.Ship.State;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.view.WorldRenderer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen, InputProcessor {

	private World 				world;
	private WorldRenderer 		renderer;
	private BobController		bobController;
	private EnemyController 	enemyController;
	private ShipController 		shipController;
	
	@Override
	public void show() {

		world = new World();
		renderer = new WorldRenderer(world, false);
		bobController = new BobController(world);
		enemyController = new EnemyController(world);
		shipController = new ShipController(world);
		Gdx.input.setInputProcessor(this);
	}
	

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		bobController.update(delta);
		enemyController.update(delta);
		shipController.update(delta);
		renderer.render();
		
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
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	// * InputProcessor methods ***************************//

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.SPACE)
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen());
		if (Ship.getState() == State.FLYING) {
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
		if (Ship.getState() == State.FLYING) {
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
		if (y > renderer.cam.viewportHeight / 2) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen());
		}
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
		
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
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

