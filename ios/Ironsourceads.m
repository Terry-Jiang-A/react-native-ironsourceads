#import "Ironsourceads.h"

#define ROOT_VIEW_CONTROLLER (UIApplication.sharedApplication.keyWindow.rootViewController)


@interface Ironsourceads()

// Parent Fields
@property (nonatomic, assign, getter=isPluginInitialized) BOOL pluginInitialized;
@property (nonatomic, assign, getter=isSDKInitialized) BOOL sdkInitialized;


// Banner Fields
// This is the Ad Unit or Placement that will display banner ads:
@property (strong) NSString* placementId;


@property (nonatomic, strong) UIView *safeAreaBackground;

// React Native's proposed optimizations to not emit events if no listeners
@property (nonatomic, assign) BOOL hasListeners;

@property (nonatomic, strong) ISBannerView      *bannerView;
@property (nonatomic, strong) ISPlacementInfo   *rvPlacementInfo;

@end
@implementation Ironsourceads

static NSString *const SDK_TAG = @"Ironsource　Sdk";
static NSString *const TAG = @"Ironsource Ads";

RCTResponseSenderBlock _onIronsourceAdsInitialized = nil;

static Ironsourceads *IronsourceShared; // Shared instance of this bridge module.

RCT_EXPORT_MODULE()

// `init` requires main queue b/c of UI code
+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

// Invoke all exported methods from main queue
- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

+ (Ironsourceads *)shared
{
    return IronsourceShared;
}

- (instancetype)init
{
    self = [super init];
    if ( self )
    {
        IronsourceShared = self;
    }
    return self;
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(isInitialized)
{
    return @([self isPluginInitialized] && [self isSDKInitialized]);
}

RCT_EXPORT_METHOD(initialize :(NSString *)sdkKey :(RCTResponseSenderBlock)callback)
{
    // Guard against running init logic multiple times
    if ( [self isPluginInitialized] )
    {
        callback(@[@" Unity Sdk has Initiallized"]);
        return;
    }
    
    self.pluginInitialized = YES;
    _onIronsourceAdsInitialized = callback;
    
    [ISSupersonicAdsConfiguration configurations].useClientSideCallbacks = @(YES);

    [IronSource setRewardedVideoDelegate:self];
    [IronSource setInterstitialDelegate:self];
    [IronSource setBannerDelegate:self];
    
    [IronSource initWithAppKey:sdkKey delegate:self];
    //[IronSource initWithAppKey:sdkKey adUnits:@[IS_REWARDED_VIDEO, IS_INTERSTITIAL, IS_BANNER] delegate:self];
    
    
}

#pragma mark - Interstitials

RCT_EXPORT_METHOD(loadInterstitial)
{
    [IronSource loadInterstitial];
    
}

RCT_EXPORT_METHOD(showRewardVideo)
{
    if ([IronSource hasRewardedVideo]) {
        [IronSource showRewardedVideoWithViewController:ROOT_VIEW_CONTROLLER];
    }
    
}

RCT_EXPORT_METHOD(loadBottomBanner:(NSString *)adUnitIdentifier)
{
    [IronSource loadBannerWithViewController:ROOT_VIEW_CONTROLLER size:ISBannerSize_BANNER placement:adUnitIdentifier];
    
}

RCT_EXPORT_METHOD(unLoadBottomBanner)
{
    [IronSource destroyBanner: _bannerView];
    
}

- (void)addBannerViewToBottomView: (UIView *)bannerView {
    bannerView.translatesAutoresizingMaskIntoConstraints = NO;
    [ROOT_VIEW_CONTROLLER.view addSubview:bannerView];
    [ROOT_VIEW_CONTROLLER.view addConstraints:@[
                               [NSLayoutConstraint constraintWithItem:bannerView
                                                            attribute:NSLayoutAttributeBottom
                                                            relatedBy:NSLayoutRelationEqual
                                                               toItem:ROOT_VIEW_CONTROLLER.bottomLayoutGuide
                                                            attribute:NSLayoutAttributeTop
                                                           multiplier:1
                                                             constant:0],
                               [NSLayoutConstraint constraintWithItem:bannerView
                                                            attribute:NSLayoutAttributeCenterX
                                                            relatedBy:NSLayoutRelationEqual
                                                               toItem:ROOT_VIEW_CONTROLLER.view
                                                            attribute:NSLayoutAttributeCenterX
                                                           multiplier:1
                                                             constant:0]
                               ]];
}

#pragma mark - Rewarded Video Delegate Functions

// This method lets you know whether or not there is a video
// ready to be presented. It is only after this method is invoked
// with 'hasAvailableAds' set to 'YES' that you can should 'showRV'.
- (void)rewardedVideoHasChangedAvailability:(BOOL)available {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    dispatch_async(dispatch_get_main_queue(), ^{
        if (available) {
            [IronSource showRewardedVideoWithViewController:ROOT_VIEW_CONTROLLER];
        }
    });
}

// This method gets invoked after the user has been rewarded.
- (void)didReceiveRewardForPlacement:(ISPlacementInfo *)placementInfo {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    self.rvPlacementInfo = placementInfo;
    [self sendReactNativeEventWithName: @"onDidReceiveRewardForPlacement" body: @{@"rewardName" : self.rvPlacementInfo.rewardName,@"rewardAmount" : self.rvPlacementInfo.rewardAmount}];
    
}

// This method gets invoked when there is a problem playing the video.
// If it does happen, check out 'error' for more information and consult
// our knowledge center for help.
- (void)rewardedVideoDidFailToShowWithError:(NSError *)error {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onRewardedVideoDidFailToShow" body: @{@"error" : error}];
    
}

// This method gets invoked when we take control, but before
// the video has started playing.
- (void)rewardedVideoDidOpen {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onRewardedVideoDidOpen" body: nil];
    
}

// This method gets invoked when we return controlback to your hands.
// We chose to notify you about rewards here and not in 'didReceiveRewardForPlacement'.
// This is because reward can occur in the middle of the video.
- (void)rewardedVideoDidClose {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    if (self.rvPlacementInfo) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Video Reward"
                                                        message:[NSString stringWithFormat:@"You have been rewarded %d %@", [self.rvPlacementInfo.rewardAmount intValue], self.rvPlacementInfo.rewardName]
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        
        [self sendReactNativeEventWithName: @"onRewardedVideoDidClose" body: nil];

        self.rvPlacementInfo = nil;
    }
}

