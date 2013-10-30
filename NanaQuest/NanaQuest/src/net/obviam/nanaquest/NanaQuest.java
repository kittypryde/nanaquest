package net.obviam.nanaquest;

import net.obviam.nanaquest.screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.obviam.nanaquest.screens.Splash;

public class NanaQuest extends Game {
	Preferences saved = Gdx.app.getPreferences("My Preferences");
	public SpriteBatch batch; 
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new Splash(this, saved));
	}

}
