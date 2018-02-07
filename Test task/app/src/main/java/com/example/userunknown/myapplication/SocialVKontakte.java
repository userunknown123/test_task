package com.example.userunknown.myapplication;

import android.app.Activity;
import android.content.Context;


import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;


/*
HELP:
-------------------------------------
Achtung!!!!!
1) write this text in Graddle: app:module in dependencies: "compile 'com.vk:androidsdk:1.6.5'"
2) add to manifest in application:
"<activity android:name="com.vk.sdk.VKServiceActivity" android:label="ServiceActivity" android:theme="@style/VK.Transparent" />"
4) create app in "https://vk.com/apps?act=manage" (Standalone - приложение)
4) включить видимость для всех
4) указать поля "Название покета для Android", "Main Activity для Android", "Отпечаток сертификата (он береться ниже)"
3) add to strings.xml: "<integer name="com_vk_sdk_AppId">YOUR_APP_ID</integer>"
-------------------------------------
// Отпечаток сертификата для Android - нужно указать в настройках приложения vkontakte
String[] fingerprints = VKUtil.getCertificateFingerprint( this, this.getPackageName() );
for( String fingerprint : fingerprints ){
    Log.d( "bbb", fingerprint );
}
-------------------------------------
// in activity use mthod for:
@Override
protected void onActivityResult( int requestCode, int resultCode, Intent data ){
    if( !VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
        @Override
        public void onResult( VKAccessToken res ){
            // Пользователь успешно авторизовался
        }
        @Override
        public void onError( VKError error ){
            // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
        }
    })) {
        super.onActivityResult( requestCode, resultCode, data );
    }
}
-------------------------------------
Achtung!!!!!
1) Create this class in project
2) Write this text in manifest: "... <application android:name=".MyApplication" ..."
public class MyApplication extends android.app.Application {
    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged( VKAccessToken oldToken, VKAccessToken newToken ){
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
-------------------------------------
*/

public abstract class SocialVKontakte {

    // resolution for use methods in requests
    private static String[] scopes = new String[]{
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS
    };

    public static void login( Activity activity ){
        // authorize
        VKSdk.login( activity, scopes );
    }


    public static boolean isLoggedIn(){
        return VKSdk.isLoggedIn();
    }
}

