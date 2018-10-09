package com.selectstar.hwshin.cachemission.Activity;

import android.app.Activity;
import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kakao.auth.KakaoSDK;
import com.selectstar.hwshin.cachemission.LoginHelper.KakaoSDKAdapter;
import com.selectstar.hwshin.cachemission.R;

import java.util.HashMap;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private final String trackingId = "UA-126880414-1";

    private static final String PROPERTY_ID = "UA-126880414-1";

    public enum TrackerName {
        APP_TRACKER,           // 앱 별로 트래킹
        GLOBAL_TRACKER,        // 모든 앱을 통틀어 트래킹
        ECOMMERCE_TRACKER,     // 아마 유료 결재 트래킹 개념 같음
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId){
        if(!mTrackers.containsKey(trackerId)){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) :
                    (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) :
                            analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = getDefaultTracker();


    }
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(trackingId);
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableAutoActivityTracking(true);
        }
        return tracker;
    }


    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }
}
