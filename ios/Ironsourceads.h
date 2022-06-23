#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTConvert.h>
#import <IronSource/IronSource.h>

#define KEY_WINDOW [UIApplication sharedApplication].keyWindow
#define DEVICE_SPECIFIC_ADVIEW_AD_FORMAT ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) ? MAAdFormat.leader : MAAdFormat.banner

NS_ASSUME_NONNULL_BEGIN

/**
 * The primary bridge between JS <-> native code for the Unity ads React Native module.
 */
@interface Ironsourceads : RCTEventEmitter<RCTBridgeModule, ISRewardedVideoDelegate ,ISInterstitialDelegate,ISBannerDelegate,ISInitializationDelegate>

/**
 * Shared instance of this bridge module.
 */
@property (nonatomic, strong, readonly, class) Ironsourceads *shared;

@end

NS_ASSUME_NONNULL_END
