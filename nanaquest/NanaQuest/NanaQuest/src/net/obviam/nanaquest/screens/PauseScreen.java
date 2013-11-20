package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.model.World;
import net.obviam.nanaquest.utils.GamePreferences;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseScreen implements Screen {
	
	
    private NanaQuest game;
    private GameScreen gameScreen;

	private TextureAtlas atlas;
    private Skin skin;
    
    public static Stage stage;
    public static Table table;
    private ImageButton resumeButton;
    private ImageButton restartButton;
    private ImageButton menuButton;
    private ImageButton exitButton;
    
    public PauseScreen(NanaQuest game, GameScreen gameScreen) {
    	this.gameScreen = gameScreen;
    	this.game = game;
    }

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
       
		//Draws debug lines
		Table.drawDebug(stage);
		
        //Update everything in the stage
		stage.act(delta);
		
		
        //Everything becomes visible
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
        stage = new Stage(960, 640, true);
        Gdx.input.setInputProcessor(stage);
		
        atlas = new TextureAtlas(Gdx.files.internal("images/textures/pausePack.txt"));
        skin = new Skin();
        skin.addRegions(atlas);

        // Create table
        table = new Table(skin);
        table.setBounds(0, 0, 960, 640);
        table.setBackground(skin.getDrawable("pause_screen.fw"));
		
		// Menu Button
        ImageButtonStyle menuB = new ImageButtonStyle();
        menuB.up = skin.getDrawable("main_menu_button.fw");
        menuB.down = skin.getDrawable("main_menu_button_down.fw");
         
        // Resume Button
        ImageButtonStyle resumeB = new ImageButtonStyle();
        resumeB.up = skin.getDrawable("resume_button.fw");
        resumeB.down = skin.getDrawable("resume_button_down.fw");
        
        // Restart Button
        ImageButtonStyle restartB = new ImageButtonStyle();
        restartB.up = skin.getDrawable("restart_level_button.fw");
        restartB.down = skin.getDrawable("restart_level_button_down.fw");
        
        // Exit Button
        ImageButtonStyle exitB = new ImageButtonStyle();
        exitB.up = skin.getDrawable("exit_button.fw");
        exitB.down = skin.getDrawable("exit_button_down.fw");
       
        menuButton = new ImageButton(menuB);
        menuButton.pad(10);
        menuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(game));
            }
        }
        );
        
        resumeButton = new ImageButton(resumeB);
        resumeButton.pad(10);
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
            	GameScreen.newGame = false;
                ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
            }
	    }
	    );
        
        restartButton = new ImageButton(restartB);
        restartButton.pad(10);
        restartButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(game, true));
                }
        }
        );
        
        exitButton = new ImageButton(exitB);
        exitButton.pad(10);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                    Gdx.app.exit();
            }
	    }
	    );
        
        // Add buttons
        table.add(menuButton);
        table.add(resumeButton);
        table.row();
        table.add(restartButton);
        table.add(exitButton);
//		table.debug();
        // Add table to stage
        stage.addActor(table);
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

}
