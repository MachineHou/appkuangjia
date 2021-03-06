package com.fosung.lighthouse.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.example.slbappcomm.broadcastreceiver.PhoneService;
import com.example.slbappcomm.uploadimg2.GlideImageLoader2;
import com.example.slbappcomm.utils.BanbenCommonUtils;
import com.example.slbappindex.services.MOBIDservices;
import com.example.slbappjpushshare.fenxiang.JPushShareUtils;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.changelanguage.LocalManageUtil;
import com.haier.cellarette.libretrofit.common.RetrofitNetNew;
import com.haier.cellarette.libwebview.hois2.HiosHelper;
import com.heytap.msp.push.HeytapPushManager;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.meituan.android.walle.WalleChannelReader;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.PrivacyPolicy;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushCallback;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tim.demo.SplashActivity;
import com.tencent.qcloud.tim.demo.helper.ConfigHelper;
import com.tencent.qcloud.tim.demo.signature.GenerateTestUserSig;
import com.tencent.qcloud.tim.demo.thirdpush.HUAWEIHmsMessageService;
import com.tencent.qcloud.tim.demo.utils.DemoLog;
import com.tencent.qcloud.tim.demo.utils.MessageNotification;
import com.tencent.qcloud.tim.demo.utils.PrivateConstants;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.rtmp.TXLiveBase;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatformConfig;
import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

/**
 * ?????????ApplicationLike???.
 * <p>
 * ?????????????????????Application??????????????????????????????Application???????????????????????????????????????<br/>
 *
 * @author wenjiewu
 * @since 2016/11/7
 */
public class MyApplication extends MultiDexApplication {

    public static final String TAG = "Tinker.SampleApplicationLike";
    public static final String DIR_PROJECT = "/geekandroid/app/";
    public static final String DIR_CACHE = DIR_PROJECT + "cache/"; // ??????????????????
    public static final String IMG_CACHE = DIR_PROJECT + "image/"; // image????????????
    public static final String VIDEO_CACHE = DIR_PROJECT + "video/"; // video????????????
    public static final String MUSIC_CACHE = DIR_PROJECT + "music/"; // music????????????
    private int mFinalCount;

    @Override
    public void onCreate() {
        super.onCreate();
        // ??????bugly
        configBugly();
        // ????????????
        LocalManageUtil.setApplicationLanguage(this);
//        GlideOptionsFactory.init(this, R.drawable.ic_def_loading);
        handleSSLHandshake();
        configHios();
        configRetrofitNet();
        Utils.init(this);// com.blankj:utilcode:1.17.3
        //?????????????????????
        configShare();
        //?????????????????????
        configTongji();
        //?????????????????????
        configTuisong();
        //?????????mob
        configMob();
        // ???????????????????????????
        configShipei();
        // ????????????
//        startService(new Intent(BaseApp.get(), ListenMusicPlayerService.class));
        // ????????????
//        cofigPhone();
        //?????????Umeng??????
        configUmengTongji();
        // ?????????????????????toast
//        ToastUtils.init(this);
        // ndk
        configNDK();
        // mmkv
        configmmkv();
        //???app????????????,activity???????????????????????????,????????????????????????
//        GenseeLive.initConfiguration(getApplicationContext());
        // ??????-> ??????????????????
        initImagePicker();
        //?????????G??????
//        ApplicationUtil.init(this);
//        ???????????????
        regActivityLife();
        // ??????IM
        initHx();
        initThrowableHandler();
        closeAndroidPDialog();
        // TencentIM
        initTencentIM();
    }

    private void initTencentIM() {
        // bugly??????
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(V2TIMManager.getInstance().getVersion());
        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);
        TXLiveBase.getInstance().setLicence(this, "licenceUrl", "licenseKey");
        /**
         * TUIKit??????????????????
         *
         * @param context  ?????????????????????????????????????????????ApplicationContext
         * @param sdkAppID ???????????????????????????????????????sdkAppID
         * @param configs  TUIKit?????????????????????????????????????????????????????????????????????API??????
         */
        TUIKit.init(this, GenerateTestUserSig.SDKAPPID, new ConfigHelper().getConfigs());
        HeytapPushManager.init(this, true);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            private int foregroundActivities = 0;
            private boolean isChangingConfiguration;
            private IMEventListener mIMEventListener = new IMEventListener() {
                @Override
                public void onNewMessage(V2TIMMessage msg) {
                    MessageNotification notification = MessageNotification.getInstance();
                    notification.notify(msg);
                }
            };

