package in.foodtalk.android.module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.foodtalk.android.app.AppController;
import in.foodtalk.android.object.AdValue;
import in.foodtalk.android.object.LoginValue;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "appDb";

    // Contacts table name
    private static final String TABLE_LOGIN = "loginInfo";
    private static final String TABLE_ADWORD = "adword";

    private static final String KEY_AW_ID = "id";
    private static final String KEY_AD_ID = "adId";
    private static final String KEY_AD_VIEW = "adView";
    private static final String KEY_AD_CLICK = "adClick";


    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FID = "fId";
    private static final String KEY_SID = "sId";
    private static final String KEY_UID = "uId";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CITY_ID = "cityId";
    private static final String KEY_RTID = "rTId";

    public Boolean oldDb = false;




    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        String ss = "ALTER TABLE " + TABLE_LOGIN + " ADD "+KEY_RTID+" TEXT DEFAULT "+getUserDetails().get("sessionId")+'"';
        Log.e("table alter",ss );
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FID + " TEXT,"
                + KEY_SID + " TEXT,"
                + KEY_RTID + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_CITY_ID + " TEXT,"
                + KEY_EMAIL +" TEXT"+ ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d("DatabaseHandler", "onCreate");

        String CREATE_ADWORD_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ADWORD + "("
                + KEY_AW_ID + " INTEGER PRIMARY KEY,"
                + KEY_AD_ID + " TEXT,"
                + KEY_AD_VIEW + " INTEGER,"
                + KEY_AD_CLICK + " INTEGER"
                +")";
        //db.execSQL(CREATE_ADWORD_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        if (newVersion == 2){
            db.execSQL("ALTER TABLE " + TABLE_LOGIN + " ADD "+KEY_EMAIL+" TEXT DEFAULT blank");
            db.execSQL("ALTER TABLE " + TABLE_LOGIN + " ADD "+KEY_CITY_ID+" TEXT DEFAULT blank");
        }
        if (newVersion == 3){
            db.execSQL("ALTER TABLE " + TABLE_LOGIN + " ADD "+KEY_RTID+" TEXT DEFAULT blank");
            db.execSQL("UPDATE " + TABLE_LOGIN + " SET "+KEY_RTID+" = "+KEY_SID);
            //Log.e("DatabaseHandler","newVersion 3 "+getUserDetails().get("sessionId"));
           // Log.e("DatabaseHandler","newVersion 3 ");
        }
        Log.d("DatabaseHandler", "onUpgrade"+" oldV "+ oldVersion+" newV "+newVersion);
    }

    public void addAd(AdValue adValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AD_ID, adValue.adId);
        values.put(KEY_AD_VIEW, 0);
        values.put(KEY_AD_CLICK, 0);

        db.insert(TABLE_ADWORD, null, values);
        db.close(); // Closing database connection
    }

    public void increaseAdClick(String adId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.execSQL("UPDATE " + TABLE_ADWORD + " SET "+KEY_AD_CLICK+" = "+KEY_AD_CLICK+1 +" WHERE " + KEY_AD_ID + " = " + adId);
        //db.update(TABLE_ADWORD, values, KEY_UID+" = "+loginValue.uId,null);
        db.close();
    }
    public void increaseAdView(String adId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.execSQL("UPDATE " + TABLE_ADWORD + " SET "+KEY_AD_VIEW+" = "+KEY_AD_VIEW+1 +" WHERE " + KEY_AD_ID + " = " + adId);
        //db.update(TABLE_ADWORD, values, KEY_UID+" = "+loginValue.uId,null);
        db.close();
    }

    public HashMap<String, String> getAdData(){
        HashMap<String,String> adData = new HashMap<String,String>();
        return adData;
    }

    public void resetAdTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_ADWORD, null, null);
        db.close();
    }

    public int getAdRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ADWORD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addUser(LoginValue loginValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FID, loginValue.fbId);
        values.put(KEY_SID, loginValue.sId);
        values.put(KEY_UID, loginValue.uId);
        values.put(KEY_NAME, loginValue.name);
        values.put(KEY_USER_NAME, loginValue.userName);
        values.put(KEY_EMAIL, loginValue.email);
        values.put(KEY_CITY_ID, loginValue.cityId);
        values.put(KEY_RTID, loginValue.rtId);


        db.update(TABLE_LOGIN, values, KEY_UID+" = "+loginValue.uId,null);
        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    public void updateTokens(String uId, String sId, String rToken){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SID, sId);
        values.put(KEY_RTID, rToken);

        db.update(TABLE_LOGIN, values, KEY_UID + " = '" + uId + "'", null);
        db.close(); // Closing database connection

    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("facebooId", cursor.getString(cursor.getColumnIndex(KEY_FID)));
            user.put("sessionId", cursor.getString(cursor.getColumnIndex(KEY_SID)));
            user.put("userId", cursor.getString(cursor.getColumnIndex(KEY_UID)));
            user.put("fullName", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("userName", cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
            user.put("email",cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.put("cityId",cursor.getString(cursor.getColumnIndex(KEY_CITY_ID)));
            user.put("refreshtoken", cursor.getString(cursor.getColumnIndex(KEY_RTID)));
        }
        Log.d("cursor ", cursor+"");
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }
    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }



    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int getDatabaseVersion(){
        SQLiteDatabase db = this.getWritableDatabase();
        // what value do you get here?
        return db.getVersion();
    }
}