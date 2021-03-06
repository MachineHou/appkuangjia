//package com.tencent.qcloud.tim.demo;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.multidex.MultiDex;
//
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
//import com.heytap.msp.push.HeytapPushManager;
//import com.huawei.hms.push.HmsMessaging;
//import com.meizu.cloud.pushsdk.PushManager;
//import com.meizu.cloud.pushsdk.util.MzSystemUtils;
//import com.tencent.bugly.crashreport.CrashReport;
//import com.tencent.imsdk.v2.V2TIMCallback;
//import com.tencent.imsdk.v2.V2TIMManager;
//import com.tencent.imsdk.v2.V2TIMMessage;
//import com.tencent.qcloud.tim.demo.helper.ConfigHelper;
//import com.tencent.qcloud.tim.demo.signature.GenerateTestUserSig;
//import com.tencent.qcloud.tim.demo.thirdpush.HUAWEIHmsMessageService;
//import com.tencent.qcloud.tim.demo.thirdpush.ThirdPushTokenMgr;
//import com.tencent.qcloud.tim.demo.utils.BrandUtil;
//import com.tencent.qcloud.tim.demo.utils.DemoLog;
//import com.tencent.qcloud.tim.demo.utils.MessageNotification;
//import com.tencent.qcloud.tim.demo.utils.PrivateConstants;
//import com.tencent.qcloud.tim.uikit.TUIKit;
//import com.tencent.qcloud.tim.uikit.base.IMEventListener;
//import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
//import com.tencent.rtmp.TXLiveBase;
//import com.vivo.push.PushClient;
//import com.xiaomi.mipush.sdk.MiPushClient;
//
//public class DemoApplication extends Application {
//
//    private static final String TAG = DemoApplication.class.getSimpleName();
//
//    private final String licenceUrl = "";
//    private final String licenseKey = "";
//
//    private static DemoApplication instance;
//
//    public static DemoApplication instance() {
//        return instance;
//    }
//
//    @Override
//    public void onCreate() {
//        DemoLog.i(TAG, "onCreate");
//        super.onCreate();
//        instance = this;
//        MultiDex.install(this);
//        // bugly??????
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
//        strategy.setAppVersion(V2TIMManager.getInstance().getVersion());
//        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);
//        TXLiveBase.getInstance().setLicence(instance, licenceUrl, licenseKey);
//        /**
//         * TUIKit??????????????????
//         *
//         * @param context  ?????????????????????????????????????????????ApplicationContext
//         * @param sdkAppID ???????????????????????????????????????sdkAppID
//         * @param configs  TUIKit?????????????????????????????????????????????????????????????????????API??????
//         */
//        TUIKit.init(this, GenerateTestUserSig.SDKAPPID, new ConfigHelper().getConfigs());
//        HeytapPushManager.init(this, true);
//        if (BrandUtil.isBrandXiaoMi()) {
//            // ??????????????????
//            MiPushClient.registerPush(this, PrivateConstants.XM_PUSH_APPID, PrivateConstants.XM_PUSH_APPKEY);
//        } else if (BrandUtil.isBrandHuawei()) {
//            // ???????????????????????????????????????Push???????????????????????????
//            HmsMessaging.getInstance(this).turnOnPush().addOnCompleteListener(new com.huawei.hmf.tasks.OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(com.huawei.hmf.tasks.Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        DemoLog.i(TAG, "huawei turnOnPush Complete");
//                    } else {
//                        DemoLog.e(TAG, "huawei turnOnPush failed: ret=" + task.getException().getMessage());
//                    }
//                }
//            });
//        } else if (MzSystemUtils.isBrandMeizu(this)) {
//            // ??????????????????
//            PushManager.register(this, PrivateConstants.MZ_PUSH_APPID, PrivateConstants.MZ_PUSH_APPKEY);
//        } else if (BrandUtil.isBrandVivo()) {
//            // vivo????????????
//            PushClient.getInstance(getApplicationContext()).initialize();
//        } else if (HeytapPushManager.isSupportPush()) {
//            // oppo???????????????????????????????????????????????????????????????token?????????????????????MainActivity??????
//        } else if (BrandUtil.isGoogleServiceSupport()) {
//            FirebaseInstanceId.getInstance().getInstanceId()
//                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<InstanceIdResult>() {
//                        @Override
//                        public void onComplete(Task<InstanceIdResult> task) {
//                            if (!task.isSuccessful()) {
//                                DemoLog.w(TAG, "getInstanceId failed exception = " + task.getException());
//                                return;
//                            }
//
//                            // Get new Instance ID token
//                            String token = task.getResult().getToken();
//                            DemoLog.i(TAG, "google fcm getToken = " + token);
//
//                            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
//                        }
//                    });
//        };
//
//        registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());
//
////        if (BuildConfig.DEBUG) {
////            if (LeakCanary.isInAnalyzerProcess(this)) {
////                return;
////            }
////            LeakCanary.install(this);
////        }
//    }
//
//    class StatisticActivityLifecycleCallback implements ActivityLifecycleCallbacks {
//        private int foregroundActivities = 0;
//        private boolean isChangingConfiguration;
//        private IMEventListener mIMEventListener = new IMEventListener() {
//            @Override
//            public void onNewMessage(V2TIMMessage msg) {
//                MessageNotification notification = MessageNotification.getInstance();
//                notification.notify(msg);
//            }
//        };
//
//        private ConversationManagerKit.MessageUnreadWatcher mUnreadWatcher = new ConversationManagerKit.MessageUnreadWatcher() {
//            @Override
//            public void updateUnread(int count) {
//                // ????????????????????????
//                HUAWEIHmsMessageService.updateBadge(DemoApplication.this, count);
//            }
//        };
//
//        @Override
//        public void onActivityCreated(Activity activity, Bundle bundle) {
//            DemoLog.i(TAG, "onActivityCreated bundle: " + bundle);
//            if (bundle != null) { // ???bundle??????????????????????????????
//                // ??????????????????
//                Intent intent = new Intent(activity, SplashActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        }
//
//        @Override
//        public void onActivityStarted(Activity activity) {
//            foregroundActivities++;
//            if (foregroundActivities == 1 && !isChangingConfiguration) {
//                // ??????????????????
//                DemoLog.i(TAG, "application enter foreground");
//                V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
//                    @Override
//                    public void onError(int code, String desc) {
//                        DemoLog.e(TAG, "doForeground err = " + code + ", desc = " + desc);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        DemoLog.i(TAG, "doForeground success");
//                    }
//                });
//                TUIKit.removeIMEventListener(mIMEventListener);
//                ConversationManagerKit.getInstance().removeUnreadWatcher(mUnreadWatcher);
//                MessageNotification.getInstance().cancelTimeout();
//            }
//            isChangingConfiguration = false;
//        }
//
//        @Override
//        public void onActivityResumed(Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityPaused(Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityStopped(Activity activity) {
//            foregroundActivities--;
//            if (foregroundActivities == 0) {
//                // ??????????????????
//                DemoLog.i(TAG, "application enter background");
//                int unReadCount = ConversationManagerKit.getInstance().getUnreadTotal();
//                V2TIMManager.getOfflinePushManager().doBackground(unReadCount, new V2TIMCallback() {
//                    @Override
//                    public void onError(int code, String desc) {
//                        DemoLog.e(TAG, "doBackground err = " + code + ", desc = " + desc);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        DemoLog.i(TAG, "doBackground success");
//                    }
//                });
//                // ????????????????????????????????????????????????
//                TUIKit.addIMEventListener(mIMEventListener);
//                ConversationManagerKit.getInstance().addUnreadWatcher(mUnreadWatcher);
//            }
//            isChangingConfiguration = activity.isChangingConfigurations();
//        }
//
//        @Override
//        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onActivityDestroyed(Activity activity) {
//
//        }
//    }
//}
