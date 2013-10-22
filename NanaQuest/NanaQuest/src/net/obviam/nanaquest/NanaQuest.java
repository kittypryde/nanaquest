package net.obviam.nanaquest;

import net.obviam.nanaquest.screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.obviam.nanaquest.screens.Splash;

public class NanaQuest extends Game {

	public SpriteBatch batch; 
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new Splash(this));
	}

}