// This method gets invoked when the video has started playing.
- (void)rewardedVideoDidStart {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onRewardedVideoDidStart" body: nil];
    
}

// This method gets invoked when the video has stopped playing.
- (void)rewardedVideoDidEnd {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onRewardedVideoDidEnd" body: nil];
    
}

// This method gets invoked after a video has been clicked
- (void)didClickRewardedVideo:(ISPlacementInfo *)placementInfo {
    //NSLog(@"%s", __PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onDidClickRewardedVideo" body: @{@"info" : placementInfo}];
    
}

#pragma mark - Interstitial Delegate Functions

- (void)interstitialDidLoad {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    dispatch_async(dispatch_get_main_queue(), ^{
        [IronSource showInterstitialWithViewController:ROOT_VIEW_CONTROLLER];
    });
}

- (void)interstitialDidFailToLoadWithError:(NSError *)error {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    dispatch_async(dispatch_get_main_queue(), ^{
        [self sendReactNativeEventWithName: @"onInterstitialDidFailToLoad" body: @{@"error" : error}];
    });
}

- (void)interstitialDidOpen {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onInterstitialDidOpen" body: nil];
    
}

// The method will be called each time the Interstitial windows has opened successfully.
- (void)interstitialDidShow {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onInterstitialDidShow" body: nil];
    
}

// This method gets invoked after a failed attempt to load Interstitial.
// If it does happen, check out 'error' for more information and consult our
// Knowledge center.
- (void)interstitialDidFailToShowWithError:(NSError *)error {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onInterstitialDidFailToShow" body: @{@"error" : error}];
    
}

