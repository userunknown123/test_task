package com.example.userunknown.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LoginActivity extends AppCompatActivity {

    EditText name;
    EditText password;

    Button login;

    DataBaseClass db;

    int id_user = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        if(SocialVKontakte.isLoggedIn())
        {
            Intent intent = new Intent(this,PostingActivity.class);
            startActivity(intent);
            finish();
        }

        db = new DataBaseClass(this);

        name = (EditText)findViewById(R.id.editTextUsername);
        password = (EditText)findViewById(R.id.editTextPassword);

        login = (Button)findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_user = db.CheckUser(name.getText().toString(),password.getText().toString());
                if(id_user == -1)
                {
                    Toast.makeText(LoginActivity.this,"WRONG PASSWORD",Toast.LENGTH_SHORT).show();
                    return;
                }
                SocialVKontakte.login( LoginActivity.this );

            }
        });



        CheckBox license = (CheckBox)findViewById(R.id.checkBox_license);
        String url = "http://www.redbus.in/mob/mTerms.aspx";
        license.setText(Html.fromHtml("I have read and agree to the " +
                "<a href='"+url+"'>TERMS AND CONDITIONS</a>"));
        license.setMovementMethod(LinkMovementMethod.getInstance());
        license.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    login.setEnabled(true);
                }
                else
                {
                    login.setEnabled(false);
                }
            }
        });



    }



    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
        if( !VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult( VKAccessToken res ){
                // Пользователь успешно авторизовался
                VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        VKApiUser user = ((VKList<VKApiUser>)response.parsedModel).get(0);
                        int vk_id = user.getId();

                        DataBaseClass db = new DataBaseClass(LoginActivity.this);
                        String pass = password.getText().toString();
                        String login = name.getText().toString();
                        if(id_user == -2) {
                            id_user = db.AddUser(login, pass, vk_id);
                        }
                        SharedPreferences settings = getSharedPreferences("testTask", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = settings.edit();
                        edit.putInt("id_user",id_user);
                        edit.putBoolean("firstLaunch",false);
                        edit.commit();
                        Intent intent = new Intent(LoginActivity.this,PostingActivity.class);

                        startActivity(intent);
                        finish();
                    }
                });

            }
            @Override
            public void onError( VKError error ){
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult( requestCode, resultCode, data );
        }
    }
}
