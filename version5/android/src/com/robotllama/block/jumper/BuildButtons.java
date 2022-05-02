package com.robotllama.block.jumper;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.robotllama.block.jumper.R;

public class BuildButtons {
	
	AndroidLauncher main;
	Map<String, Object[]> iconList = new HashMap<String, Object[]>();
	Map<String, Texture> buttonTextures = new HashMap<String, Texture>();
	int width, height;
	Typeface font;
	int size;
	int backgroundColor;
	String buttonShape;
	
	public BuildButtons(AndroidLauncher main, int size){
		this.size = size;
		this.main = main;
		this.font = Typeface.createFromAsset(
				main.getAssets(), 
				"fonts/"+main.getResources().getString(R.string.iconFont));
		
		displaySize();
		
		backgroundColor = main.getResources().getColor(R.color.buttonBackgroundColor);
		buttonShape = "square";
		
		iconList.put("play", new Object[]{FontIcons.PLAY, main.getResources().getColor(R.color.playIconColor)});
		iconList.put("replay", new Object[]{FontIcons.REPLAY, main.getResources().getColor(R.color.replayIconColor)});
		iconList.put("home",new Object[]{ FontIcons.HOME, main.getResources().getColor(R.color.homeIconColor)});
		iconList.put("sound", new Object[]{FontIcons.SOUND, main.getResources().getColor(R.color.soundIconColor)});
		iconList.put("mute", new Object[]{FontIcons.MUTE, main.getResources().getColor(R.color.muteIconColor)});
		iconList.put("google", new Object[]{FontIcons.GOOGLE, main.getResources().getColor(R.color.googleIconColor)});
		iconList.put("score", new Object[]{FontIcons.SCORES, main.getResources().getColor(R.color.scoreIconColor)});
		iconList.put("star", new Object[]{FontIcons.STAR, main.getResources().getColor(R.color.starIconColor)});
		iconList.put("jump", new Object[]{FontIcons.JUMP, main.getResources().getColor(R.color.jumpIconColor)});
		iconList.put("share", new Object[]{FontIcons.SHARE, main.getResources().getColor(R.color.shareIconColor)});
		
		for(String key : iconList.keySet()){
			createFlatButton(key);
		}
	}
	
	public Map<String, Texture> getButtonTextures(){
		return buttonTextures;
	}
	
	@SuppressLint("NewApi")
	public void displaySize(){
		Display display = main.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}
	
	private void createFlatButton(final String key){
		Object[] info = iconList.get(key);
		FontIcons icon = (FontIcons) info[0];
		int color = (Integer) info[1];
				
		int buttonSize = size * 2;
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setColor(backgroundColor);
		paint.setStyle(Paint.Style.FILL);
		
		final Bitmap bitmap = Bitmap.createBitmap(buttonSize, buttonSize, Bitmap.Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap);
		canvas2.setBitmap(bitmap);
		
		canvas2.drawRect(0, 0, buttonSize, buttonSize, paint);
		
		Paint mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG);
		mScorePaint.setTypeface(font);
		mScorePaint.setAntiAlias(true);
		int fontsize = (int) (buttonSize * .7f);
		mScorePaint.setTextSize(fontsize);
		
		
		float x = buttonSize/2 -(mScorePaint.measureText(icon.getString()) / 2);
		float y = (buttonSize/2 - (mScorePaint.descent()/2)) + ((mScorePaint.ascent() * -1)/2) ;
		mScorePaint.setColor(color);
		canvas2.drawText(icon.getString(), x, y, mScorePaint);
		
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Format.RGBA8888);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
				bitmap.recycle();
				tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				addToList(key, tex);
			}
		});
	}
	
	public void addToList(String key, Texture tex){
		buttonTextures.put(key, tex);
	}
}
