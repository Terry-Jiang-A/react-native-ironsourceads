package com.reactnativeironsourceads;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import static com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InitializationListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;

import java.util.concurrent.TimeUnit;
import android.util.Log;
import android.text.TextUtils;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.Nullable;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.content.pm.ActivityInfo;


@ReactModule(name = IronsourceadsModule.NAME)
public class IronsourceadsModule extends ReactContextBaseJavaModule implements RewardedVideoListener, InterstitialListener{
    public static final String NAME = "Ironsourceads";

    private static final String SDK_TAG = "Ironsource Ads Sdk";
    private static final String TAG     = "Ironsource Ads Module";


    public static  IronsourceadsModule instance;
    private static Activity          sCurrentActivity;
    private RelativeLayout bottomBannerView;
    public static final int BANNER_WIDTH = 320;
    public static final int BANNER_HEIGHT = 50;
    private Placement mPlacement;

    private IronSourceBannerLayout mIronSourceBannerLayout;

    private Callback mInitCallback;

    // Parent Fields
    private boolean                  isPluginInitialized;
    private boolean                  isSdkInitialized;

    public static IronsourceadsModule getInstance()
    {
      return instance;
    }

    public IronsourceadsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        instance = this;
        sCurrentActivity = reactContext.getCurrentActivity();
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    @Nullable
    private Activity maybeGetCurrentActivity()
    {
      // React Native has a bug where `getCurrentActivity()` returns null: https://github.com/facebook/react-native/issues/18345
      // To alleviate the issue - we will store as a static reference (WeakReference unfortunately did not suffice)
      if ( getReactApplicationContext().hasCurrentActivity() )
      {
        sCurrentActivity = getReactApplicationContext().getCurrentActivity();
      }

      return sCurrentActivity;
    }


    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod(isBlockingSynchronousMethod = true)
    public boolean isInitialized()
    {
      return isPluginInitialized && isSdkInitialized;
    }

    @ReactMethod
    public void initialize(final String sdkKey, final Callback callback)
    {
      // Check if Activity is available
      Activity currentActivity = maybeGetCurrentActivity();
      if ( currentActivity != null )
      {
        performInitialization( sdkKey, currentActivity, callback );
      }
      else
      {
        Log.d( TAG, "No current Activity found! Delaying initialization..." );

        new Handler().postDelayed(new Runnable()
        {
          @Override
          public void run()
          {
            Context contextToUse = maybeGetCurrentActivity();
            if ( contextToUse == null )
            {
              Log.d( TAG,"Still unable to find current Activity - initializing SDK with application context" );
              contextToUse = getReactApplicationContext();
            }

            performInitialization( sdkKey, contextToUse, callback );
          }
        }, TimeUnit.SECONDS.toMillis( 3 ) );
      }
    }

    @ReactMethod()
    public void loadInterstitial()
    {
      IronSource.loadInterstitial();

    }

    @ReactMethod()
    public void showRewardVideo()
    {
      if (IronSource.isRewardedVideoAvailable())
        //show rewarded video
        IronSource.showRewardedVideo();

    }

    @ReactMethod()
    public void loadBottomBanner(String adUnitId)
    {
        //createAndloadBanner();
        loadBannerView();

    }

    @ReactMethod()
    public void unLoadBottomBanner()
    {
      /*if (bottomBannerView != null && sCurrentActivity != null){
        sCurrentActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            bottomBannerView.setVisibility(View.INVISIBLE);
            bottomBannerView.removeAllViews();
            bottomBannerView = null;

          }
        });
        WritableMap params = Arguments.createMap();
        params.putString( "adUnitId", "unload banner view" );

        sendReactNativeEvent( "OnbannerViewDidLeaveApplication", params );

      }*/
      destroyAndDetachBanner();



    }

    private static  int toPixelUnits(int dipUnit) {
      float density = sCurrentActivity.getResources().getDisplayMetrics().density;
      return Math.round(dipUnit * density);
    }

    private void loadBannerView(){
      sCurrentActivity.runOnUiThread(new Runnable(){

        @Override
        public void run(){
          // choose banner size
          ISBannerSize size = ISBannerSize.BANNER;

          // instantiate IronSourceBanner object, using the IronSource.createBanner API
          mIronSourceBannerLayout = IronSource.createBanner(sCurrentActivity, size);

          // add IronSourceBanner to your container
          sCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
          //sCurrentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏：根据传感器横向切换

          bottomBannerView = new RelativeLayout(sCurrentActivity.getApplicationContext());

          LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
          sCurrentActivity.addContentView(bottomBannerView,lp);

          //RelativeLayout.LayoutParams bannerLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
          //bannerLayoutParams.setMargins(5, 5, 5, 5);

          int width = toPixelUnits(BANNER_WIDTH);
          int height = toPixelUnits(BANNER_HEIGHT);
          RelativeLayout.LayoutParams bannerLayoutParams = new RelativeLayout.LayoutParams(width, height);
          bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
          bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

          //bottomBannerView.addView( applovin_adView, new android.widget.FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER ) );
          bottomBannerView.addView( mIronSourceBannerLayout,bannerLayoutParams);

          if (mIronSourceBannerLayout != null) {
            // set the banner listener
            mIronSourceBannerLayout.setBannerListener(new BannerListener() {
              @Override
              public void onBannerAdLoaded() {
                Log.d(TAG, "onBannerAdLoaded");
                // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                bottomBannerView.setVisibility(View.VISIBLE);
                WritableMap params = Arguments.createMap();
                params.putString( "message", "" );

                sendReactNativeEvent( "onBannerDidLoad", params );
              }

              @Override
              public void onBannerAdLoadFailed(IronSourceError error) {
                Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                WritableMap params = Arguments.createMap();
                params.putString( "error", error.getErrorMessage() );

                sendReactNativeEvent( "onBannerDidFailToLoad", params );
              }

              @Override
              public void onBannerAdClicked() {
                Log.d(TAG, "onBannerAdClicked");
                WritableMap params = Arguments.createMap();
                params.putString( "message", "" );

                sendReactNativeEvent( "onDidClickBanner", params );
              }

              @Override
              public void onBannerAdScreenPresented() {
                Log.d(TAG, "onBannerAdScreenPresented");
                WritableMap params = Arguments.createMap();
                params.putString( "message", "" );

                sendReactNativeEvent( "onBannerWillPresentScreen", params );
              }

              @Override
              public void onBannerAdScreenDismissed() {
                Log.d(TAG, "onBannerAdScreenDismissed");
                WritableMap params = Arguments.createMap();
                params.putString( "message", "" );

                sendReactNativeEvent( "onBannerDidDismissScreen", params );
              }

              @Override
              public void onBannerAdLeftApplication() {
                Log.d(TAG, "onBannerAdLeftApplication");
                WritableMap params = Arguments.createMap();
                params.putString( "message", "" );

                sendReactNativeEvent( "onBannerWillLeaveApplication", params );
              }
            });

            // load ad into the created banner
            IronSource.loadBanner(mIronSourceBannerLayout);
          } else {
            Log.e(TAG, "IronSource.createBanner returned null");
          }


        }
      });
    }

