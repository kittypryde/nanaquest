package net.obviam.nanaquest.model;

import java.util.ArrayList;
import java.util.List;

import net.obviam.nanaquest.NanaQuest;
import net.obviam.nanaquest.utils.GamePreferences;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class World {
	
	public static NanaQuest game;
	
	/** Our player controlled hero **/
	Bob bob;
	
	/** Boss **/
	Boss boss;
	
	/** Health Bar **/
	HealthBar healthBar;
	
	/** Our enemies **/
	WalkEnemy walkEnemy;
	ShootEnemy shootEnemy;
	
	/** The Ship **/
	Ship ship;

	/** A world has a level through which Bob needs to go through **/
	Level level;
		
	/** The collision boxes **/
	Array<Rectangle> collisionRects = new Array<Rectangle>();


	// Getters -----------
	
	public Array<Rectangle> getCollisionRects() {
		return collisionRects;
	}
	public Bob getBob() {
		return bob;
	}
	
	public Boss getBoss() {
		return boss;
	}
	
	public WalkEnemy getWalkEnemy() {
		return walkEnemy;
	}
	
	public ShootEnemy getShootEnemy() {
		return shootEnemy;
	}
	
	public Ship getShip() {
		return ship;
	}
	
	public Level getLevel() {
		return level;
	}
	/** Return only the blocks that need to be drawn **/
	public List<Block> getDrawableBlocks(int width, int height) {
			
		int x = (int)Bob.getPosition().x - width;
		int y = (int)Bob.getPosition().y - height;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		int x2 = x + 2 * width;
		int y2 = y + 2 * height;
		if (x2 > Level.getWidth()) {
			x2 = Level.getWidth() - 1;
		}
		if (y2 > Level.getHeight()) {
			y2 = Level.getHeight() - 1;
		}
		
		List<Block> blocks = new ArrayList<Block>();
		Block block;
		for (int col = x; col <= x2; col++) {
			for (int row = y; row <= y2; row++) {
				block = level.getBlocks()[col][row];
				if (block != null) {
					blocks.add(block);
				}
			}
		}
		return blocks;
	}

	// --------------------
	public World(NanaQuest game) {
		World.game = game;
		createDemoWorld();
	}

	private void createDemoWorld() {
		
//		if (GamePreferences.prefs.getBoolean("Forest CP1")) {
//			bob = new Bob (CheckPoints.forest_cp1);
//		} else {
			bob = new Bob(new Vector2(7, 1));
//		}
		boss = new Boss(new Vector2(135, 1));
		Boss.newBullet();
		// create 10 forest bugs
//		for (int i = 0; i < 10; i++) {
//			enemy = new Enemy(new Vector2(30 + (i*3), 20), "forest-bug");
//		}
		
		// create 10 forest spiders
		for (int i = 0; i < 10; i++) {
			walkEnemy = new WalkEnemy(new Vector2(30 + (i*5), 20));
		}
		
		shootEnemy = new ShootEnemy(new Vector2(30, 1));
		shootEnemy = new ShootEnemy(new Vector2(35, 1));
		shootEnemy = new ShootEnemy(new Vector2(45, 1));
		
		ship = new Ship(new Vector2(30, 1));
		healthBar = new HealthBar(10, 10);
		level = new Level();
		
	}
	
	public static void dispose() {
		
	}
}
