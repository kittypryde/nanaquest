package net.obviam.nanaquest.utils;

import net.obviam.nanaquest.screens.GameScreen;
import net.obviam.nanaquest.screens.MainMenu;

import com.badlogic.gdx.Input.TextInputListener;

public class MyTextInputListener implements TextInputListener {
		
   @Override
   public void input (String text) {
       MainMenu.playerName = text;
   }

   @Override
   public void canceled () {
   }
}