package com.robotllama;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;

public interface ActionResolver {
	
	public void googlePlaySubmit(int score);
 
	public void googlePlayView();
	
	public void connect(boolean seeScore);
	
	public void showAds(boolean show);
	
	public void showInterAds();
	
	public boolean wasInterClosed();
	
	public void resetInterClosed();
	
	public int getInterAdFrequency();
	
	public void share();
	
	public void rate();

	// =========================================
	// Grabbing Title and Sub
	// =========================================
	
	// hero speed
	public String getAppName();
	
	// pulse count
	public String getSubTitle();
	
	
	
	// =========================================
	// Grabbing Difficulty Settings
	// =========================================
	
	// hero speed
	public int getGameSpeed();	
	
	//increment
	public int getGameSpeedIncrement();
	
	//increment Increase value
	public int getGameSpeedIncrementIncrease();
	
	
	
	// =========================================
	// Grabbing title files
	// =========================================
	
	// main font size
	public String getMainTitleFontFile();
	
	// main font size
	public String getSubTitleFontFile();	
	
	
	
	// =========================================
	// Grabbing title colors
	// >> colors
	// =========================================
	
	// main font color
	public String getMainTitleColor();
	
	// sub font color
	public String getSubTitleColor();
	
	
	
	// =========================================
	// Grabbing title size
	// >> colors
	// =========================================
	
	// main font size
	public int getMainTitleSize();
	
	// title image boolean
	public boolean useTitleImage();
	
	// main font size
	public int getSubTitleSize();	
	
	
	
	// =========================================
	// Grabbing Entity Info
	// >> colors
	// =========================================
	
	// hero color
	public String getHeroColor();
	
	// Object color
	public int getHeroSize();
	
	// hero image boolean
	public boolean useHeroImage();
	
	// Object color
	public String getObjectColor();
	
	// Object color
	public int getObjectSize();
	
	// wall image boolean
	public boolean useObjectImage();
	
	// ground color
	public String getGroundColor();	
	
	// wall image boolean
	public boolean useWallImage();
	
	// hero particle color
	public String getHeroParticleColor();	
	
	// ground particle color
	public String getGroundParticleColor();	
	
	
	// =========================================
	// Grabbing game settings
	// >> colors
	// =========================================
	
	//splash background color
	public String getSplashBackgroundColor();
	
	//splash background color
	public String getGameBackgroundColor();
	
	//use background image
	public boolean useBackgroundImage();
	
	
	
	// =========================================
	// Grabbing game buttons
	// =========================================
	
	public void loadButtons(int size);
	
	public Map<String, Texture> getButtonTextures();
}
