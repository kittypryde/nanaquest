package net.obviam.nanaquest.utils;

import java.util.LinkedList;
import java.util.List;

import net.obviam.nanaquest.screens.LoadPlayerScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

public class GamePreferences {
	
	public static int player;
	public static Preferences prefs = Gdx.app.getPreferences("GamePreferences");
	
	public GamePreferences() {

	}
	
	public static void addPlayer (String playerName) {
		if (prefs.getString("Player Name 1").isEmpty()) {
			prefs.putString("Player Name 1", playerName);
		} else if (prefs.getString("Player Name 2").isEmpty()) {
			prefs.putString("Player Name 2", playerName);
		} else if (prefs.getString("Player Name 3").isEmpty()) {
			prefs.putString("Player Name 3", playerName);
		} else if (prefs.getString("Player Name 4").isEmpty()) {
			prefs.putString("Player Name 4", playerName);
		} else if (prefs.getString("Player Name 5").isEmpty()) {
			prefs.putString("Player Name 5", playerName);
		} else {
			System.out.println("what?");
			prefs.remove("Player Name 5");
			prefs.putString("Player Name 5", playerName);
		}
		prefs.flush();
	}
	

}
