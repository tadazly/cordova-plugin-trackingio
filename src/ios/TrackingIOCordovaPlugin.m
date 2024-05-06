#import "TrackingIOCordovaPlugin.h"
#import <AppTrackingTransparency/AppTrackingTransparency.h>
#import <AdSupport/ASIdentifierManager.h>
#import <AdServices/AAAttribution.h>
#import "Tracking.h"

@implementation TrackingIOCordovaPlugin

- (void)getIDFV:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *idfv = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:idfv];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)getASAToken:(CDVInvokedUrlCommand *_Nonnull)command
{
    if (@available(iOS 14.3, *)) {
        NSError *error = nil;
        NSString *attributionToken = [AAAttribution attributionTokenWithError:&error];
        
        if (attributionToken) {
            // 成功获取到归因信息
            NSLog(@"Attribution Token: %@", attributionToken);
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:attributionToken];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        } else {
            // 获取归因信息失败，可以查看错误信息
            NSLog(@"Failed to get attribution token. Error: %@", error);
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"unknown"];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }
    } else {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"unknown"];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }
}

- (void)setDebugMode:(CDVInvokedUrlCommand *_Nonnull)command
{
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    [Tracking setPrintLog:enabled];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)getDeviceId:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *deviceId = [Tracking getDeviceId];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:deviceId];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)getOAID:(CDVInvokedUrlCommand *_Nonnull)command
{
    if (!self.IDFA) {
        NSLog(@"尚未初始化");
        self.IDFA = @"unknown";
    }
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:self.IDFA];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)initOaidSdk:(CDVInvokedUrlCommand *_Nonnull)command
{
    if (@available(iOS 14, *)) {
        // iOS14及以上版本需要先请求权限
        [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) {
            // 获取到权限后，依然使用老方法获取idfa
            if (status == ATTrackingManagerAuthorizationStatusAuthorized) {
                NSString *idfa = [[ASIdentifierManager sharedManager].advertisingIdentifier UUIDString];
                NSLog(@"%@",idfa);
                self.IDFA = idfa;
            } else {
                NSLog(@"请在设置-隐私-跟踪中允许App请求跟踪");
                self.IDFA = @"unknown";
            }
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:self.IDFA];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }];
    } else {
        // iOS14以下版本依然使用老方法
        // 判断在设置-隐私里用户是否打开了广告跟踪
        if ([[ASIdentifierManager sharedManager] isAdvertisingTrackingEnabled]) {
            NSString *idfa = [[ASIdentifierManager sharedManager].advertisingIdentifier UUIDString];
            NSLog(@"%@",idfa);
            self.IDFA = idfa;
        } else {
            NSLog(@"请在设置-隐私-广告中打开广告跟踪功能");
            self.IDFA = @"unknown";
        }
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:self.IDFA];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }
}

