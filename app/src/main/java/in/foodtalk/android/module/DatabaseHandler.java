package in.foodtalk.android.module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.foodtalk.android.object.LoginValue;

public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    //---- master test for git--

    // Database Name
    private static final String DATABASE_NAME = "appDb";

    // Contacts table name
    private static final String TABLE_LOGIN = "loginInfo";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FID = "fId";
    private static final String KEY_SID = "sId";
    private static final String KEY_UID = "uId";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER_NAME = "userName";





    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FID + " TEXT,"
                + KEY_SID + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_USER_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        Log.d("DatabaseHandler", "onCreate");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        /*if (newVersion == 2){
            db.execSQL("ALTER TABLE " + TABLE_LOGIN + " ADD "+KEY_EMAIL+" TEXT");
            db.execSQL("ALTER TABLE " + TABLE_LOGIN + " ADD "+KEY_CITY_ID+" TEXT");
        }*/

        Log.d("DatabaseHandler", "onUpgrade"+" oldV "+ oldVersion+" newV "+newVersion);

        // Create tables again
        onCreate(db);
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


        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
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