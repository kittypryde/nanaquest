package net.obviam.nanaquest.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealthBar {
	
	TextureAtlas atlas;
	TextureRegion healthBarRegion;
	
	void Init(){

	   String texturefile="images/healthbar_pack";
	   atlas=new TextureAtlas(Gdx.files.internal(texturefile),Gdx.files.internal("/images"));
	   healthBarRegion=atlas.findRegion("health_bar_nq");  //Assuming the image of health bar name is "healthbar"
	 
	}
		 
	public Texture tex;
	public Vector2 position;
	 
	//Target Dimension of image
	 
	public static int targetWidth;
	public static int targetHeight;
	 
	//Src Dimensions of Image
	 
	public int srcWidth;
	public int srcHeight;
	public int srcX;
	public int srcY;
	 
	//Ratio of dimension of target and source
	 
	public float srcTargetRatioX;
	public float srcTargetRatioY;
	 
	//ImagePart variables with values between 0-100 to draw part of image
	 
	public 	int startPercentX;
	public 	int endPercentX;
	public 	int startPercentY;
	public 	int endPercentY;
	 
	public static int clipWidth;
	public static int clipHeight;
	 
	public static int clipSrcWidth;
	public static int clipSrcHeight;
	 
	
	public void TexturePart(TextureRegion reg,float x,float y){
	 
	    tex=reg.getTexture();
	    position=new Vector2(x,y);
	    srcX=reg.getRegionX();
	    srcY=reg.getRegionY();
	    srcWidth=reg.getRegionWidth();
	    srcHeight=reg.getRegionHeight();
	    clipSrcWidth=srcWidth;
	    clipSrcHeight=srcHeight;
	    startPercentX=0;
	    startPercentY=0;
	    endPercentX=100;
	    endPercentY=100;
	    SetTargetDimension(srcWidth,srcHeight);
	}
	 
	public void SetTargetDimension(int targetWidth,int targetHeight){
	    this.targetWidth=targetWidth;
	    this.targetHeight=targetHeight;
	    clipWidth=targetWidth;
	    clipHeight=targetHeight;
	    srcTargetRatioX=(float)targetWidth/(float)srcWidth;
	    srcTargetRatioY=(float)targetHeight/(float)srcHeight;
	}
	 
	public void SetStart(int x,int y){
	    startPercentX=x;
	    startPercentY=y;
	}
	 
	public void SetEnd(int x,int y){
	    endPercentX=x;
	    endPercentY=y;
	}
	 
	public void Draw(SpriteBatch sp){
	        clipSrcWidth=(int)(Math.abs(startPercentX-endPercentY)/100f*srcWidth);
	        clipSrcHeight=(int)(Math.abs(startPercentY-endPercentY)/100f*srcHeight);
	        int startX=srcX+(int)((float)startPercentX/100f*(float)srcX);
	        int startY=srcY+(int)((float)startPercentY/100f*(float)srcY);
	        clipWidth=(int) (srcTargetRatioX*clipSrcWidth);
	        clipHeight=(int) (srcTargetRatioY*clipSrcHeight);

	}
	
}
	
