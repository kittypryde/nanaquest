package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.controller.BobController;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.view.WorldRenderer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import android.util.Log;

public class GameScreen implements Screen, InputProcessor {

	private SpriteBatch		dPad;
	private World 			world;
	private WorldRenderer 	renderer;
	private BobController	controller;
	
	
	private int width, height;
	
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
