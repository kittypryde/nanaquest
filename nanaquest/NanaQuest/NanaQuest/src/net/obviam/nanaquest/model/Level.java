package net.obviam.nanaquest.model;

import com.badlogic.gdx.math.Vector2;
import net.obviam.nanaquest.model.Block;

public class Level {

	private int row = 0;
	private static int width;
	private static int height;
	private Block[][] blocks;

	public static int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		Level.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		Level.height = height;
	}

	public Block[][] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[][] blocks) {
		this.blocks = blocks;
	}

	public Level() {
		loadDemoLevel();
	}
	
	public Block get(int x, int y) {
		return blocks[x][y];
	}

	// Create World - use this to edit blocks 
		private void loadDemoLevel() {
			width = 140;
			height = 20;
			blocks = new Block[width][height];
			
			// null grid 
			for (int col = 0; col < width; col++) {
				for (int row = 0; row < height; row++) {
					blocks[col][row] = null;
				}
			}
			
			// bottom row
			for (int col = 0; col < width; col++) {
				blocks[col][0] = new Block(new Vector2(col, 0));
			}
			
			// special jump platform
			row = 4;
			for (int col = 22; col < 30; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			
			// ship test
			row = 6 ;
			for (int col = 56; col < 58; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			row = 16 ;
			for (int col = 53; col < 60; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			row = 4;
			for (int col = 60; col < 70; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			row = 14;
			for (int col = 65; col < 75; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}			
			row = 10;
			for (int col = 75; col < 85; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			row = 18;
			for (int col = 75; col < 90; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			row = 14;
			for (int col = 86; col < 95; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
			
			
			// normal jump platform
			row = 4;
			for (int col = 10; col < 13; col++) {
				blocks[col][row] = new Block(new Vector2(col, row));
			}
		}
	}
