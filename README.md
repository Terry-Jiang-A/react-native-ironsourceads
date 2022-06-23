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
    Ironsourceads.addEventListener('OninterstitialDidFailToLoad', (adInfo) => {
      logStatus('Ad fail to Loaded: ' +adInfo.error);
    });


    Ironsourceads.addEventListener('OninterstitialDidFailToShow', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('Ads fail to show: ' +adInfo.error);
      
    });
    Ironsourceads.addEventListener('OninterstitialDidOpen', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.failed);
      logStatus('Ads did open');
      
    });
    Ironsourceads.addEventListener('OninterstitialDidShow', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.start);
      logStatus('Ads did show  ');
    });
    Ironsourceads.addEventListener('OndidClickInterstitial', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did click');
    });
    Ironsourceads.addEventListener('OninterstitialDidClose', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did close');
    });


    //reward video Listeners
    Ironsourceads.addEventListener('OnrewardedVideoDidFailToShow', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('Ads fail to show: ' +adInfo.error);
      
    });
    Ironsourceads.addEventListener('OnrewardedVideoDidOpen', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did open');
    });
    Ironsourceads.addEventListener('OnrewardedVideoDidClose', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('reward info: ' +adInfo);
      
    });
    Ironsourceads.addEventListener('OnrewardedVideoDidStart', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did start');
    });
    Ironsourceads.addEventListener('OnrewardedVideoDidEnd', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.click);
      logStatus('Ads did end');
    });
    Ironsourceads.addEventListener('OndidClickRewardedVideo', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('did click: ' +adInfo.info);
      
    });
    Ironsourceads.addEventListener('OndidReceiveRewardForPlacement', (adInfo) => {
      //setIronsourceadshowCompleteState(adsShowState.completed);
      logStatus('reward name: ' +adInfo.rewardName + ' reward amount: '+adInfo.rewardAmount);
      
    });

   

    // Banner Ad Listeners
    Ironsourceads.addEventListener('OnbannerDidLoad', (adInfo) => {
      logStatus('Banner ad loaded ');
      setIsNativeUIBannerShowing(!isNativeUIBannerShowing);
    });
    Ironsourceads.addEventListener('OnbannerDidFailToLoad', (adInfo) => {
      logStatus('Banner ad fail to loaded ' +adInfo.error);
    });
    Ironsourceads.addEventListener('OnbannerWillPresentScreen', (Info) => {
      logStatus('Banner will present screen ');
    });
    Ironsourceads.addEventListener('OndidClickBanner', (adInfo) => {
      logStatus('Banner ad clicked');
    });
    Ironsourceads.addEventListener('OnbannerDidDismissScreen', (adInfo) => {
      logStatus('Banner full screen content dissmissed');
    });
    Ironsourceads.addEventListener('OnbannerWillLeaveApplication', (adInfo) => {
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