// This method will be called each time the user had clicked the Interstitial ad.
- (void)didClickInterstitial {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onDidClickInterstitial" body: nil];
    
}

// This method get invoked after the Interstitial window had closed and control
// returns to your application.
- (void)interstitialDidClose {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onInterstitialDidClose" body: nil];
    
}

#pragma mark - Banner Delegate Functions

/**
 Called after a banner ad has been successfully loaded
 */
- (void)bannerDidLoad:(ISBannerView *)bannerView {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    dispatch_async(dispatch_get_main_queue(), ^{
        self.bannerView = bannerView;
        if (@available(iOS 11.0, *)) {
            [self.bannerView setCenter:CGPointMake(ROOT_VIEW_CONTROLLER.view.center.x,ROOT_VIEW_CONTROLLER.view.frame.size.height - (self.bannerView.frame.size.height/2.0) - ROOT_VIEW_CONTROLLER.view.safeAreaInsets.bottom)]; // safeAreaInsets is available from iOS 11.0
        } else {
            [self.bannerView setCenter:CGPointMake(ROOT_VIEW_CONTROLLER.view.center.x,ROOT_VIEW_CONTROLLER.view.frame.size.height - (self.bannerView.frame.size.height/2.0))];
        }
        [ROOT_VIEW_CONTROLLER.view addSubview:self.bannerView];
        [self sendReactNativeEventWithName: @"onBannerDidLoad" body: nil];
        
    });
}

/**
 Called after a banner has attempted to load an ad but failed.
  @param error The reason for the error
 */
- (void)bannerDidFailToLoadWithError:(NSError *)error {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onBannerDidFailToLoad" body: @{@"error" : error}];
    
}

/**
 Called after a banner has been clicked.
 */
- (void)didClickBanner {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onDidClickBanner" body: nil];
    
}

/**
 Called when a banner is about to present a full screen content.
 */
- (void)bannerWillPresentScreen {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onBannerWillPresentScreen" body: nil];
    
}

/**
 Called after a full screen content has been dismissed.
 */
- (void)bannerDidDismissScreen {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onBannerDidDismissScreen" body: nil];
    
}

/**
 Called when a user would be taken out of the application context.
 */
- (void)bannerWillLeaveApplication {
    //NSLog(@"%s",__PRETTY_FUNCTION__);
    [self sendReactNativeEventWithName: @"onBannerWillLeaveApplication" body: nil];
    
}

#pragma mark -ISInitializationDelegate
// Invoked after init mediation completed
- (void)initializationDidComplete {
    self.sdkInitialized = YES;
    _onIronsourceAdsInitialized(@[@" success"]);
}

#pragma mark - React Native Event Bridge

- (void)sendReactNativeEventWithName:(NSString *)name body:(NSDictionary<NSString *, id> *)body
{
    [self sendEventWithName: name body: body];
}

// From RCTBridgeModule protocol
- (NSArray<NSString *> *)supportedEvents
{
    return @[@"onInterstitialDidFailToLoad",
             @"onInterstitialDidFailToShow",
             
             @"onInterstitialDidOpen",
             @"onInterstitialDidShow",
             @"onDidClickInterstitial",
             @"onInterstitialDidClose",
             
             @"onRewardedVideoDidFailToShow",
             @"onRewardedVideoDidOpen",
             @"onRewardedVideoDidClose",
             @"onRewardedVideoDidStart",
             @"onDidReceiveRewardForPlacement",
             
             @"onRewardedVideoDidEnd",
             @"onDidClickRewardedVideo",
             @"onBannerDidFailToLoad",
             @"onBannerDidLoad",
             @"onDidClickBanner",
             @"onBannerWillPresentScreen",
             @"onBannerDidDismissScreen",
             @"onBannerWillLeaveApplication",];
}

- (void)startObserving
{
    self.hasListeners = YES;
}

- (void)stopObserving
{
    self.hasListeners = NO;
}

@end
