package com.example.bookswap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "books.db";
    public static final int DB_VERSION = 9;

    // ---------------- BOOK TABLE ----------------
    public static final String TABLE_BOOKS = "books";
    public static final String BOOK_ID = "id";
    public static final String BOOK_TITLE = "title";
    public static final String BOOK_AUTHOR = "author";
    public static final String BOOK_IMAGE = "image";   // string uri
    public static final String BOOK_CATEGORY = "category";
    public static final String BOOK_PHONE = "phone";
    public static final String BOOK_EMAIL = "email";
    public static final String BOOK_DESC = "description";

    // ---------------- USER TABLE ----------------
    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_ROLE = "role";
    public static final String USER_JWT = "jwt_token";

    // ---------------- AUDIT TABLE ----------------
    public static final String TABLE_AUDIT = "login_audit";
    public static final String AUDIT_ID = "id";
    public static final String AUDIT_USERID = "user_id";
    public static final String AUDIT_TIME = "login_time";

    // ---------------- NOTIFICATIONS ----------------
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String NOTIF_ID = "notif_id";
    public static final String NOTIF_TITLE = "title";
    public static final String NOTIF_TIME = "time";
    public static final String NOTIF_ICON = "icon";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // BOOK TABLE
        db.execSQL(
                "CREATE TABLE " + TABLE_BOOKS + " (" +
                        BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BOOK_TITLE + " TEXT, " +
                        BOOK_AUTHOR + " TEXT, " +
                        BOOK_IMAGE + " TEXT, " +
                        BOOK_CATEGORY + " TEXT, " +
                        BOOK_PHONE + " TEXT, " +
                        BOOK_EMAIL + " TEXT, " +
                        BOOK_DESC + " TEXT)"
        );

        // USER TABLE
        db.execSQL(
                "CREATE TABLE " + TABLE_USERS + " (" +
                        USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        USER_NAME + " TEXT, " +
                        USER_EMAIL + " TEXT UNIQUE, " +
                        USER_PASSWORD + " TEXT, " +
                        USER_ROLE + " TEXT, " +
                        USER_JWT + " TEXT)"
        );

        // AUDIT TABLE
        db.execSQL(
                "CREATE TABLE " + TABLE_AUDIT + " (" +
                        AUDIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AUDIT_USERID + " INTEGER, " +
                        AUDIT_TIME + " LONG)"
        );

        // NOTIFICATION TABLE
        db.execSQL(
                "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                        NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NOTIF_TITLE + " TEXT, " +
                        NOTIF_TIME + " TEXT, " +
                        NOTIF_ICON + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    // ---------------- USER FUNCTIONS ----------------
    public long insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, name);
        cv.put(USER_EMAIL, email);
        cv.put(USER_PASSWORD, password);
        cv.put(USER_ROLE, "user");
        return db.insert(TABLE_USERS, null, cv);
    }

    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?",
                new String[]{email, password}
        );
    }

    public void updateToken(int userId, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_JWT, token);
        db.update(TABLE_USERS, cv, USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    // ---------------- AUDIT FUNCTIONS ----------------
    public void insertLogin(int userId, long loginTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AUDIT_USERID, userId);
        cv.put(AUDIT_TIME, loginTime);
        db.insert(TABLE_AUDIT, null, cv);
    }

    // ---------------- BOOK FUNCTIONS ----------------
    public boolean addBook(String title, String author, String imagePath,
                           String category, String phone, String email, String description) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(BOOK_TITLE, title);
        cv.put(BOOK_AUTHOR, author);
        cv.put(BOOK_IMAGE, imagePath);
        cv.put(BOOK_CATEGORY, category);
        cv.put(BOOK_PHONE, phone);
        cv.put(BOOK_EMAIL, email);
        cv.put(BOOK_DESC, description);

        long result = db.insert(TABLE_BOOKS, null, cv);

        return result != -1;
    }

    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);
    }

    public Cursor getAllBooksByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_BOOKS + " WHERE " + BOOK_EMAIL + "=?",
                new String[]{email}
        );
    }

    public List<BookModel> fetchBooksByEmail(String email) {

        List<BookModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_BOOKS + " WHERE " + BOOK_EMAIL + "=?",
                new String[]{email}
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_TITLE));
                    String author = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_AUTHOR));
                    String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_IMAGE));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_CATEGORY));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_PHONE));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_EMAIL));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_DESC));

                    BookModel book = new BookModel(
                            title,
                            author,
                            imageUri,
                            category,
                            phone,
                            userEmail
                    );

                    list.add(book);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    // ---------------- NOTIFICATIONS ----------------
// ---------------- NOTIFICATIONS ----------------
    public boolean addNotification(String title, long timeMillis, int icon) { // updated to long
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NOTIF_TITLE, title);
        cv.put(NOTIF_TIME, timeMillis); // store as long instead of string
        cv.put(NOTIF_ICON, icon);
        return db.insert(TABLE_NOTIFICATIONS, null, cv) != -1;
    }

    public Cursor getAllNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + NOTIF_ID + " DESC",
                null
        );
    }

    // ---------------- ADMIN FUNCTIONS ----------------

    // Get all users
    public Cursor adminGetAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    // Search user by email
    public Cursor adminSearchUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + USER_EMAIL + "=?",
                new String[]{email}
        );
    }

    // Create user from admin
    public long adminCreateUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, name);
        cv.put(USER_EMAIL, email);
        cv.put(USER_PASSWORD, password);
        cv.put(USER_ROLE, role);
        return db.insert(TABLE_USERS, null, cv);
    }
    // ---------------- BOOK DELETE ----------------
    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_BOOKS, BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
        return rows > 0;
    }

    // Delete user
    public boolean adminDeleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_USERS, USER_ID + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }
}
