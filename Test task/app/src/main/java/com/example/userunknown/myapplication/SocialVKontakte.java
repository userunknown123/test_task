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
    public static void login( android.app.Fragment fragment ){
        VKSdk.login( fragment, scopes );
    }
    public static void login( android.support.v4.app.Fragment fragment ){
        Intent intent = new Intent( fragment.getActivity(), VKServiceActivity.class );
        intent.putExtra( "arg1", "Authorization" );
        ArrayList scopes = new ArrayList<>();
        Collections.addAll( scopes, SocialVKontakte.scopes );
        scopes.add( VKScope.OFFLINE );
        intent.putStringArrayListExtra( "arg2", scopes );
        intent.putExtra( "arg4", VKSdk.isCustomInitialize() );
        fragment.startActivityForResult( intent, VKServiceActivity.VKServiceType.Authorization.getOuterCode() );
    }

    public static boolean isLoggedIn(){
        return VKSdk.isLoggedIn();
    }




    // build dialog share to WALL without image
    private static VKShareDialogBuilder buildShareDialog( String text, String linkText, String link,
                                                          VKShareDialog.VKShareDialogListener listener ){
        VKShareDialogBuilder builder = new VKShareDialogBuilder();
        builder.setText( text );
        builder.setAttachmentLink( linkText, link );
        builder.setShareDialogListener( listener );
        return builder;
    }
    // build dialog share to WALL with image
    private static VKShareDialogBuilder buildShareDialog( String text, String linkText, String link,
                                                          final Bitmap bitmap,
                                                          final VKShareDialog.VKShareDialogListener listener ){
        final VKShareDialogBuilder builder = buildShareDialog( text, linkText, link, new VKShareDialog.VKShareDialogListener() {
            @Override
            public void onVkShareComplete( int postId ){
                if( listener != null ) listener.onVkShareComplete( postId );
                recycle();
            }
            @Override
            public void onVkShareCancel(){
                if( listener != null ) listener.onVkShareCancel();
                recycle();
            }
            @Override
            public void onVkShareError( VKError error ){
                if( listener != null ) listener.onVkShareError( error );
            }
            private void recycle(){
                if( bitmap != null && bitmap.isRecycled() == false ) bitmap.recycle();
            }
        });
        if( bitmap != null ) {
            builder.setAttachmentImages( new VKUploadImage[]{
                    new VKUploadImage( bitmap, VKImageParameters.jpgImage( 1f ) )
            });
        }
        return builder;
    }
    // build share dialog with image link from internet
    public static VKShareDialogBuilder buildShareDialog(    String text, String linkText, String link,
                                                            String linkImage, VKShareDialog.VKShareDialogListener listener ){
        return buildShareDialog( text + "<br/><img src='" + linkImage + "' />", linkText, link, listener );
    }


    private static VKShareDialog.VKShareDialogListener buildListenerShowDialogAtComplete( final Context context,
            final String textSuccesfully, final String textError
    ){
        if( context == null ) return null;

        return new VKShareDialog.VKShareDialogListener() {
            @Override
            public void onVkShareComplete( int postId ){
                toast( textSuccesfully );
            }
            @Override
            public void onVkShareCancel(){}
            @Override
            public void onVkShareError( VKError error ){
                toast( textError );
            }

            private void toast( String string ){
                if( TextUtils.isEmpty( string ) == false ){
                    Toast.makeText( context, string, Toast.LENGTH_SHORT ).show();
                }
            }
        };
    }





    // share to WALL, if bitmap is null - without image post
    public static void shareWithDialog( Context context, FragmentManager fragmentManager, Bitmap bitmap,
                                        String text, String linkText, String link,
                                        String textSuccesfully, String textError ){

        VKShareDialogBuilder builder = buildShareDialog(    text, linkText, link, bitmap,
                buildListenerShowDialogAtComplete( context, textSuccesfully, textError ) );
        builder.show( fragmentManager, "VK_SHARE_DIALOG" );
    }
    public static void shareWithDialog(     FragmentManager fragmentManager, Bitmap bitmap,
                                            String text, String linkText, String link ){
        shareWithDialog( null, fragmentManager, bitmap, text, linkText, link, null, null );
    }

    // share to WALL with image at link from internet
    public static void shareWithDialog(     Context context, FragmentManager fragmentManager, String linkImage,
                                            String text, String linkText, String link,
                                            String textSuccesfully, String textError ){
        VKShareDialogBuilder builder = buildShareDialog( text, linkImage, linkText, link,
                buildListenerShowDialogAtComplete( context, textSuccesfully, textError ) );
        builder.show( fragmentManager, "VK_SHARE_DIALOG" );
    }
    public static void shareWithDialog(     FragmentManager fragmentManager, String linkImage,
                                            String text, String linkText, String link ){
        shareWithDialog( null, fragmentManager, text, linkImage, linkText, link, null, null );
    }


    public static void sendMessage( int user_id, String message ){
        VKRequest request = new VKRequest( "messages.send",
                VKParameters.from(  VKApiConst.USER_ID, Integer.toString( user_id ),
                                    VKApiConst.MESSAGE, message ) );
        request.executeWithListener( new VKRequest.VKRequestListener() {
            @Override
            public void onComplete( VKResponse response ){
                super.onComplete( response );
            }
        } );
    }
}

