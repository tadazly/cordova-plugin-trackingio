package com.tadazly.trackingio;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.reyun.tracking.sdk.InitParameters;
import com.reyun.tracking.sdk.Tracking;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TrackingIOCordovaPlugin extends CordovaPlugin {
    private final String LOG_TAG = "TrackingIO_Cordova";
    private String APP_KEY;
    private Activity yourApp;
    private long appStartTime;

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
        Log.d(LOG_TAG, "TrackingIO Cordova Plugin initialize");
        // 从配置中获取appKey，参考对应的plugin.xml文件
        this.APP_KEY = webView.getPreferences().getString("TRACKINGIO_APPKEY", "");
        this.yourApp = cordova.getActivity();
        this.appStartTime = System.currentTimeMillis();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, "TrackingIO Action:" + action);
        if (action.equals("setDebugMode")) {
            return this.setDebugMode(args, callbackContext);
        }
        else if (action.equals("getDeviceId")) {
            return this.getDeviceId(args, callbackContext);
        }
        else if (action.equals("initWithKeyAndChannelId")) {
            return this.initWithKeyAndChannelId(args, callbackContext);
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
            return true;
        } else {
            callbackContext.error("Cannot create deviceId");
            return false;
        }
    }

    private boolean initWithKeyAndChannelId(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Application app = this.yourApp.getApplication();
        if (!(app instanceof Application)) {
            callbackContext.error("Cannot get current Application");
            return false;
        }

        JSONObject initParams = args.getJSONObject(0);

        InitParameters parameters = new InitParameters();
        if (initParams.has("appKey")) {
            parameters.appKey = initParams.getString("appKey");
        } else {
            parameters.appKey = this.APP_KEY;
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

        Tracking.initWithKeyAndChannelId(app, parameters);
        callbackContext.success();
        return true;
    }

    private boolean setRegisterWithAccountID(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String accountId = args.getString(0);
        if (accountId != null && accountId.length() > 0) {
            Tracking.setRegisterWithAccountID(accountId);
            callbackContext.success();
            return true;
        } else {
            callbackContext.error("Please give accountId");
            return false;
        }
    }

    private boolean setLoginSuccessBusiness(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String accountId = args.getString(0);
        if (accountId != null && accountId.length() > 0) {
            Tracking.setLoginSuccessBusiness(accountId);
            return true;
        } else {
            callbackContext.error("Please give accountId");
            return false;
        }
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
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
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
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
    }

    private boolean setEvent(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String eventName = args.getString(0);
        JSONObject extra = args.getJSONObject(1);

        Map<String, Object> map = jsonObjectToMap(extra);
        if (eventName != null && extra != null && map != null) {
            Tracking.setEvent(eventName, map);
            callbackContext.success();
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
    }

    private boolean setAdShow(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String adPlatform = args.getString(0);
        String adId = args.getString(1);
        String fill = args.getString(2);

        if (adPlatform != null && adId != null && fill != null) {
            Tracking.setAdShow(adPlatform, adId, fill);
            callbackContext.success();
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
    }

    private boolean setAdClick(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String adPlatform = args.getString(0);
        String adId = args.getString(1);

        if (adPlatform != null && adId != null) {
            Tracking.setAdClick(adPlatform, adId);
            callbackContext.success();
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
    }

    private boolean setAppDuration(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Long duration = args.getLong(0);

        if (duration != null) {
            this.hasCallSetAppDuration = true;
            Tracking.setAppDuration(duration);
            Log.d(LOG_TAG, "TrackingIO AppDuration: " + duration);
            callbackContext.success();
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
    }

    private boolean setPageDuration(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String activityName = args.getString(0);
        Long duration = args.getLong(1);

        if (activityName != null && duration != null) {
            Tracking.setPageDuration(activityName, duration);
            callbackContext.success();
            return true;
        } else {
            callbackContext.error("Wrong Parameters!");
            return false;
        }
    }

    private boolean exitSdk(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.hasCallExitSdk = true;
        Tracking.exitSdk();
        Log.d(LOG_TAG, "TrackingIO Cordova Plugin Exited");
        callbackContext.success();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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