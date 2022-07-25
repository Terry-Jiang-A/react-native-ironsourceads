import Ironsourceads from 'react-native-ironsourceads';

import React, {useState} from 'react';
import {Platform, StyleSheet, Text, View} from 'react-native';
import AppButton from './AppButton';
import 'react-native-gesture-handler';
import {NavigationContainer} from "@react-navigation/native";

var adLoadState = {
  notLoaded: 'NOT_LOADED',
  loading: 'LOADING',
  loaded: 'LOADED',
};

var adsShowState = {
  notStarted: 'NOT_STARTED',
  completed: 'COMPLETED',
  failed: 'FAILED',
  start: 'STARTED',
  click: 'CLICKED',
};

const App = () => {

  // GameID
  const SDK_KEY = Platform.select({
    ios: '153c717b1',
    android: '153c75139',
  });

  const INTERSTITIAL_AD_UNIT_ID = Platform.select({
    ios: 'DefaultInterstitial',
    android: 'DefaultInterstitial',
  });

  const REWARDED_AD_UNIT_ID = Platform.select({
    ios: 'DefaultRewardedVideo',
    android: 'DefaultRewardedVideo',
  });

  const BANNER_AD_UNIT_ID = Platform.select({
    ios: 'DefaultBanner',
    android: 'DefaultBanner',
  });

  // Create states
  const [isInitialized, setIsInitialized] = useState(false);
  const [interstitialAdLoadState, setInterstitialAdLoadState] = useState(adLoadState.notLoaded);
  const [IronsourceadshowCompleteState, setIronsourceadshowCompleteState] = useState(adsShowState.notStarted);
  const [interstitialRetryAttempt, setInterstitialRetryAttempt] = useState(0);
  const [rewardedAdLoadState, setRewardedAdLoadState] = useState(adLoadState.notLoaded);
  const [isNativeUIBannerShowing, setIsNativeUIBannerShowing] = useState(false);
  const [statusText, setStatusText] = useState('Initializing SDK...');


  Ironsourceads.initialize(SDK_KEY, (callback) => { //second parameter for test mode, 1 default. 0 for production.
    setIsInitialized(true);
    logStatus('SDK Initialized: '+ callback);

    // Attach ad listeners for rewarded ads, and banner ads
    attachAdListeners();
  });

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
    Ironsourceads.addEventListener('onRewardedVideoDidStart', (adInfo) => {
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

  function getInterstitialButtonTitle() {
    if (interstitialAdLoadState === adLoadState.notLoaded) {
      return 'Load Interstitial';
    } else if (interstitialAdLoadState === adLoadState.loading) {
      return 'Loading...';
    } else {
      return 'Show Interstitial'; // adLoadState.loaded
    }
  }

  function getRewardedButtonTitle() {
    if (rewardedAdLoadState === adLoadState.notLoaded) {
      return 'Load Rewarded Ad';
    } else if (rewardedAdLoadState === adLoadState.loading) {
      return 'Loading...';
    } else {
      return 'Show Rewarded Ad'; // adLoadState.loaded
    }
  }

  function logStatus(status) {
    console.log(status);
    setStatusText(status);
  }

  return (
    <NavigationContainer>
      <View style={styles.container}>
        <Text style={styles.statusText}>
          {statusText}
        </Text>
        <AppButton
          title={getInterstitialButtonTitle()}
          enabled={
            isInitialized && interstitialAdLoadState !== adLoadState.loading
          }
          onPress={() => {
            Ironsourceads.loadInterstitial();
          }}
        />
        <AppButton
          title='Show Rewarded Ad'
          enabled={isInitialized && rewardedAdLoadState !== adLoadState.loading}
          onPress={() => {
            Ironsourceads.showRewardVideo();
          }}
        />
        <AppButton
          title={isNativeUIBannerShowing ? 'Hide Native UI Banner' : 'Show Native UI Banner'}
          enabled={isInitialized}
          onPress={() => {
            if (isNativeUIBannerShowing) {
              Ironsourceads.unLoadBottomBanner();
            }else{
              Ironsourceads.loadBottomBanner(BANNER_AD_UNIT_ID);
            
            } 
            
          }}
        /> 
        
      </View>
    </NavigationContainer>
  );
};


const styles = StyleSheet.create({
  container: {
    paddingTop: 80,
    flex: 1, // Enables flexbox column layout
  },
  statusText: {
    marginBottom: 10,
    backgroundColor: 'green',
    padding: 10,
    fontSize: 20,
    textAlign: 'center',
  },
  banner: {
    // Set background color for banners to be fully functional
    backgroundColor: '#000000',
    position: 'absolute',
    width: '100%',
    height: 300,
    bottom: Platform.select({
      ios: 36, // For bottom safe area
      android: 0,
    })
  }
});

export default App;
