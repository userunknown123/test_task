package com.example.userunknown.myapplication;

import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by userunknown on 05.02.2018.
 */

public class MyApplication extends android.app.Application {
    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken ){
            if( newToken == null ){
                // VKAccessToken is invalid
                Log.d( "bbb", "vk token is invalid" );
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize( this );
    }
}
