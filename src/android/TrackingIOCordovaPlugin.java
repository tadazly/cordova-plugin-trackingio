package com.tadazly.trackingio;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.util.Log;

/** 热云sdk */
import com.reyun.tracking.sdk.InitParameters;
import com.reyun.tracking.sdk.Tracking;

/** oaid sdk */
import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TrackingIOCordovaPlugin extends CordovaPlugin {
    public final static String LOG_TAG = "plugin.TrackingIO";
    private static String APP_KEY;
    private static String OAID;
    private Activity yourApp;
    private long appStartTime;

    // oaid sdk 回调函数超时定时器
    private Handler oaidTimeoutHandler;
    // 用于标记是否初始化过Sdk
    private static boolean hasInitSdk = false;
    // 用于标记退出时调用的两个api，防止重复调用
    private boolean hasCallSetAppDuration = false;
    private boolean hasCallExitSdk = false;

    public Map<String, Object> jsonObjectToMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        // 迭代JSONObject中的键并将其添加到Map中
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = jsonObject.get(key);
            map.put(key, value);
        }
        return map;
    }

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        // 从配置中获取appKey，参考对应的plugin.xml文件
        APP_KEY = webView.getPreferences().getString("TRACKINGIO_APPKEY", "");
        yourApp = cordova.getActivity();
        this.appStartTime = System.currentTimeMillis();
        Log.d(LOG_TAG, "TrackingIO Cordova Plugin initialize");
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, "TrackingIO Action:" + action);
        if (action.equals("setDebugMode")) {
            return this.setDebugMode(args, callbackContext);
        }
        else if (action.equals("initWithKeyAndChannelId")) {
            return this.initWithKeyAndChannelId(args, callbackContext);
        }
        if (!hasInitSdk) {
            Log.e(LOG_TAG, "TrackingIO SDK not Init !");
            callbackContext.error("TrackingIO SDK not Init ! Please Call initWithKeyAndChannelId First !");
            return true;
        }
        if (action.equals("getDeviceId")) {
            return this.getDeviceId(args, callbackContext);
        }
        else if (action.equals("getOAID")) {
            return this.getOAID(args, callbackContext);
        }
        else if (action.equals("setRegisterWithAccountID")) {
            return this.setRegisterWithAccountID(args, callbackContext);
        }
        else if (action.equals("setLoginSuccessBusiness")) {
            return this.setLoginSuccessBusiness(args, callbackContext);
        }
        else if (action.equals("setPayment")) {
            return this.setPayment(args, callbackContext);
        }
        else if (action.equals("setEvent")) {
            return this.setEvent(args, callbackContext);
        }
        else if (action.equals("setOrder")) {
            return this.setOrder(args, callbackContext);
        }
        else if (action.equals("setAdShow")) {
            return this.setAdShow(args, callbackContext);
        }
        else if (action.equals("setAdClick")) {
            return this.setAdClick(args, callbackContext);
        }
        else if (action.equals("setAppDuration")) {
            return this.setAppDuration(args, callbackContext);
        }
        else if (action.equals("setPageDuration")) {
            return this.setPageDuration(args, callbackContext);
        }
        else if (action.equals("exitSdk")) {
            return this.exitSdk(args, callbackContext);
        }
        return false;
    }

    private boolean setDebugMode(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Boolean enabled = args.getBoolean(0);
        Tracking.setDebugMode(enabled);
        callbackContext.success();
        return true;
    }

    private boolean getDeviceId(CordovaArgs args, CallbackContext callbackContext) {
        String deviceId = Tracking.getDeviceId();
        if (deviceId != null && deviceId.length() > 0) {
            callbackContext.success(deviceId);
        } else {
            callbackContext.success("unknown");
//            callbackContext.error("Cannot get DeviceId");
        }
        return true;
    }

    private boolean getOAID(CordovaArgs args, CallbackContext callbackContext) {
        if (OAID != null && OAID.length() > 0) {
            callbackContext.success(OAID);
        } else {
            callbackContext.success("unknown");
//            callbackContext.error("Cannot get OAID");
        }
        return true;
    }

    private boolean initWithKeyAndChannelId(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (hasInitSdk) {
            callbackContext.success();
            return true;
        }

        Application app = yourApp.getApplication();
         if (app == null) {
            callbackContext.error("Cannot get current Application");
            return true;
        }

        InitParameters parameters = new InitParameters();
        parameters.appKey = APP_KEY;

        JSONObject initParams = args.getJSONObject(0);
        if (initParams != null) {
            if (initParams.has("appKey")) {
                parameters.appKey = initParams.getString("appKey");
            }
            if (initParams.has("channelId")) {
                parameters.channelId = initParams.getString("channelId");
            }
            if (initParams.has("oaid")) {
                parameters.oaid = initParams.getString("oaid");
            }
            if (initParams.has("assetFileName")) {
                parameters.assetFileName = initParams.getString("assetFileName");
            }
            if (initParams.has("oaidLibraryString")) {
                parameters.oaidLibraryString = initParams.getString("oaidLibraryString");
            }
        }

        if (parameters.oaid != null) {
            OAID = parameters.oaid;
            Tracking.initWithKeyAndChannelId(app, parameters);
            hasInitSdk = true;
            Log.d(LOG_TAG, "TrackingIO init success !");
            callbackContext.success();
        } else if (oaidTimeoutHandler == null) {
            int OAID_TIMEOUT_MILLIS = 10000;
            oaidTimeoutHandler = new Handler();
            oaidTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(LOG_TAG, "OAID callback Timeout !!!");
                    Tracking.initWithKeyAndChannelId(app, parameters);
                    hasInitSdk = true;
                    Log.d(LOG_TAG, "TrackingIO init success !");
                    callbackContext.success(); // Thread-safe.
                }
            }, OAID_TIMEOUT_MILLIS);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(LOG_TAG, "OAID start init");
                    int errorCode = MdidSdkHelper.InitSdk(yourApp.getApplicationContext(), true, new IIdentifierListener() {
                        @Override
                        public void OnSupport(boolean support, IdSupplier idSupplier) {
                            if (oaidTimeoutHandler != null) {
                                oaidTimeoutHandler.removeCallbacksAndMessages(null);
                                oaidTimeoutHandler = null;
                            }
                            Log.d(LOG_TAG, "OAID callback");
                            if(idSupplier != null && idSupplier.isSupported()) {
                                String oaid = idSupplier.getOAID();
                                if (oaid.equals("00000000000000000000000000000000")) {
                                    Log.w(LOG_TAG, "OAID Not Got permission !!!");
                                } else {
                                    OAID = oaid;
                                    parameters.oaid = OAID;
                                    Log.d(LOG_TAG, "OAID generated: " + OAID);
                                }
                            } else {
                                Log.e(LOG_TAG, "OAID Not Supported !!!");
                            }
                            Tracking.initWithKeyAndChannelId(app, parameters);
                            hasInitSdk = true;
                            Log.d(LOG_TAG, "TrackingIO init success !");
                            callbackContext.success(); // Thread-safe.
                        }
                    });
                    Log.d(LOG_TAG, "OAID init result: " + errorCode);
                    if (errorCode  == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {
                        Log.e(LOG_TAG,"不支持的设备");
                    } else if (errorCode == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {
                        Log.e(LOG_TAG,"加载配置文件出错");
                    } else if (errorCode == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {
                        Log.e(LOG_TAG,"不支持的设备厂商");
                    } else if (errorCode == ErrorCode.INIT_ERROR_RESULT_DELAY) {
                        Log.d(LOG_TAG,"获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程");
                    } else if (errorCode == ErrorCode.INIT_HELPER_CALL_ERROR) {
                        Log.e(LOG_TAG,"反射调用出错");
                    }
                }
            });
        }
        return true;
    }

    private boolean setRegisterWithAccountID(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String accountId = args.getString(0);
        if (accountId != null && accountId.length() > 0) {
            Tracking.setRegisterWithAccountID(accountId);
            callbackContext.success();
        } else {
            callbackContext.error("Please give accountId");
        }
        return true;
    }

    private boolean setLoginSuccessBusiness(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String accountId = args.getString(0);
        if (accountId != null && accountId.length() > 0) {
            Tracking.setLoginSuccessBusiness(accountId);
            callbackContext.success();
        } else {
            callbackContext.error("Please give accountId");
        }
        return true;
    }

    private boolean setPayment(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String transactionId = args.getString(0);
        String paymentType = args.getString(1);
        String currencyType = args.getString(2);
        Float currencyAmount = new Float(args.getLong(3));
        Log.d(LOG_TAG, "Payment args transactionId:" + transactionId
                + " paymentType:" + paymentType
                + " currencyType:" + currencyType
                + " currencyAmount" + currencyAmount
        );
        if (transactionId != null && paymentType != null && currencyType != null && currencyAmount != null) {
            Tracking.setPayment(transactionId, paymentType, currencyType, currencyAmount);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean setOrder(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String paymentType = args.getString(0);
        String currencyType = args.getString(1);
        Float currencyAmount = new Float(args.getLong(2));
        Log.d(LOG_TAG, "Payment args"
                + " paymentType:" + paymentType
                + " currencyType:" + currencyType
                + " currencyAmount" + currencyAmount
        );
        if (paymentType != null && currencyType != null && currencyAmount != null) {
            Tracking.setOrder(paymentType, currencyType, currencyAmount);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean setEvent(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String eventName = args.getString(0);
        JSONObject extra = args.getJSONObject(1);

        Map<String, Object> map = jsonObjectToMap(extra);
        if (eventName != null && extra != null && map != null) {
            Tracking.setEvent(eventName, map);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean setAdShow(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String adPlatform = args.getString(0);
        String adId = args.getString(1);
        String fill = args.getString(2);

        if (adPlatform != null && adId != null && fill != null) {
            Tracking.setAdShow(adPlatform, adId, fill);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean setAdClick(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String adPlatform = args.getString(0);
        String adId = args.getString(1);

        if (adPlatform != null && adId != null) {
            Tracking.setAdClick(adPlatform, adId);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean setAppDuration(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Long duration = args.getLong(0);

        if (duration != null) {
            this.hasCallSetAppDuration = true;
            Tracking.setAppDuration(duration);
            Log.d(LOG_TAG, "TrackingIO AppDuration: " + duration);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean setPageDuration(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String activityName = args.getString(0);
        Long duration = args.getLong(1);

        if (activityName != null && duration != null) {
            Tracking.setPageDuration(activityName, duration);
            callbackContext.success();
        } else {
            callbackContext.error("Wrong Parameters!");
        }
        return true;
    }

    private boolean exitSdk(CordovaArgs args, CallbackContext callbackContext) {
        this.hasCallExitSdk = true;
        Tracking.exitSdk();
        Log.d(LOG_TAG, "TrackingIO Cordova Plugin Exited");
        callbackContext.success();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.oaidTimeoutHandler != null) {
            this.oaidTimeoutHandler.removeCallbacksAndMessages(null);
            this.oaidTimeoutHandler = null;
        }
        if (hasInitSdk) {
            // 如果没有手动调用退出的api，则在销毁插件时自动调用
            if (!this.hasCallSetAppDuration) {
                long appRunTime = System.currentTimeMillis() - this.appStartTime;
                Tracking.setAppDuration(appRunTime);
                Log.d(LOG_TAG, "TrackingIO AppDuration: " + appRunTime);
            }
            if (!this.hasCallExitSdk) {
                Tracking.exitSdk();
                Log.d(LOG_TAG, "TrackingIO Cordova Plugin Exited");
            }
        }
    }
}