# 本项目尚未完成，敬请期待（Caution：Under Construction）

# cordova-plugin-trackingio

## 一、说明

### Cordova项目接入热云SDK（TrackingIO 2.0）

热云是一款移动应用数据分析平台，通过集成热云 SDK，开发者可以统计应用的启动、注册、登录、充值、事件等关键数据，以便进行数据分析和业务优化。

### TrackingIO SDK版本
- [Android SDK      v1.9.2](http://newdoc.trackingio.com/AndroidSDK.html)
- iOS SDK          待定

## 二、接入流程
### 1.在项目中安装插件

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
    - 参照[SDK文档添加配置](http://newdoc.trackingio.com/AndroidSDK.html#12%E5%9C%A8%E5%BA%94%E7%94%A8%E7%9A%84buildgradle%E4%B8%8B%E5%A2%9E%E5%8A%A0%E5%A6%82%E4%B8%8B%E9%85%8D%E7%BD%AE)
    
        1. 待定
        2. 待定

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

### 5.获取设备ID
如果开发者没有自己的用户系统，希望使用用户设备ID作为accountId，直接调用Tracking.getDeviceId()方法获取设备ID即可。

该方法一定要在调用初始化接口之后间隔5s以上再使用，否则会影响取值。

``` typescript
const uuid = await Tracking.getDeviceId();
```


## 三、API使用说明

### 1.[初始化热云SDK](http://newdoc.trackingio.com/AndroidSDK.html#2%E5%88%9D%E5%A7%8B%E5%8C%96%E7%83%AD%E4%BA%91sdk)

初始化参数说明：appKey默认使用安装插件时的TRACKINGIO_APPKEY参数，插件使用1.0.25版本的oaid sdk，理论上不用传入任何与oaid相关的参数，待测试。
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
    channelId: 'test',
}
Tracking.initWithKeyAndChannelId(initParams);
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

``` typescript
Tracking.setAppDuration(3);
```

### 10.[统计app页面停留时长事件](http://newdoc.trackingio.com/AndroidSDK.html#11%E7%BB%9F%E8%AE%A1app%E9%A1%B5%E9%9D%A2%E5%81%9C%E7%95%99%E6%97%B6%E9%95%BF%E4%BA%8B%E4%BB%B6)

``` typescript
Tracking.setPageDuration('module.HappyNewYearActivity', 3);
```

### 11.[退出sdk](http://newdoc.trackingio.com/AndroidSDK.html#12%E9%80%80%E5%87%BAsdk)

``` typescript
Tracking.exitSdk();
```

## 技术支持
如有任何问题，请及时联系[热云的技术支持工程师](http://newdoc.trackingio.com/AndroidSDK.html#%E6%8A%80%E6%9C%AF%E6%94%AF%E6%8C%81) ：）