            private ConversationManagerKit.MessageUnreadWatcher mUnreadWatcher = new ConversationManagerKit.MessageUnreadWatcher() {
                @Override
                public void updateUnread(int count) {
                    // ????????????????????????
                    HUAWEIHmsMessageService.updateBadge(BaseApp.get(), count);
                }
            };

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                DemoLog.i(TAG, "onActivityCreated bundle: " + bundle);
                if (bundle != null) { // ???bundle??????????????????????????????
                    // ??????????????????
                    Intent intent = new Intent(activity, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                foregroundActivities++;
                if (foregroundActivities == 1 && !isChangingConfiguration) {
                    // ??????????????????
                    DemoLog.i(TAG, "application enter foreground");
                    V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
                        @Override
                        public void onError(int code, String desc) {
                            DemoLog.e(TAG, "doForeground err = " + code + ", desc = " + desc);
                        }

                        @Override
                        public void onSuccess() {
                            DemoLog.i(TAG, "doForeground success");
                        }
                    });
                    TUIKit.removeIMEventListener(mIMEventListener);
                    ConversationManagerKit.getInstance().removeUnreadWatcher(mUnreadWatcher);
                    MessageNotification.getInstance().cancelTimeout();
                }
                isChangingConfiguration = false;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                foregroundActivities--;
                if (foregroundActivities == 0) {
                    // ??????????????????
                    DemoLog.i(TAG, "application enter background");
                    int unReadCount = ConversationManagerKit.getInstance().getUnreadTotal();
                    V2TIMManager.getOfflinePushManager().doBackground(unReadCount, new V2TIMCallback() {
                        @Override
                        public void onError(int code, String desc) {
                            DemoLog.e(TAG, "doBackground err = " + code + ", desc = " + desc);
                        }

                        @Override
                        public void onSuccess() {
                            DemoLog.i(TAG, "doBackground success");
                        }
                    });
                    // ????????????????????????????????????????????????
                    TUIKit.addIMEventListener(mIMEventListener);
                    ConversationManagerKit.getInstance().addUnreadWatcher(mUnreadWatcher);
                }
                isChangingConfiguration = activity.isChangingConfigurations();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private void initHx() {
//        // ?????????PreferenceManager
//        PreferenceManager.init(this);
//        // init hx sdk
//        if (DemoHelper.getInstance().getAutoLogin()) {
//            MyLogUtil.i("DemoApplication", "application initHx");
//            DemoHelper.getInstance().init(this);
//        }

    }

