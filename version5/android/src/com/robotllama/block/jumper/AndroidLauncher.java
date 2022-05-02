package com.robotllama.block.jumper;

import java.util.Map;

import Utils.Score;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.robotllama.ActionResolver;
import com.robotllama.Main;

public class AndroidLauncher extends AndroidApplication  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActionResolver{

	private BuildButtons buttons;
	
	private static final String TAG = "Game Name";

	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	private GoogleApiClient mGoogleApiClient;

	// Are we currently resolving a connection failure?
	private boolean mResolvingConnectionFailure = false;

	// Has the user clicked the sign-in button?
	private boolean mSignInClicked = false;

	// Set to true to automatically start the sign in flow when the Activity starts.
	// Set to false to require the user to click the button in order to sign in.
	private boolean mAutoStartSignInFlow = true;

	// Has connect been triggered
	private boolean connected = false, seeScore = false;

	//Google AdView
	AdView adView;

	//Google AdRequest
	AdRequest adRequest;
	AdRequest interAdRequest;
	
	//Google InterstitialAd
	private InterstitialAd interstitial;
	private boolean interClosed = false;

	//Ad state
	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	static RelativeLayout layout;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		super.onCreate(savedInstanceState);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
		.addApi(Games.API).addScope(Games.SCOPE_GAMES)
		.build();

		// Create the layout
		layout = new RelativeLayout(this);

		
		// Create the libgdx View
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 2;
		
		View gameView = initializeForView(new Main(this), config);
		layout.addView(gameView);
		
		if(getResources().getBoolean(R.bool.admob_banner_on)){
			// Create and setup the AdMob view
			adView = new AdView(this);
			adView.setAdUnitId(getString(R.string.admob_id)); // Put in secret key here

			adView.setAdSize(AdSize.SMART_BANNER);

			adView.setAdListener(new AdListener() {
				@Override
				public void onAdFailedToLoad(int errorCode) {
					super.onAdFailedToLoad(errorCode);
					//Log.d("Admob", "Error code: " + errorCode);
				}
			});

			adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);

			// Add the libgdx view


