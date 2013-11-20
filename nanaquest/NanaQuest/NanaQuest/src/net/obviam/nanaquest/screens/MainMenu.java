package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.model.WalkEnemy;
import net.obviam.nanaquest.utils.GamePreferences;
import net.obviam.nanaquest.utils.MyTextInputListener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu implements Screen {
        
        private Stage stage;
        private TextureAtlas atlas;
        private Skin skin;
        
    	MyTextInputListener listener = new MyTextInputListener();
        public static String playerName;
    	public static Preferences prefs = Gdx.app.getPreferences("settings.prefs");
        
        private boolean newGamePressed = false;
        
        //Appearance
        private Table table;
        private ImageButton newGameButton;
        private ImageButton loadButton;
        private ImageButton settingsButton;
        private ImageButton exitButton;
               
        private WalkEnemy		enemy;
        final NanaQuest 	game;
        
        public MainMenu(final NanaQuest game){
               this.game = game;
        }
        @Override
        public void render(float delta) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                        //Clear everything with black background
                Table.drawDebug(stage);
                        //Draws debug lines
                stage.act(delta);
                        //Update everything in the stage
                stage.draw();
                        //Everything becomes visible 
                
                if (newGamePressed && playerName != null) {
                	GamePreferences.addPlayer(playerName);
            		((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(new NanaQuest(), true));
        		}
        }

        @Override
        public void resize(int width, int height) {
                
                
        }

        @Override
        public void show() {
                stage = new Stage(960, 640, true);
                
                Gdx.input.setInputProcessor(stage);                
                
                atlas = new TextureAtlas(Gdx.files.internal("images/textures/menuPack.txt"));
                skin = new Skin();
                skin.addRegions(atlas);
                
                // Create table
                table = new Table(skin);
                table.setBounds(0, 0, 960, 640);
                table.setBackground(skin.getDrawable("MainMenuBG.fw"));
                
                // New Game Button
                ImageButtonStyle newGameB = new ImageButtonStyle();
                newGameB.up = skin.getDrawable("new_game_button_up.fw");
                newGameB.down = skin.getDrawable("new_game_button_down.fw");
                
                // Load Button
                ImageButtonStyle loadB = new ImageButtonStyle();
                loadB.up = skin.getDrawable("load_game_button_up.fw");
                loadB.down = skin.getDrawable("load_game_button_down.fw");
                
                // Settings Button
                ImageButtonStyle settingsB = new ImageButtonStyle();
                settingsB.up = skin.getDrawable("settings_button.fw");
                settingsB.down = skin.getDrawable("settings_button_down.fw");
                
                // Exit Button
                ImageButtonStyle exitB = new ImageButtonStyle();
                exitB.up = skin.getDrawable("exit_button.fw");
                exitB.down = skin.getDrawable("exit_button_down.fw");
                
                newGameButton = new ImageButton(newGameB);
                newGameButton.pad(10);
                newGameButton.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                        	MyTextInputListener listener = new MyTextInputListener();
                			Gdx.input.getTextInput(listener, "Player Name", "Name");
                			playerName = null;
                        	newGamePressed = true;
                        }	
                }
                );
                
                loadButton = new ImageButton(loadB);
                loadButton.pad(10);
                loadButton.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                        	stage.dispose();
                    		((Game) Gdx.app.getApplicationListener()).setScreen(new LoadPlayerScreen());
                        }
                }
                );
                
                settingsButton = new ImageButton(settingsB);
                settingsButton.pad(10);
                settingsButton.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                        	System.out.println("SETTINGS!");
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
                
//                if (!Enemy.enemyPositions.isEmpty()) {
//                	Enemy.removeAll();
//                }
                
                // Add buttons
                table.add(newGameButton);
                table.add(loadButton);
                table.row();
                table.add(settingsButton);
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