    private void initThrowableHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                MyLogUtil.e("demoApp", e.getMessage());
            }
        });
    }

    /**
     * ??????androidP ???????????????????????????????????????
     * ???????????????detected problems with api ???
     */
    private void closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            try {
                Class aClass = Class.forName("android.content.pm.PackageParser$Package");
                Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
                declaredConstructor.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Class cls = Class.forName("android.app.ActivityThread");
                Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
                declaredMethod.setAccessible(true);
                Object activityThread = declaredMethod.invoke(null);
                Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
                mHiddenApiWarningShown.setAccessible(true);
                mHiddenApiWarningShown.setBoolean(activityThread, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler;

    private void configMob() {
        MobSDK.init(this);
        //???????????????????????????  ?????????MainActivity????????????????????????MobPushReceiver
        MobPush.getRegistrationId(new MobPushCallback<String>() {

            @Override
            public void onCallback(String rid) {
                MyLogUtil.e("MobPush", "RegistrationId:" + rid);
                SPUtils.getInstance().put("MOBID", rid);
                //TODO MOBID TEST
                startService(new Intent(getApplicationContext(), MOBIDservices.class));
            }
        });
        //????????????
        mob_privacy();
//        MobSDK.init(this);
//        //???????????????????????????  ?????????MainActivity????????????????????????MobPushReceiver
//        String processName = getProcessName(this);
//        MobPush.getRegistrationId(new MobPushCallback<String>() {
//
//            @Override
//            public void onCallback(String rid) {
//                System.out.println("MobPush->RegistrationId:" + rid);
//                SPUtils.getInstance().put("MOBID", rid);
//            }
//        });
//        if (getPackageName().equals(processName)) {
//            MobPush.addPushReceiver(new MobPushReceiver() {
//                @Override
//                public void onCustomMessageReceive(Context context, MobPushCustomMessage message) {
//                    //?????????????????????(??????)
//                    System.out.println("MobPush onCustomMessageReceive:" + message.toString());
//                }
//
//                @Override
//                public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
//                    //???????????????
//                    System.out.println("MobPush onNotifyMessageReceive:" + message.toString());
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = "Message Receive:" + message.toString();
//                    handler.sendMessage(msg);
//
//                }
//
//                @Override
//                public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
//                    //?????????????????????????????????
//                    System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.toString());
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = "Click Message:" + message.toString();
//                    handler.sendMessage(msg);
//                }
//
//                @Override
//                public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
//                    //??????tags?????????????????????
//                    System.out.println("MobPush onTagsCallback:" + operation + "  " + errorCode);
//                }
//
//                @Override
//                public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
//                    //??????alias?????????????????????
//                    System.out.println("MobPush onAliasCallback:" + alias + "  " + operation + "  " + errorCode);
//                }
//            });
//
//            handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
//                @Override
//                public boolean handleMessage(@NonNull Message msg) {
//                    if (msg.what == 1) {
//                        System.out.println("MobPush Callback Data:" + msg.obj);
//                    }
//                    return false;
//                }
//            });
//        }
        // activity????????????
//        Intent intent = getIntent();
//        Uri uri = intent.getData();
//        if (intent != null) {
//            System.out.println("MobPush linkone intent---" + intent.toUri(Intent.URI_INTENT_SCHEME));
//        }
//        StringBuilder sb = new StringBuilder();
//        if (uri != null) {
//            sb.append(" scheme:" + uri.getScheme() + "\n");
//            sb.append(" host:" + uri.getHost() + "\n");
//            sb.append(" port:" + uri.getPort() + "\n");
//            sb.append(" query:" + uri.getQuery() + "\n");
//        }
//
//        //??????link?????????????????????
//        JSONArray jsonArray = MobPushUtils.parseSchemePluginPushIntent(intent);
//        if (jsonArray != null){
//            sb.append("\n" + "bundle toString :" + jsonArray.toString());
//        }
//        //??????scheme????????????????????????????????????
//        JSONArray var = new JSONArray();
//        var =  MobPushUtils.parseSchemePluginPushIntent(intent2);
//        //??????????????????????????????
//        JSONArray var2 = new JSONArray();
//        var2 = MobPushUtils.parseMainPluginPushIntent(intent2);
    }

    public void mob_privacy() {
        // ????????????Locale
// Locale locale = Locale.CHINA;
// ??????????????????locale
        Locale locale = null;
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = getApplicationContext().getResources().getConfiguration().getLocales();
            if (localeList != null && !localeList.isEmpty()) {
                locale = localeList.get(0);
            }
        } else {
            locale = getApplicationContext().getResources().getConfiguration().locale;
        }

// ????????????????????????,locale?????????null?????????????????????????????????????????????
//        PrivacyPolicy policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL, locale);
//        String url = policyUrl.getContent();

// ????????????????????????,locale?????????null?????????????????????????????????????????????
        MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_URL, new PrivacyPolicy.OnPolicyListener() {
            @Override
            public void onComplete(PrivacyPolicy data) {
                if (data != null) {
                    // ???????????????
                    String text = data.getContent();
                    MyLogUtil.e("MobPush", "??????????????????->" + text);
                    MobSDK.submitPolicyGrantResult(!TextUtils.isEmpty(text), new OperationCallback<Void>() {
                        @Override
                        public void onComplete(Void data) {
                            MyLogUtil.e("MobPush", "???????????????????????????????????????");
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            MyLogUtil.e("MobPush", "???????????????????????????????????????");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // ????????????
            }
        });

    }

    /**
     * ??????https???????????????
     * ??????Glide??????https???????????????
     * javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
     */
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts?????????????????????
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }


    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new ClassTest1GlideImageLoader());    //?????????????????????
        imagePicker.setImageLoader(new GlideImageLoader2());    //?????????????????????
        imagePicker.setShowCamera(true);                       //??????????????????
        imagePicker.setMultiMode(true);                       //????????????
        imagePicker.setCrop(true);                             //?????????????????????????????????
        imagePicker.setSaveRectangle(true);                    //???????????????????????????
        imagePicker.setSelectLimit(9);              //??????????????????
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //??????????????????
        imagePicker.setFocusWidth(1000);                        //?????????????????????????????????????????????????????????????????????
        imagePicker.setFocusHeight(1000);                       //?????????????????????????????????????????????????????????????????????
        imagePicker.setOutPutX(1000);                          //????????????????????????????????????
        imagePicker.setOutPutY(1000);                          //????????????????????????????????????
    }

    private void configmmkv() {
        MmkvUtils.getInstance().get("");
        MmkvUtils.getInstance().get_demo();
    }

    private void configNDK() {
        JNIUtils jniUtils = new JNIUtils();
        MyLogUtil.e("--JNIUtils--", jniUtils.stringFromJNI());
    }

    @Override
    protected void attachBaseContext(Context base) {
        //????????????????????????
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //????????????????????????
        LocalManageUtil.onConfigurationChanged(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    private void configUmengTongji() {
        /**
         * ??????walle????????????
         */
        String channel = WalleChannelReader.getChannel(this);
        MyLogUtil.e("--??????--", channel);
        MyLogUtil.e("??????->", BanbenCommonUtils.banben_comm);
        if (TextUtils.equals(BanbenCommonUtils.banben_comm, "??????")) {
            UMConfigure.setLogEnabled(true);
            UMConfigure.init(this, "601a644d6a2a470e8fa120e3", channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        } else if (TextUtils.equals(BanbenCommonUtils.banben_comm, "?????????")) {
            UMConfigure.setLogEnabled(true);
            UMConfigure.init(this, "601a644d6a2a470e8fa120e3", channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        } else if (TextUtils.equals(BanbenCommonUtils.banben_comm, "??????")) {
            UMConfigure.setLogEnabled(false);
            UMConfigure.init(this, "601a644d6a2a470e8fa120e3", channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        }
        //??????AUTO???????????????????????????SDK????????????????????????????????????????????????
        //???????????????App???Application.onCreate???????????????????????????
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    private void cofigPhone() {
        Intent intent = new Intent(this, PhoneService.class);
        startService(intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//            startService(intent);
//        }
    }

    private void configBugly() {
        // ?????????????????????
//         String channel = WalleChannelReader.getChannel(this);
//         Bugly.setAppChannel(getApplicationContext(), channel);
        // ????????????SDK????????????appId??????????????????Bugly???????????????appId
        // bugly??????
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(AppUtils.getAppVersionName());
//        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);
        CrashReport.initCrashReport(getApplicationContext(), "3aeeb18e5e", true, strategy);
//        Bugly.init(this, "e0b1ba785f", true);
        if (TextUtils.equals(BanbenCommonUtils.banben_comm, "??????")) {
//            CrashReport.initCrashReport(this, "068e7f32c3", true);// ??????
            Bugly.init(getApplicationContext(), "3aeeb18e5e", true);
            MyLogUtil.on(true);
            //TODO test
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
        } else if (TextUtils.equals(BanbenCommonUtils.banben_comm, "?????????")) {
//            CrashReport.initCrashReport(this, "068e7f32c3", true);// ?????????
            Bugly.init(getApplicationContext(), "3aeeb18e5e", true);
            MyLogUtil.on(true);
        } else if (TextUtils.equals(BanbenCommonUtils.banben_comm, "??????")) {
//            CrashReport.initCrashReport(this, "068e7f32c3", false);// ??????
            Bugly.init(getApplicationContext(), "3aeeb18e5e", true);
            MyLogUtil.on(true);
        }
    }

    private void configShipei() {
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(true)
                .setSupportSubunits(Subunits.MM);
        AutoSize.initCompatMultiProcess(this);
    }

    private void configTuisong() {
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);
//        MyLogUtil.e("jiguang->", JPushInterface.getRegistrationID(this));
//        SPUtils.getInstance().put("MOBID", JPushInterface.getRegistrationID(this));
    }

    private void configShare() {
        JShareInterface.setDebugMode(true);
        PlatformConfig platformConfig = new PlatformConfig()
                .setWechat(JPushShareUtils.APP_ID, JPushShareUtils.APP_KEY)// wxa3fa50c49fcd271c 746c2cd0f414de2c256c4f2095316bd0
                .setQQ("1106011004", "YIbPvONmBQBZUGaN")
                .setSinaWeibo("374535501", "baccd12c166f1df96736b51ffbf600a2", "https://www.jiguang.cn");
        JShareInterface.init(this, platformConfig);// android 10??????
    }

    private void configTongji() {
        // ??????????????????,????????????????????????
//        JAnalyticsInterface.setDebugMode(true);
//        JAnalyticsInterface.init(this);
    }

    private void configHios() {
//        HiosRegister.load();// ?????????????????? ?????????
        HiosHelper.config(AppUtils.getAppPackageName() + ".ad.web.page", AppUtils.getAppPackageName() + ".web.page");
    }

    protected void configRetrofitNet() {
        String cacheDir = getExternalFilesDir(null) + DIR_CACHE;
        // https://api-cn.faceplusplus.com/
//        RetrofitNet.config();
        RetrofitNetNew.config();
    }


    private void regActivityLife() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    mFinalCount++;
                    //??????mFinalCount ==1??????????????????????????????
                    if (mFinalCount == 1) {
                        //??????????????????????????????
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    mFinalCount--;
                    //??????mFinalCount == 0???????????????????????????
                    if (mFinalCount == 0) {
                        //??????????????????????????????
                        //                    Toast.makeText(MyApplication.this, "?????????????????????????????????", Toast.LENGTH_LONG).show();
                        ToastUtils.showLong(AppUtils.getAppName() + "?????????????????????");
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
    }

}