			// Add the AdMob view
			RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			if(getResources().getBoolean(R.bool.admob_banner_top))adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			if(getResources().getBoolean(R.bool.admob_banner_bottom))adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			layout.addView(adView, adParams);
		}

	    // Create the interstitial.
		if(getResources().getBoolean(R.bool.admob_interstitial_on)){
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(getString(R.string.InterstitialAd_unit_id));

			// Create ad request.
			interAdRequest = new AdRequest.Builder().build();

			// Begin loading your interstitial.
			interstitial.loadAd(adRequest);
		}
	    
		// Hook it all up
		setContentView(layout);
	}

	@Override
	public void showAds(final boolean show) {
		if(getResources().getBoolean(R.bool.admob_banner_on)){
			handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
		}
	}

	//Google Admob Handler
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ADS:
				adView.setVisibility(View.VISIBLE);
				adView.resume();
				break;
			case HIDE_ADS:
				adView.setVisibility(View.GONE);
				adView.pause();
				break;
			}
		}
	};


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		//Log.d(TAG, "onConnected() called. Sign in successful!");
		if(seeScore){
			googlePlayView();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		//Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		//Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

		if (mResolvingConnectionFailure) {
			//Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
			return;
		}

		if (mSignInClicked || mAutoStartSignInFlow) {
			mAutoStartSignInFlow = false;
			mSignInClicked = false;
			mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
					connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
		}
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			//Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode=" + responseCode + ", intent=" + intent);
			mSignInClicked = false;
			mResolvingConnectionFailure = false;
			if (responseCode == RESULT_OK) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void googlePlaySubmit(int score) {
		if (mGoogleApiClient.isConnected()) {
			Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_leaders), score);
		}
	}

	@Override
	public void googlePlayView() {
		if (mGoogleApiClient.isConnected()) {
			startActivityForResult(
					Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_leaders)),100);
		}
	}

	@Override
	public void connect(boolean seeScore){
		this.seeScore = seeScore;
		if (!mGoogleApiClient.isConnected()) {
			mSignInClicked = true;
	        mGoogleApiClient.connect();
	        connected = true;
		}else{
			googlePlayView();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.finish();
		android.os.Process.killProcess(android.os.Process.myPid()); 
	}
	
	@Override
	public void share(){
		int score = Score.getHighScore();
		String msg = getResources().getString(R.string.shareMsg);
		msg = msg.replace("@score@", String.valueOf(score));
		msg = msg.replace("@appName@", getAppName());
		msg = msg.replace("@gameUrl@", "http://play.google.com/store/apps/details?id=" + getPackageName());
		
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getAppName());
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	@Override
	public void rate(){
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
		  startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
		  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
		}
	}
	
	@Override
	public String getAppName(){
		int stringId = R.string.app_name;
	    return getString(stringId);
	}

	@Override
	public String getHeroColor() {
		return getResources().getString(R.string.heroColor);
	}

	@Override
	public String getObjectColor() {
		return getResources().getString(R.string.objectColor);
	}
	
	@Override
	public String getSplashBackgroundColor() {
		return getResources().getString(R.string.splashBackgroundColor);
	}

	@Override
	public String getGameBackgroundColor() {
		return getResources().getString(R.string.gameBackgroundColor);
	}
	
	@Override
	public void loadButtons(int size){
		buttons = new BuildButtons(this, size);
	}

	@Override
	public Map<String, Texture> getButtonTextures() {
		return buttons.getButtonTextures();
	}

	@Override
	public String getMainTitleColor() {
		return getResources().getString(R.string.mainTitleColor);
	}

	@Override
	public String getSubTitleColor() {
		return getResources().getString(R.string.subTitleColor);
	}

	@Override
	public String getMainTitleFontFile() {
		return getResources().getString(R.string.titleTitleFontFile);
	}

	@Override
	public String getSubTitleFontFile() {
		return getResources().getString(R.string.subTitleFontFile);
	}

	@Override
	public int getMainTitleSize() {
		return getResources().getInteger(R.integer.titleTitleFontSize);
	}

	@Override
	public int getSubTitleSize() {
		return getResources().getInteger(R.integer.subTitleFontSize);
	}

	@Override
	public int getGameSpeed() {
		return getResources().getInteger(R.integer.gameSpeed);
	}

	@Override
	public String getSubTitle() {
		return getResources().getString(R.string.subTitle);
	}

	@Override
	public int getHeroSize() {
		return getResources().getInteger(R.integer.heroSize);
	}

	@Override
	public int getObjectSize() {
		return getResources().getInteger(R.integer.objectSize);
	}

	@Override
	public String getGroundColor() {
		return getResources().getString(R.string.groundColor);
	}
	

	@Override
	public void showInterAds() {
		if(getResources().getBoolean(R.bool.admob_interstitial_on)){
			runOnUiThread(new Runnable() {
				public void run() {
					if (interstitial.isLoaded()) {
						interstitial.show();
					}

					interstitial.setAdListener(new AdListener() {
						public void onAdClosed() {
							interstitial.loadAd(adRequest);
							interClosed = true;
						}
					});
				}
			});
		}
	}

	@Override
	public boolean wasInterClosed() {
		return interClosed;
	}
	
	@Override
	public void resetInterClosed() {
		interClosed = false;
	}

	@Override
	public int getInterAdFrequency() {
		return getResources().getInteger(R.integer.InterstitialAd_frequency);
	}

	@Override
	public boolean useBackgroundImage() {
		return getResources().getBoolean(R.bool.useBackgroundImage);
	}

	@Override
	public boolean useTitleImage() {
		return getResources().getBoolean(R.bool.useTitleImage);
	}

	@Override
	public boolean useWallImage() {
		return getResources().getBoolean(R.bool.useWallImage);
	}

	@Override
	public boolean useObjectImage() {
		return getResources().getBoolean(R.bool.useObjectImage);
	}
	
	@Override
	public boolean useHeroImage() {
		return getResources().getBoolean(R.bool.useHeroImage);
	}

	@Override
	public int getGameSpeedIncrement() {
		return getResources().getInteger(R.integer.gameSpeedIncrement);
	}

	@Override
	public int getGameSpeedIncrementIncrease() {
		return getResources().getInteger(R.integer.gameSpeedIncrementIncrease);
	}

	@Override
	public String getHeroParticleColor() {
		return getResources().getString(R.string.heroParticleColor);
	}

	@Override
	public String getGroundParticleColor() {
		return getResources().getString(R.string.groundParticleColor);
	}
}
