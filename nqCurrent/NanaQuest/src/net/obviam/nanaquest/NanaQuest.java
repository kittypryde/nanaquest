package net.obviam.nanaquest;

import net.obviam.nanaquest.screens.GameScreen;

import com.badlogic.gdx.Game;

public class NanaQuest extends Game {

	@Override
	public void create() {
		
		setScreen(new GameScreen());
	}

}