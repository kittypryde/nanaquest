package net.obviam.nanaquest;

import net.obviam.nanaquest.screens.GameScreen;
import net.obviam.nanaquest.screens.Splash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NanaQuest extends Game {

	public SpriteBatch batch;
	
	@Override
	public void create() {
		
//		batch = new SpriteBatch();
//		setScreen(new Splash(this));
		setScreen(new GameScreen());
		
	}

}
