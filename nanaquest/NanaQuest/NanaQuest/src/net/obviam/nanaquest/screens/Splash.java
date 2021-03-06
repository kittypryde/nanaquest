package net.obviam.nanaquest.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.tween.SpriteAccessor;

public class Splash implements Screen{


        private Sprite splash;
        final NanaQuest game;
        private TweenManager tweenManager;
        
        public Splash(final NanaQuest gam){
                game = gam;
        }
        
        @Override
        public void render(float delta) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                
                tweenManager.update(delta);
                        //Delta is time between current frame and last frame
                        //used to maintain consistency in speed, no matter what fps happens to be
                game.batch.begin();
                splash.draw(game.batch);
                game.batch.end();
                game.batch.dispose();
        }

        @Override
        public void resize(int width, int height) {

        }

        @Override
        public void show() {
                tweenManager = new TweenManager();
                Tween.registerAccessor(Sprite.class, new SpriteAccessor());
                
                Texture splashTexture = new Texture("images/splash_screen.png");
                splash = new Sprite(splashTexture);
                splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                
                Tween.set(splash, SpriteAccessor.ALPHA).target(0).start(tweenManager);
                        //Starts at target 0 so alpha starts at 0 (totally transparent)
                Tween.to(splash, SpriteAccessor.ALPHA, 2).target(1).repeatYoyo(1,5).setCallback(new TweenCallback() {
        
                @Override
                public void onEvent(int arg0, BaseTween<?> source) {
                        game.setScreen(new MainMenu(game));
                        
                        
                }
        }).start(tweenManager);
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
                splash.getTexture().dispose();
        }

}