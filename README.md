# Cordova热云SDK插件

# cordova-plugin-trackingio

## 一、说明

### Cordova项目接入热云SDK（TrackingIO 2.0）

热云是一款移动应用数据分析平台，通过集成热云 SDK，开发者可以统计应用的启动、注册、登录、充值、事件等关键数据，以便进行数据分析和业务优化。

### SDK版本
- Android
    - [Android_SDK v1.9.2](http://newdoc.trackingio.com/AndroidSDK.html)
    - OAID SDK v1.0.25 来源大佬：[多看书_ + Android OAID 获取 基于MSA oaid_sdk_1.0.25.zip](https://www.jianshu.com/p/748df2fddc9a)
    - 相关权限请参考TrackingIO SDK，在调用前确保权限已经申请。
- iOS 
    - [iOS_SDK v1.9.14](http://newdoc.trackingio.com/iOSSDK.html)

## 二、接入流程
### 1.在项目中安装插件

- android安装前必看 ！
    - 项目中使用了oaid sdk 1.0.25，他的minSdkVersion是21
    - 如果你的Cordova项目符合这个要求，以下几种方式都适合您，请放心安装～
    - 如果你的Cordova项目正好和我的一样不符合这个要求，比如我的是19，那么请先将本项目clone下来，然后将plugin.xml中注释包围的几行取消注释，最后再使用第3种方式（通过本地路径安装），安装命令后面接上 --force

    ``` shell
    cordova plugin add /local/path/to/cordova-plugin-trackingio --variable TRACKINGIO_APPKEY=IX4BGYYG8L4L --force
    ```

- 安装时使用的 TRACKINGIO_APPKEY 需要在[热云产品中心创建](http://newdoc.trackingio.com/AndroidSDK.html#1%E7%94%B3%E8%AF%B7appkey)

1. 通过npm安装
``` shell
cordova plugin add cordova-plugin-trackingio --variable TRACKINGIO_APPKEY=IX4BGYYG8L4L
```

2. 通过git链接安装
``` shell
cordova plugin add https://github.com/tadazly/cordova-plugin-trackingio.git --variable TRACKINGIO_APPKEY=IX4BGYYG8L4L
```

3. 通过本地路径安装
``` shell
cordova plugin add /local/path/to/cordova-plugin-trackingio --variable TRACKINGIO_APPKEY=IX4BGYYG8L4L
```

- 本地插件调试开发（改插件代码时可以用）

首先将插件clone到本地，然后使用本地路径方式安装并传入 --link 参数，会将插件目录中的代码链接至项目
``` shell
cordova plugin add /local/path/to/cordova-plugin-trackingio --variable TRACKINGIO_APPKEY=IX4BGYYG8L4L --link
```

### 2.项目配置
- Android
    - 无需额外配置
- iOS
    - 关闭bitcode: 选择⼯程-> Build Settings -> 搜索bitcode ->设置为NO
    - （重要）AdServices.framework 以Optional形式引入
        
        Build Phases -> AdServices.framework Status: Optional

### 3.使用方式

插件对象可以在js代码中使用[Tracking](https://github.com/tadazly/cordova-plugin-trackingio/blob/main/plugin.xml#L14)或者[cordova.plugins.Tracking](https://github.com/tadazly/cordova-plugin-trackingio/blob/main/plugin.xml#L15)调用

### 4.测试

- 在开发环境中进行Debug测试，日志TAG为Tracking：

``` typescript
Tracking.setDebugMode(true);
```

注：热云SDK所有API接口的http response均以status：0表示成功。

- 进入热云调试页面查看调试数据：

    “全部产品按钮” - “待调试产品”–“调试”

## 三、通用说明
### 1.获取设备ID
如果开发者没有自己的用户系统，希望使用用户设备ID作为accountId，直接调用Tracking.getDeviceId()方法获取设备ID即可。

该方法一定要在调用初始化接口之后间隔5s以上再使用，否则会影响取值。

``` typescript
const uuid = await Tracking.getDeviceId();
```

### 2.通过后台来统计
如果你的项目后台很好说话，他愿意帮助你来接热云，那么谢谢他，我们只需要初始化sdk，然后在用户注册、登录的时候将设备id和oaid提供给后台就行，剩下的统计全部交给后台。
``` typescript
// onDeviceReady
const initParams: initParameters = {
    // 你的配置...
}
Tracking.initWithKeyAndChannelId(
    initParams,
    () => {
        // onSuccess
        console.log('TrackingIO 初始化成功');
        // 据热云sdk描述，需要隔5s再取...
        setTimeout(() => {
            Tracking.getDeviceId().then(deviceId => {
                console.log('TrackingIO deviceId: ' + deviceId);
            });
            Tracking.getOAID().then(oaid => {
                console.log('TrackingIO oaid: ' + oaid);
            });
        }, 5000);
    },
    () => {
        // onFailed
        console.error('TrackingIO 初始化失败');
    }
);
```

### 3.OAID获取不到的情况
- 返回 unknown

    经不严谨测试，模拟器在调用oaid初始化( MdidSdkHelper.InitSdk )方法后，走不进回调函数 :(  
    
    这种情况下，默认返回 'unknown'，如果想抛出错误，请手动修改TrackingIOCordovaPlugin.java中的getOAID方法。

- 返回 00000000000000000000000000000000
    
    部分手机会在初次调用sdk时提示个性化推荐服务权限，在没有获得权限前或者被拒绝后会返回。
    
    解决办法参考 [EXTRA] 部分，首先在app获取权限后先调用Tracking.initOaidSdk()，然后再在登陆你的用户系统前调用Tracking.initWithKeyAndChannelId(...)确保热云sdk在初始化的时候能够正确拿到oaid。


### 4.onDestroy没有调用情况
- 本插件会在onDestroy时判断是否已经手动调用过统计时长和退出SDK的api，如果没有调用过，自动帮你调用。
- 正常情况下，在app结束时，PluginManager会调用所有被使用过的Cordova插件的onDestroy方法。
- 如果没有正常调用，检查是否有别的插件在onDestroy里做了杀进程的操作，比如某个插件在onDestroy里写了android.os.Process.killProcess(android.os.Process.myPid());

## 四、API使用说明

### 1.[初始化热云SDK](http://newdoc.trackingio.com/AndroidSDK.html#2%E5%88%9D%E5%A7%8B%E5%8C%96%E7%83%AD%E4%BA%91sdk)

初始化参数说明：
- appKey默认使用安装插件时的TRACKINGIO_APPKEY参数，不推荐从js传入。
- 插件提供1.0.25版本的oaid sdk，理论上不用传入任何与oaid相关的参数。
``` typescript
type initParameters = {
    appKey?: string
    channelId?: string
    oaid?: string
    assetFileName?: string
    oaidLibraryString?: string
}
```

``` typescript
const initParams: initParameters = {
    // 你的配置...
    channelId: 'test',
};
Tracking.initWithKeyAndChannelId(
    initParams,
    () => {/** on Success **/},
    (err) => {/** on Error **/}
);
```
#### 1.1 获取设备ID
该方法一定要在调用初始化接口之后间隔5s以上再使用，否则会影响取值。
``` typescript
const deviceId = await Tracking.getDeviceId();
```
#### 1.2 获取OAID/IDFA
- iOS通过该接口获取idfa，需要先调用initWithKeyAndChannelId或者initOaidSdk再调用getOAID。
- Android如果初始化参数填入了oaid，直接返回提供的oaid。
- 如果没有提供，会自动调用插件自带的oaid sdk去获取。
    - 注意：模拟器和 androidTarget < 29 获取不到这该死的oaid，返回 'unknown'
``` typescript
const oaid = await Tracking.getOAID();
```

### 2.[统计用户注册数据](http://newdoc.trackingio.com/AndroidSDK.html#3%E7%BB%9F%E8%AE%A1%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C%E6%95%B0%E6%8D%AE)

``` typescript
Tracking.setRegisterWithAccountID('123456');
```

### 3.[统计用户登录数据](http://newdoc.trackingio.com/AndroidSDK.html#4%E7%BB%9F%E8%AE%A1%E7%94%A8%E6%88%B7%E7%99%BB%E5%BD%95%E6%95%B0%E6%8D%AE)

``` typescript
Tracking.setLoginSuccessBusiness('123456');
```

### 4.[统计用户充值成功数据（建议使用服务器REST报送）](http://newdoc.trackingio.com/AndroidSDK.html#5%E7%BB%9F%E8%AE%A1%E7%94%A8%E6%88%B7%E5%85%85%E5%80%BC%E6%88%90%E5%8A%9F%E6%95%B0%E6%8D%AE%E5%BB%BA%E8%AE%AE%E4%BD%BF%E7%94%A8%E6%9C%8D%E5%8A%A1%E5%99%A8rest%E6%8A%A5%E9%80%81)

``` typescript
Tracking.setPayment('0062001242', 'alipay', 'CNY', 1000);
```

### 5.[统计用户自定义事件](http://newdoc.trackingio.com/AndroidSDK.html#6%E7%BB%9F%E8%AE%A1%E7%94%A8%E6%88%B7%E8%87%AA%E5%AE%9A%E4%B9%89%E4%BA%8B%E4%BB%B6)

``` typescript
Tracking.setEvent('event_1', { param1: 'value1', param2: 'value2' });
```

### 6.[上报订单事件](http://newdoc.trackingio.com/AndroidSDK.html#7%E4%B8%8A%E6%8A%A5%E8%AE%A2%E5%8D%95%E4%BA%8B%E4%BB%B6)

``` typescript
Tracking.setOrder('f93182bc6', 'CNY', 9);
```

### 7.[统计广告展示事件](http://newdoc.trackingio.com/AndroidSDK.html#8%E7%BB%9F%E8%AE%A1%E5%B9%BF%E5%91%8A%E5%B1%95%E7%A4%BA%E4%BA%8B%E4%BB%B6)

``` typescript
Tracking.setAdShow('csj', 'adid123456', '1');
```

### 8.[统计广告点击事件](http://newdoc.trackingio.com/AndroidSDK.html#9%E7%BB%9F%E8%AE%A1%E5%B9%BF%E5%91%8A%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6)

``` typescript
Tracking.setAdClick('csj', 'adid123456');
```

### 9.[统计app使用时长事件](http://newdoc.trackingio.com/AndroidSDK.html#10%E7%BB%9F%E8%AE%A1app%E4%BD%BF%E7%94%A8%E6%97%B6%E9%95%BF%E4%BA%8B%E4%BB%B6)
如果没有调用，那么在app关闭时会自动调用。

``` typescript
Tracking.setAppDuration(3);
```

### 10.[统计app页面停留时长事件](http://newdoc.trackingio.com/AndroidSDK.html#11%E7%BB%9F%E8%AE%A1app%E9%A1%B5%E9%9D%A2%E5%81%9C%E7%95%99%E6%97%B6%E9%95%BF%E4%BA%8B%E4%BB%B6)

``` typescript
Tracking.setPageDuration('module.HappyNewYearActivity', 3);
```

### 11.[退出sdk](http://newdoc.trackingio.com/AndroidSDK.html#12%E9%80%80%E5%87%BAsdk)
如果没有调用，那么在app关闭时会自动调用。(ios不需要调用)

``` typescript
Tracking.exitSdk();
```

## Extra: 先初始化oaid sdk，延后初始化热云sdk
#### 以下内容是基于你使用本插件自带的oaid sdk来获取oaid的情况来说明。如果你自己可以获得oaid，那么请直接在initWithKeyAndChannelId的初始化参数中设置吧，后面和你没关系。
部分手机可能存在调用oaid sdk时会弹出一个让用户选择是否同意个性化推荐服务的弹窗，此时用户没有操作也会立即走入oaid sdk初始化完成的回调函数，并返回support=true和oaid=000000000000000，建议第一次打开app时先调用这个接口让用户做选择，延后调用热云sdk的初始化。
``` typescript
Tracking.initOaidSdk(oaid=>{
    console.log(oaid);
});
```
举个例子：
#### 初次启动app流程
1. 弹出你的请求权限弹窗（给我XXX权限，blabla...）[拒绝][同意]
2. 用户点击[同意]，此时我们调用 Tracking.initOaidSdk，并再弹出一个需要用户交互的弹窗（Hello，blabla...）[确认]
    - 此时如果系统弹窗了，那么用户肯定会先点击系统的弹窗。
    - 没弹窗是最好的了。
    - 此时无论与否，用户已经对是否开启个性化推荐功能完成了选择。
3. 接下来用户只有一个选择，点击你的[确认]按钮，接下来就可以进入你的正常app流程了。
#### 正常启动app流程
1. 判断是否获得权限。
2. 如果有READ_PHONE_STATE权限，则正式调用 Tracking.initWithKeyAndChannelId(...) 初始化热云。
3. 接下来做你爱做的事吧。

## 五、iOS使用说明
iOS的所有接口都与Android保持一致，initParameters可选appKey、channelId、caid、caid2。

idfa可以通过getOAID获得，例：
``` typescript
Tracking.initWithKeyAndChannelId(
    {},
    () => {
        /** on Success **/
        setTimeout(async () => {
            const deviceId = await Tracking.getDeviceId();
            const idfa = await Tracking.getOAID();
            console.log(`deviceId: ${deviceId}, idfa: ${idfa}`);
            /** 接下来可以把deviceId与idfa传给后端开发，让后端开发来调用其余api，比如登录、支付等 **/
        }, 5000);
    },
    (err) => {/** on Error **/}
);
```

## 技术支持
如有任何问题，请及时联系[热云的技术支持工程师](http://newdoc.trackingio.com/AndroidSDK.html#%E6%8A%80%E6%9C%AF%E6%94%AF%E6%8C%81) ：）