package net.obviam.nanaquest.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

public class SpriteAccessor implements TweenAccessor<Sprite>{

	public static final int ALPHA = 0;
	
	@Override
	public int getValues(Sprite target, int tweenType, float[] returnvalues) {
		switch(tweenType){
			case ALPHA:
				returnvalues[0] = target.getColor().a;
				return 1;
			default:
				//if tweenType is passed in that we don't know, return 0. this shouldn't happen
				assert false;
				return -1;
		
		}
		
	}

	@Override
	public void setValues(Sprite target, int tweenType, float[] newValues) {
		// TODO Auto-generated method stub
		switch(tweenType){
			case ALPHA:
				target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
					//Set color: only alpha should change, r, g and b stay the same
			default:
				assert false;
		}
	}

}