var exec = require('cordova/exec');

module.exports = {
    setDebugMode(enabled, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setDebugMode', [enabled]);
    },

    getDeviceId() {
        return new Promise(function (resolve, reject) {
            exec(function (res) {
                resolve(res);
            }, function (err) {
                reject(err);
            }, 'Tracking', 'getDeviceId', []);
        });
    },

    getOAID() {
        return new Promise(function (resolve, reject) {
            exec(function (res) {
                resolve(res);
            }, function (err) {
                reject(err);
            }, 'Tracking', 'getOAID', []);
        });
    },

    initOaidSdk(onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'initOaidSdk', []);
    },

    initWithKeyAndChannelId(params, onSuccess, onError) {
        if (!params) {
            params = {};
        }
        exec(onSuccess, onError, 'Tracking', 'initWithKeyAndChannelId', [params]);
    },

    setRegisterWithAccountID(accountId, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setRegisterWithAccountID', [accountId]);
    },

    setLoginSuccessBusiness(accountId, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setLoginSuccessBusiness', [accountId]);
    },

    setPayment(transactionId, paymentType, currencyType, currencyAmount, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setPayment', [transactionId, paymentType, currencyType, currencyAmount]);
    },

    setEvent(eventName, extra, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setEvent', [eventName, extra]);
    },

    setOrder(transactionId, currencyType, currencyAmount, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setOrder', [transactionId, currencyType, currencyAmount]);
    },

    setAdShow(adPlatform, adId, fill, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setAdShow', [adPlatform, adId, fill]);
    },

    setAdClick(adPlatform, adId, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setAdClick', [adPlatform, adId]);
    },

    setAppDuration(duration, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setAppDuration', [duration]);
    },

    setPageDuration(activityName, duration, onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'setPageDuration', [activityName, duration]);
    },

    exitSdk(onSuccess, onError) {
        exec(onSuccess, onError, 'Tracking', 'exitSdk', []);
    }
}