    private void performInitialization(final String sdkKey, final Context context, final Callback callback)
    {
      // Guard against running init logic multiple times
      if ( isPluginInitialized ) return;

      isPluginInitialized = true;


      // If SDK key passed in is empty
      if ( TextUtils.isEmpty( sdkKey ) )
      {
        throw new IllegalStateException( "Unable to initialize Unity Ads SDK - no SDK key provided!" );
      }

      // Initialize SDK
      mInitCallback = callback;
      IronSource.setRewardedVideoListener(this);
      // set client side callbacks for the offerwall
      SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
      // set the interstitial listener
      IronSource.setInterstitialListener(this);
      IronSource.init(sCurrentActivity, sdkKey, new InitializationListener() {
        @Override
        public void onInitializationComplete() {
          // ironSource SDK is initialized
          isSdkInitialized = true;
          mInitCallback.invoke( "success" );

        }
      });

    }


    /**
     * Destroys IronSource Banner and removes it from the container
     *
     */
    private void destroyAndDetachBanner() {
      IronSource.destroyBanner(mIronSourceBannerLayout);
      if (bottomBannerView != null && sCurrentActivity != null){
        sCurrentActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            bottomBannerView.setVisibility(View.INVISIBLE);
            bottomBannerView.removeAllViews();
            bottomBannerView = null;

          }
        });
      }
    }

    @Override
    public void onInterstitialAdReady() {
      if (IronSource.isInterstitialReady()) {
        //show the interstitial
        IronSource.showInterstitial();
      }

    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
      WritableMap params = Arguments.createMap();
      params.putString( "error", ironSourceError.getErrorMessage() );

      sendReactNativeEvent( "onInterstitialDidFailToLoad", params );

    }

    @Override
    public void onInterstitialAdOpened() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onInterstitialDidOpen", params );

    }

    @Override
    public void onInterstitialAdClosed() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onInterstitialDidClose", params );

    }

    @Override
    public void onInterstitialAdShowSucceeded() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onInterstitialDidShow", params );

    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
      WritableMap params = Arguments.createMap();
      params.putString( "error", ironSourceError.getErrorMessage() );

      sendReactNativeEvent( "onInterstitialDidFailToShow", params );

    }

    @Override
    public void onInterstitialAdClicked() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onDidClickInterstitial", params );

    }

    @Override
    public void onRewardedVideoAdOpened() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onRewardedVideoDidOpen", params );

    }

    @Override
    public void onRewardedVideoAdClosed() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onRewardedVideoDidClose", params );

    }

    @Override
    public void onRewardedVideoAvailabilityChanged(boolean b) {
      if(b){
        IronSource.showRewardedVideo();
      }


    }

    @Override
    public void onRewardedVideoAdStarted() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onRewardedVideoDidStart", params );

    }

    @Override
    public void onRewardedVideoAdEnded() {
      WritableMap params = Arguments.createMap();
      params.putString( "message", "" );

      sendReactNativeEvent( "onRewardedVideoDidEnd", params );

    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
      WritableMap params = Arguments.createMap();
      params.putString( "info", "placeName: " +placement.getPlacementName() +"RewardName: " +placement.getRewardName()+ "RewardAmount: " +placement.getRewardAmount() );

      sendReactNativeEvent( "onDidReceiveRewardForPlacement", params );

    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
      WritableMap params = Arguments.createMap();
      params.putString( "error", ironSourceError.getErrorMessage() );

      sendReactNativeEvent( "onRewardedVideoDidFailToShow", params );

    }

    @Override
    public void onRewardedVideoAdClicked(Placement placement) {
      WritableMap params = Arguments.createMap();
      params.putString( "info", "placeName: " +placement.getPlacementName() +"RewardName: " +placement.getRewardName()+ "RewardAmount: " +placement.getRewardAmount() );

      sendReactNativeEvent( "onDidClickRewardedVideo", params );

    }

    // React Native Bridge
    private void sendReactNativeEvent(final String name, @Nullable final WritableMap params)
    {
      getReactApplicationContext()
        .getJSModule( RCTDeviceEventEmitter.class )
        .emit( name, params );
    }
}
