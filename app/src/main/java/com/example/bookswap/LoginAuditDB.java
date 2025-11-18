package com.example.bookswap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginAuditDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "audit.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_AUDIT = "login_audit";

    public static final String COL_ID = "id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_LOGIN_TIME = "login_time";
    public static final String COL_LOGOUT_TIME = "logout_time";

    public LoginAuditDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String create = "CREATE TABLE " + TABLE_AUDIT + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USER_ID + " INTEGER," +
                COL_LOGIN_TIME + " INTEGER," +      // FIXED
                COL_LOGOUT_TIME + " INTEGER" +      // FIXED
                ");";

        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIT);
        onCreate(db);
    }

    // Insert login event
    public long insertLogin(int userId, long loginTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_USER_ID, userId);
        cv.put(COL_LOGIN_TIME, loginTime);
        cv.put(COL_LOGOUT_TIME, 0);

        return db.insert(TABLE_AUDIT, null, cv);
    }

    // Safely update logout time for the last login record
    public void updateLogoutTime(int userId, long logoutTime) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(
                "UPDATE " + TABLE_AUDIT +
                        " SET logout_time = ? " +
                        "WHERE id = (SELECT id FROM " + TABLE_AUDIT +
                        " WHERE user_id = ? ORDER BY id DESC LIMIT 1)",
                new Object[]{logoutTime, userId}
        );
    }

    // Get last login record
    public Cursor getLastLogin(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_AUDIT +
                        " WHERE user_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{String.valueOf(userId)}
        );
    }
}
