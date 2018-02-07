package com.example.userunknown.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vk.sdk.VKSdk;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Status");

        DataBaseClass db = new DataBaseClass(this);

        SharedPreferences prefs = getSharedPreferences("testTask", Context.MODE_PRIVATE);
        Cursor cursor = db.loadAllHistory(prefs.getInt("id_user",-1)+"");
        cursor.moveToFirst();

        TableLayout layout = (TableLayout) findViewById(R.id.tableLayout_status);
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);


        if(cursor.getCount()>0) {
            do {
                String activity = cursor.getString(cursor.getColumnIndex(db.KEY_ACTIVITY));
                String date = cursor.getString(cursor.getColumnIndex(db.KEY_DATE));
                String ip = cursor.getString(cursor.getColumnIndex(db.KEY_IP));

                TableRow row = new TableRow(this);
                row.setLayoutParams(tableParams);
                TextView tvactivity = new TextView(this);
                tvactivity.setTextSize(18);
                tvactivity.setText(activity);
                row.addView(tvactivity);
                TextView tvdate = new TextView(this);
                tvactivity.setTextSize(18);
                tvdate.setText(date);
                row.addView(tvdate);
                TextView tvip = new TextView(this);
                tvactivity.setTextSize(18);
                tvip.setText(ip);
                row.addView(tvip);
                layout.addView(row);

            } while (cursor.moveToNext());
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.removeItem(R.id.menu_history);
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
                intent = new Intent(this,LoginActivity.class);
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
}
