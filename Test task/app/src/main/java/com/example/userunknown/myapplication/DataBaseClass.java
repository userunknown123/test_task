package com.example.userunknown.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by userunknown on 05.02.2018.
 */

public class DataBaseClass extends SQLiteOpenHelper {

    final static String databaseName = "usersDB";
    final static int databaseVersion = 1;

    public static final String KEY_ROWID = "rowid _id";

    public static final String TABLE_USERS = "users";
    public static final String TABLE_HISTORY = "History";


    public static final String ID_USER = "id_user";
    public static final String ID_VK = "id_vk";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ACTIVITY = "activity";
    public static final String KEY_DATE = "time";
    public static final String KEY_IP = "ip";

    Context appContext;

    private SQLiteDatabase db;

    public DataBaseClass(Context context)
    {
        super(context, databaseName, null, databaseVersion);
        appContext = context;
        try {
            copyDataBase(databaseName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db = SQLiteDatabase.openDatabase(appContext.getFilesDir().getPath() + "/databases/" + databaseName, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void copyDataBase(String dbname) throws IOException
    {
        if(new File(appContext.getFilesDir().getPath() + "/databases/" + dbname).exists())
        {
            return;
        }
        InputStream myInput = appContext.getAssets().open(dbname);

        File db_folder = new File(appContext.getFilesDir().getPath() + "/databases/");
        if(!db_folder.exists())
            db_folder.mkdir();

        String outFileName =  appContext.getFilesDir().getPath() + "/databases/" + dbname;

        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[102400];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public int AddUser(String username, String password, int id_vk)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERNAME, username);
        cv.put(KEY_PASSWORD, password);
        cv.put(ID_VK,id_vk);

        db.insert(TABLE_USERS, null, cv);

        return CheckUser(username,password);
    }


    public int CheckUser(String login,String password)
    {
        String selectQuery = "SELECT * FROM "+TABLE_USERS+" WHERE "+KEY_USERNAME+" = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] { login });
        if(cursor.getCount()>0)
        {
            selectQuery = "SELECT * FROM "+TABLE_USERS+" WHERE "+KEY_USERNAME+" = ? and "+KEY_PASSWORD+" = ?";
            cursor = db.rawQuery(selectQuery, new String[] { login,password });
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                return cursor.getInt(cursor.getColumnIndex(ID_USER));
            }
            else
            {
                return -1;//неверный пароль
            }
        }
        else
        {
             return -2;//пользователя нет в базе
        }


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addHistory(String activity,String date, int id_user)
    {
        ContentValues cv = new ContentValues();
        cv.put(ID_USER, id_user);
        cv.put(KEY_ACTIVITY,activity);
        cv.put(KEY_DATE,date);
        cv.put(KEY_IP,getLocalIpAddress());

        db.insert(TABLE_HISTORY, null, cv);
    }

    public Cursor loadAllHistory(String id_user)
    {

        String selectQuery = "SELECT * FROM "+TABLE_HISTORY+" WHERE "+ID_USER+" = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] { id_user });
        cursor.moveToFirst();
        return cursor;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("log_ip", "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("log_ip", ex.toString());
        }
        return null;
    }
}