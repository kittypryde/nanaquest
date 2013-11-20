package net.obviam.nanaquest.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealthBar {
	
	public static float bobHealth;
	public static float bossHealth;
	
	public HealthBar (float bobHealth, float bossHealth) {
		this.bobHealth = bobHealth;
		this.bossHealth = bossHealth;
	}
	
}
	
