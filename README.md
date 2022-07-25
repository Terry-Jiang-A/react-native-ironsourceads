# react-native-ironsourceads

ironsource ads

## Installation

```sh
npm install react-native-ironsourceads
```

## Usage

```js
import Ironsourceads from "react-native-ironsourceads";
  
  //Initialize SDK
  Ironsourceads.initialize(SDK_KEY, (callback) => {
    setIsInitialized(true);
    logStatus('SDK Initialized: '+ callback);

    // Attach ad listeners for rewarded ads, and banner ads
    attachAdListeners();//need to call removeEventListener to remove listeners.
  });
  
    //Attach ad Listeners for rewarded ads, and banner ads, and so on.
    function attachAdListeners() {　
    Ironsourceads.addEventListener('onInterstitialDidFailToLoad', (adInfo) => {
      logStatus('Ad fail to Loaded: ' +adInfo.error);
    });


    Ironsourceads.addEventListener('onInterstitialDidFailToShow', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('Ads fail to show: ' +adInfo.error);
      
    });
    Ironsourceads.addEventListener('onInterstitialDidOpen', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.failed);
      logStatus('Ads did open');
      
    });
    Ironsourceads.addEventListener('onInterstitialDidShow', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.start);
      logStatus('Ads did show  ');
    });
    Ironsourceads.addEventListener('onDidClickInterstitial', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did click');
    });
    Ironsourceads.addEventListener('onInterstitialDidClose', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did close');
    });


    //reward video Listeners
    Ironsourceads.addEventListener('onRewardedVideoDidFailToShow', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('Ads fail to show: ' +adInfo.error);
      
    });
    Ironsourceads.addEventListener('onRewardedVideoDidOpen', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did open');
    });
    Ironsourceads.addEventListener('onRewardedVideoDidClose', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('reward info: ' +adInfo);
      
    });
    Ironsourceads.addEventListener('onRewardedVideoDidClose', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did start');
    });
    Ironsourceads.addEventListener('onRewardedVideoDidEnd', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did end');
    });
    Ironsourceads.addEventListener('onDidClickRewardedVideo', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('did click: ' +adInfo.info);
      
    });
    Ironsourceads.addEventListener('onDidReceiveRewardForPlacement', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('reward name: ' +adInfo.rewardName + ' reward amount: '+adInfo.rewardAmount);
      
    });

   

    // Banner Ad Listeners
    Ironsourceads.addEventListener('onBannerDidLoad', (adInfo) => {
      logStatus('Banner ad loaded ');
      setIsNativeUIBannerShowing(!isNativeUIBannerShowing);
    });
    Ironsourceads.addEventListener('onBannerDidFailToLoad', (adInfo) => {
      logStatus('Banner ad fail to loaded ' +adInfo.error);
    });
    Ironsourceads.addEventListener('onBannerWillPresentScreen', (Info) => {
      logStatus('Banner will present screen ');
    });
    Ironsourceads.addEventListener('onDidClickBanner', (adInfo) => {
      logStatus('Banner ad clicked');
    });
    Ironsourceads.addEventListener('onBannerDidDismissScreen', (adInfo) => {
      logStatus('Banner full screen content dissmissed');
    });
    Ironsourceads.addEventListener('onBannerWillLeaveApplication', (adInfo) => {
      logStatus('Called when a user would be taken out of the application context');
    });

  }
  
  //load interstitial ads：
  Ironsourceads.loadInterstitial();
  
  //load reward ads：
  Ironsourceads.showRewardVideo();
    
　//load Banner:
　Ironsourceads.loadBottomBanner(BANNER_AD_UNIT_ID);
　
　ios:
  //Modify podfile，add IronSource Ads SDK：
  pod 'IronSourceSDK','7.2.2.1'   

  For specific usage, please refer to example.
  How To Run example:
  1,$ cd example && npm install
  2,$ cd ios && pod install
  3,$ cd .. && npm run ios or npm run android
        　
　See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