- (void)initWithKeyAndChannelId:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSObject *obj = [command.arguments objectAtIndex:0];
    NSString *appKey = [obj valueForKey:@"appKey"];
    NSString *channelId = [obj valueForKey:@"channelId"];
    NSString *caid = [obj valueForKey:@"caid"];
    NSString *caid2 = [obj valueForKey:@"caid2"];
    BOOL ASAEnabled = [[obj valueForKey:@"ASAEnabled"] boolValue];
    if (!appKey) appKey = [[self.commandDelegate settings] objectForKey:@"trackingio_appkey"];
    if (!channelId) channelId = @"_default_";
    NSLog(@"appKey %@",appKey);
    NSLog(@"channelId %@",channelId);
    NSLog(@"caid %@",caid);
    NSLog(@"caid2 %@",caid2);
    NSLog(@"ASAEnabled %@", [obj valueForKey:@"ASAEnabled"]);
    if (@available(iOS 14, *)) {
        // iOS14及以上版本需要先请求权限
        [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) {
            // 获取到权限后，依然使用老方法获取idfa
            if (status == ATTrackingManagerAuthorizationStatusAuthorized) {
                NSString *idfa = [[ASIdentifierManager sharedManager].advertisingIdentifier UUIDString];
                NSLog(@"%@",idfa);
                self.IDFA = idfa;
            } else {
                NSLog(@"请在设置-隐私-跟踪中允许App请求跟踪");
                self.IDFA = @"unknown";
            }
            //如需投放ASA，请在初始化接口之前调用：
            if (ASAEnabled) {
                [Tracking setASAEnable:ASAEnabled];
            }
            //无论是否获取到idfa 在收到系统回调函数后调用热云SDK init函数
            [Tracking initWithAppKey:appKey withChannelId:channelId withCAID:caid withCAID2:caid2 withMobDNAOid:nil withParams:nil withStartupParams:nil];
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }];
    } else {
        // iOS14以下版本依然使用老方法
        // 判断在设置-隐私里用户是否打开了广告跟踪
        if ([[ASIdentifierManager sharedManager] isAdvertisingTrackingEnabled]) {
            NSString *idfa = [[ASIdentifierManager sharedManager].advertisingIdentifier UUIDString];
            NSLog(@"%@",idfa);
            self.IDFA = idfa;
        } else {
            NSLog(@"请在设置-隐私-广告中打开广告跟踪功能");
            self.IDFA = @"unknown";
        }
        //如需投放ASA，请在初始化接口之前调用：
        if (ASAEnabled) {
            [Tracking setASAEnable:ASAEnabled];
        }
        //无论是否获取到idfa 直接调用热云SDK init函数
        [Tracking initWithAppKey:appKey withChannelId:channelId withCAID:caid withCAID2:caid2 withMobDNAOid:nil withParams:nil withStartupParams:nil];
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }
}

- (void)setRegisterWithAccountID:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *accountId = [[command.arguments objectAtIndex:0] stringValue];
    [Tracking setRegisterWithAccountID:accountId withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setLoginSuccessBusiness:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *accountId = [[command.arguments objectAtIndex:0] stringValue];
    [Tracking setLoginWithAccountID:accountId withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setPayment:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *transactionId = [[command.arguments objectAtIndex:0] stringValue];
    NSString *paymentType = [[command.arguments objectAtIndex:1] stringValue];
    NSString *currencyType = [[command.arguments objectAtIndex:1] stringValue];
    float currencyAmount = [[command.arguments objectAtIndex:3] floatValue];
    [Tracking setRyzf:transactionId ryzfType:paymentType hbType:currencyType hbAmount:currencyAmount withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setEvent:(CDVInvokedUrlCommand *_Nonnull)command
{
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setOrder:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *paymentType = [[command.arguments objectAtIndex:0] stringValue];
    NSString *currencyType = [[command.arguments objectAtIndex:1] stringValue];
    float currencyAmount = [[command.arguments objectAtIndex:2] floatValue];
    [Tracking setDD:paymentType hbType:currencyType hbAmount:currencyAmount withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setAdShow:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *adPlatform = [[command.arguments objectAtIndex:0] stringValue];
    NSString *adId = [[command.arguments objectAtIndex:1] stringValue];
    Boolean fill = [[command.arguments objectAtIndex:2] boolValue];
    [Tracking onAdShow:adPlatform adId:adId isSuccess:fill withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setAdClick:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *adPlatform = [[command.arguments objectAtIndex:0] stringValue];
    NSString *adId = [[command.arguments objectAtIndex:1] stringValue];
    [Tracking onAdClick:adPlatform adId:adId withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setAppDuration:(CDVInvokedUrlCommand *_Nonnull)command
{
    long duration = [[command.arguments objectAtIndex:0] longValue];
    [Tracking setTrackAppDuration:duration withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setPageDuration:(CDVInvokedUrlCommand *_Nonnull)command
{
    NSString *pageId = [[command.arguments objectAtIndex:0] stringValue];
    long duration = [[command.arguments objectAtIndex:1] longValue];
    [Tracking trackViewName:pageId duration:duration withParams:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)exitSdk:(CDVInvokedUrlCommand *_Nonnull)command
{
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}


@end
