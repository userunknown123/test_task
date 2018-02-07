package com.example.userunknown.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostingActivity extends AppCompatActivity {


    ImageView picture;
    EditText message;

    DataBaseClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        setTitle("Main");

        db = new DataBaseClass(this);

        picture = (ImageView)findViewById(R.id.imageView_picture);

        message = (EditText)findViewById(R.id.editText_message);

        Button send = (Button)findViewById(R.id.button_Send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                if(picture.getTag() != null) {
                    bitmap = ImageOperation.decodeSampledBitmapFromPath(picture.getTag().toString(), 400, 400);
                    //Log.i("size", bitmap.getWidth() + " " + bitmap.getHeight());
                    VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(bitmap,
                            VKImageParameters.jpgImage(0.9f)), getMyId(), 0);
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            // recycle bitmap
                            VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                            VKApi.wall().post(VKParameters.from(VKApiConst.ATTACHMENTS, new VKAttachments(photoModel), VKApiConst.MESSAGE, message));
                            makePost(new VKAttachments(photoModel), message.getText().toString(), getMyId());

                            SharedPreferences prefs = getSharedPreferences("testTask", Context.MODE_PRIVATE);
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String date = df.format(Calendar.getInstance().getTime());
                            if(message.getText().toString().equals(""))
                            {
                                db.addHistory("Upload picture", date, prefs.getInt("id_user", -1));
                            }
                            else {

                                db.addHistory("Upload picture and status", date, prefs.getInt("id_user", -1));
                            }

                            Toast.makeText(PostingActivity.this,"message posted",Toast.LENGTH_SHORT);

                            picture.setImageBitmap(null);
                            picture.setTag(null);
                            message.setText("");
                        }
                        @Override
                        public void onError(VKError error) {
                            // error
                        }
                    });
                }
                else
                {
                    makePost(new VKAttachments(), message.getText().toString(), getMyId());
                    SharedPreferences prefs = getSharedPreferences("testTask", Context.MODE_PRIVATE);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    db.addHistory("Upload status",date,prefs.getInt("id_user",-1));
                    Toast.makeText(PostingActivity.this,"message posted",Toast.LENGTH_SHORT);

                    picture.setImageBitmap(null);
                    picture.setTag(null);
                    message.setText("");
                }





            }
        });

        Button upload = (Button)findViewById(R.id.button_uploadimage);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PostingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    Intent galleryIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(galleryIntent , 1);
                }
                else
                {
                    ActivityCompat.requestPermissions(PostingActivity.this,
                            new String[] {
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                            }, 11);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.removeItem(R.id.menu_main);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id)
        {
            case R.id.menu_contacts:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    intent = new Intent(this,ContactsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    ActivityCompat.requestPermissions(this,
                            new String[] {
                                    Manifest.permission.READ_CONTACTS,
                            }, 12);
                }
                break;
            case R.id.menu_history:
                intent = new Intent(this,HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.menu_logout:
                VKSdk.logout();
                intent = new Intent(PostingActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_main:
                startActivity(new Intent(this,PostingActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void makePost(VKAttachments att, String msg, final int ownerId) {
        VKParameters parameters = new VKParameters();
        parameters.put(VKApiConst.OWNER_ID, String.valueOf(ownerId));
        parameters.put(VKApiConst.ATTACHMENTS, att);
        parameters.put(VKApiConst.MESSAGE, msg);
        VKRequest post = VKApi.wall().post(parameters);
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                // post was added
            }
            @Override
            public void onError(VKError error) {
                // error
            }
        });
    }

    int getMyId() {
        final VKAccessToken vkAccessToken = VKAccessToken.currentToken();
        return vkAccessToken != null ? Integer.parseInt(vkAccessToken.userId) : 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 11 && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent , 1);
            }

        }
        if (requestCode == 12 && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, ContactsActivity.class));
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (null != data && resultCode == RESULT_OK) {

                    picture.setImageURI(data.getData());
                    picture.setTag(GetFilePathFromDevice.getPath(this,data.getData()));
                    //Do whatever that you desire here. or leave this blank

                }
                break;
            default:
                break;
        }
    }
}
