declare namespace Tracking {
    /** 初始化参数 */
    type initParameters = {
        appKey?: string
        channelId?: string
        oaid?: string
        assetFileName?: string
        oaidLibraryString?: string
        caid?: string
        caid2?: string
        ASAEnabled?: boolean
    }

    /**
     * 在开发环境中进行Debug测试，日志TAG为Tracking
     * 注：热云SDK所有API接口的http response均以status：0表示成功
     * @example Tracking.setDebugMode(true);
     */
    function setDebugMode(enabled: boolean): void;

    /**
     * 获取 ANDROID_ID
     */
    function getAndroidId(): Promise<string>;

    /**
     * 如果开发者没有自己的用户系统，希望使用用户设备ID作为accountId，直接调用Tracking.getDeviceId()方法获取设备ID即可。
     * 该方法一定要在调用初始化接口之后间隔5s以上再使用，否则会影响取值。
     * @example const uuid = await Tracking.getDeviceId();
     */
    function getDeviceId(): Promise<string>;
    
    /**
     * 获取 oaid (ios 为 idfa)
     * @example const uuid = await Tracking.getOAID();
     */
    function getOAID(): Promise<string>;

    /**
     * 获取 idfv (ios Only)
     * @example const uuid = await Tracking.getOAID();
     */
    function getIDFV(): Promise<string>;

    /**
     * 获取 ASAToken (ios Only)
     * @example const uuid = await Tracking.getOAID();
     */
    function getASAToken(): Promise<string>;

    /**
     * 初始化内置Oaid sdk，为了应对有个性化推荐弹窗的手机，提前先调用一遍oaid sdk，延后调用热云sdk的初始化。
     * @param params 
     * @example Tracking.initWithKeyAndChannelId({channelId: '_default_'});
     */
    function initOaidSdk(onSuccess?: (oaid: string)=> void, onError?: (err: string) => void): void;

    /**
     * 应用启动后，初始化热云 SDK，报送应用安装或启动事件
     * @param params 
     * @example Tracking.initWithKeyAndChannelId({channelId: '_default_'});
     */
    function initWithKeyAndChannelId(params: initParameters, onSuccess?: ()=> void, onError?: (err: string) => void): void;

    /**
     * 在用户注册完成时调用Tracking.setRegisterWithAccountID方法。
     * @example Tracking.setRegisterWithAccountID('123456');
     */
    function setRegisterWithAccountID(accountId: string): void;

    /**
     * 用户登录完成、切换账号时，报送应用登录事件
     * @param accountId 
     * @example Tracking.setLoginSuccessBusiness('123456');
     */
    function setLoginSuccessBusiness(accountId: string): void;

    /**
     * 用于用户充值成功，统计充值数据，所有付费相关分析的数据报表均依赖此方法。
     * 用户充值成功且后端发货成功后调用Tracking.setPayment方法。
     * @param transactionId 付费数据按此参数排重，交易流水号，请务必确保唯一。
     * @param paymentType 支付类型，例如支付宝(alipay)，银联(unionpay)，微信支付（weixinpay）,易宝支付（yeepay），paymentType不能填写：FREE（FREE不统计付费）
     * @param currencyType 货币类型，按照国际标准组织ISO 4217中规范的3位字母，例如CNY人民币、USD美金等
     * @param currencyAmount 支付的真实货币金额，人民币单位：元
     * @example Tracking.setPayment('0062001242', 'alipay', 'CNY', 1000);
     */
    function setPayment(transactionId: string, paymentType: string, currencyType: string, currencyAmount: number): void;

    /**
     * 用于统计用户在应用内的任意行为，如打开某个面板、点击某个Button、参与某个活动等
     * @param eventName 自定义事件名称，必须为event_1到event_30
     * @param extra 自定义属性key只能为string类型，名称为param1-param10，value支持字符串、数字。
     * @example Tracking.setEvent('event_1', { param1: 'value1', param2: 'value2' });
     */
    function setEvent(eventName: string, extra?: Object): void;

    /**
     * 当订单产生成功时调用此方法进行事件上报
     * @param transactionId 交易流水号，请确保唯一。
     * @param currencyType 货币类型，按照国际标准组织ISO 4217中规范的3位字母，例如CNY人民币、USD美金等
     * @param currencyAmount 支付的真实货币金额，人民币单位：元
     * @example Tracking.setOrder('f93182bc6', 'CNY', 9);
     */
    function setOrder(transactionId: string, currencyType: string, currencyAmount: number): void;

    /**
     * 当App内的变现广告被展示时调用此方法进行事件上报
     * @param adPlatform 填充广告的变现平台(传括号中的值)：穿山甲(csj)、优量汇(ylh)、百青藤(bqt)、快手(ks)、Sigmob(sigmob)、Mintegral(mintegral)、OneWay(oneway)、Vungle(vungle)、Facebook(facebook)、AdMob(admob)、UnityAds(unity)、IronSource(is)、AdTiming(adtiming)、游可赢(klein)
     * @param adId 填充广告在变现平台的广告位ID。注意：若您使用聚合平台且需要通过聚合平台API获取收入时，需传聚合平台对应的值，TopOn传“adsource_id”；AdTiming传“placementId”；TradPlus传“placementId”；GroMore传“ad_unit_id”
     * @param fill 本次展示广告是否填充成功，1成功、2失败，无法确定填充是否成功时，请传1
     * @example Tracking.setAdShow('csj', 'adid123456', '1');
     */
    function setAdShow(adPlatform: string, adId: string, fill: string): void;

    /**
     * 当App内的变现广告被点击时调用此方法进行事件上报。
     * @param adPlatform 填充广告的变现平台(传括号中的值)：穿山甲(csj)、优量汇(ylh)、百青藤(bqt)、快手(ks)、Sigmob(sigmob)、Mintegral(mintegral)、OneWay(oneway)、Vungle(vungle)、Facebook(facebook)、AdMob(admob)、UnityAds(unity)、IronSource(is)、AdTiming(adtiming)、游可赢(klein)
     * @param adId 填充广告在变现平台的广告位ID。注意：若您使用聚合平台且需要通过聚合平台API获取收入时，需传聚合平台对应的值，TopOn传“adsource_id”；AdTiming传“placementId”；TradPlus传“placementId”；GroMore传“ad_unit_id”
     * @example Tracking.setAdClick('csj', 'adid123456');
     */
    function setAdClick(adPlatform: string, adId: string): void;

    /**
     * 用于统计App的使用时长, 当App退出时进行调用
     * @param duration App使用时长，单位：毫秒
     * @example Tracking.setAppDuration(3);
     */
    function setAppDuration(duration: number): void;

    /**
     * 统计App的页面停留时长
     * @param activityName 页面名称或ID，请确保唯一性，推荐使用包名+页面名
     * @param duration App使用时长，单位：毫秒
     * @example Tracking.setPageDuration('com.test.MainActivity', 3);
     */
    function setPageDuration(activityName: string, duration: number): void;

    /**
     * 应用退出时释放sdk占用资源。
     * @example Tracking.exitSdk();
     */
    function exitSdk(): void;
}