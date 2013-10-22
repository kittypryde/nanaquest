package net.obviam.nanaquest.model;

import com.badlogic.gdx.math.Vector2;
import net.obviam.nanaquest.model.Block;

public class Level {

	private static int width;
	private static int height;
	private Block[][] blocks;

	public static int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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
			width = 80;
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
			
			//stairs
			int col = 5;
			for (int row = 2; row < height; row++) {
				col++;
				blocks[col][row] = new Block(new Vector2(col, row));
			}
		}
	}
