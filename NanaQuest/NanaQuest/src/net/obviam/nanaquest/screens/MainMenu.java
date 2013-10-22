package net.obviam.nanaquest.screens;

import java.awt.Button;
//import java.awt.event.InputEvent;

import net.obviam.nanaquest.NanaQuest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu implements Screen {
	
	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
		//Appearance
	private Table table;
	private ImageButton megganButton;
	Button buttonExit;
	//private BitmapFont white, black;
	private Label heading;
	final NanaQuest game;

	
	public MainMenu(final NanaQuest gam){
		game = gam;
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
		
		atlas = new TextureAtlas("ui/buttontest.pack");
			//Atlas defines regions of sprite image that will be created
		skin = new Skin();
		skin.addRegions(atlas);
		//Create table
		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	//Create button
		ImageButtonStyle imageButt = new ImageButtonStyle();
		imageButt.up = skin.getDrawable("meggan1");
		imageButt.down = skin.getDrawable("meggan2");
		
		megganButton = new ImageButton(imageButt);
		megganButton.pad(10);
		
		megganButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				//game.setScreen(new GameScreen());
				((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
			}
		}
		);
		
		//Put stuff together/into the table
		table.add(megganButton);
		table.debug();
			//Enables debug lines showing us where cells end 
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