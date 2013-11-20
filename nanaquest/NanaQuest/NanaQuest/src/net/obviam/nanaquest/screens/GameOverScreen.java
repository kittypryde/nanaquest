package net.obviam.nanaquest.screens;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.model.WalkEnemy;
import net.obviam.nanaquest.utils.GamePreferences;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

public class GameOverScreen implements Screen {
        
        private Stage stage;
        private TextureAtlas atlas;
        private Skin skin;
        
        //Appearance
        private Table table;
        private ImageButton restartButton;
        private ImageButton mainMenuButton;
        private ImageButton exitButton;
        
        private WalkEnemy enemy;
        final NanaQuest 	game;
        
        public GameOverScreen(final NanaQuest game){
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
        }

        @Override
        public void resize(int width, int height) {
                
                
        }

        @Override
        public void show() {
                stage = new Stage(960, 640, true);
                
                Gdx.input.setInputProcessor(stage);
                
                
                atlas = new TextureAtlas(Gdx.files.internal("images/textures/gameOverPack.txt"));
                skin = new Skin();
                skin.addRegions(atlas);
                
                // Create table
                table = new Table(skin);
                table.setBounds(0, 0, 960, 640);
                table.setBackground(skin.getDrawable("GameOverBG.fw"));
                
                // Restart Button
                ImageButtonStyle restartB = new ImageButtonStyle();
                restartB.up = skin.getDrawable("restart_level_button.fw");
                restartB.down = skin.getDrawable("restart_level_button_down.fw");
                
                // Main Menu Button
                ImageButtonStyle mainMenuB = new ImageButtonStyle();
                mainMenuB.up = skin.getDrawable("main_menu_button.fw");
                mainMenuB.down = skin.getDrawable("main_menu_button_down.fw");
                
                // Exit Button
                ImageButtonStyle exitB = new ImageButtonStyle();
                exitB.up = skin.getDrawable("exit_button.fw");
                exitB.down = skin.getDrawable("exit_button_down.fw");
                
                restartButton = new ImageButton(restartB);
                restartButton.pad(10);
                restartButton.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(game, true));
                        }
                }
                );
                
                mainMenuButton = new ImageButton(mainMenuB);
                mainMenuButton.pad(10);
                mainMenuButton.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        //game.setScreen(new GameScreen());
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(game));
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
                
                if (WalkEnemy.enemyPositions.size() > 0) {
                	WalkEnemy.removeAll();
                }
                
                // Add buttons
                table.add(restartButton);
                table.row();
                table.add(mainMenuButton);
                table.row();
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
