package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.utils.GamePreferences;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LoadPlayerScreen implements Screen, InputProcessor {
	
	private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
        
    private int width = 960;
    private int height = 640;
    private float firstCellX = (float)(width/2) - 150;
    private float firstCellY = (float)(height/2) + 60;
    
    private SpriteBatch batch;
    
    private boolean mouseOver = false;
    private boolean backButtonHover = false;
    private boolean backButtonDown = false;
     
    //Appearance
    private Table table;
    private ImageButton usernameButton;
    private BitmapFont headerFont;
    private BitmapFont contentFont;
 
	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
		Gdx.input.setInputProcessor(this);
        
        headerFont = new BitmapFont();
	    headerFont.setColor(0, 0, 0, 1);   // Black
	    headerFont.setScale((float)1.1, (float)1.1);
	    
	    contentFont = new BitmapFont();
	    contentFont.setColor(1, 0.3f, 0, 1);
	    
	    batch = new SpriteBatch();
	    batch.begin();           
	    batch.draw(skin.getRegion("loadUserNameBG.fw"), 0, 0, width, height);
	    
	    // Back Button
	    if (!backButtonHover) {  // Not clicked
	    	batch.draw(skin.getRegion("back_button.fw"), 350, 50, 250, 80);
	    } else if (backButtonHover) {  // Clicked
	    	batch.draw(skin.getRegion("back_button_down.fw"), 350, 50, 250, 80);
	    }
	    
	    
	    if (mouseOver) {
	    	batch.draw(skin.getRegion("username_button-down.fw"), (float)(width/2) - 180, (float)(height/2) - 30);
	    }
	    
	    headerFont.draw(batch, "Name:", (float)(width/2) - 150, (float)(height/2) + 100);
	    headerFont.draw(batch, "Level:", (float)(width/2) + 100, (float)(height/2) + 100);
	    
		contentFont.draw(batch, GamePreferences.prefs.getString("Player Name 1"), firstCellX, firstCellY);
		contentFont.draw(batch, GamePreferences.prefs.getString("Player Name 2"), firstCellX, firstCellY - 40); 
		contentFont.draw(batch, GamePreferences.prefs.getString("Player Name 3"), firstCellX, firstCellY - 80); 
		contentFont.draw(batch, GamePreferences.prefs.getString("Player Name 4"), firstCellX, firstCellY - 120); 
		contentFont.draw(batch, GamePreferences.prefs.getString("Player Name 5"), firstCellX, firstCellY - 160); 
		
	    batch.end();
	    batch.dispose();
	    
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
		 atlas = new TextureAtlas(Gdx.files.internal("images/textures/loadScreenPack.txt"));
         skin = new Skin();
         skin.addRegions(atlas);
		  
		 headerFont = new BitmapFont();
	     headerFont.setColor(0.5f,0.4f,0,1);   // Brown
	      
	     batch = new SpriteBatch();
	     
        
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT && mouseOver) {
			// set screen with preferences GAME PLAY
		} else if (button == Input.Buttons.LEFT && backButtonHover) {
			backButtonDown = true;
		} else {
			backButtonDown = false;
		}
			
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT && backButtonHover) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(new NanaQuest()));
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
			if (screenX > (float)(width/2) - 180
			 && screenX < (float)(width/2) + 230
			 && screenY < (float)(height/2) + 30
			 && screenY > (float)(height/2) - 10) {
				mouseOver = true;
			} else {
				mouseOver = false;
			}								// 350, 50, 250, 80
			
			if (screenX > 350
			 && screenX < 600
			 && screenY > 500
			 && screenY < 600) {
				backButtonHover = true;
				System.out.println("back");
			} else {
				backButtonHover = false;
			}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
     
     
}
