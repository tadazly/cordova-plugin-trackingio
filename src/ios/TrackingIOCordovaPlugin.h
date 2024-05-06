#import <Cordova/CDV.h>

@interface TrackingIOCordovaPlugin : CDVPlugin

@property (nonatomic, strong, nullable) NSString *IDFA;

- (void)getIDFV:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getASAToken:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setDebugMode:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getDeviceId:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getOAID:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)initOaidSdk:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)initWithKeyAndChannelId:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setRegisterWithAccountID:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setLoginSuccessBusiness:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setPayment:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setEvent:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setOrder:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setAdShow:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setAdClick:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setAppDuration:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)setPageDuration:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)exitSdk:(CDVInvokedUrlCommand *_Nonnull)command;

@end
