package com.example.bookswap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bookswap.BookModel;
import com.example.bookswap.R;

import java.util.ArrayList;
import java.util.List;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bookswap.db";
    private static final int DB_VERSION = 4; // increment because we add 'role'

    // -------------------- USERS --------------------
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_PHONE = "phone";
    public static final String COL_ROLE = "role";

    // -------------------- LOGIN AUDIT --------------------
    public static final String TABLE_LOGINS = "login_audit";
    public static final String COL_LOGIN_ID = "login_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_LOGIN_TIME = "login_time";

    // -------------------- BOOKS --------------------
    public static final String TABLE_BOOKS = "books";
    public static final String COL_BOOK_ID = "book_id";
    public static final String COL_TITLE = "title";
    public static final String COL_AUTHOR = "author";
    public static final String COL_IMAGE = "image";
    public static final String COL_CATEGORY = "category";
    public static final String COL_PHONE_BOOK = "phone";
    public static final String COL_EMAIL_BOOK = "email";

    public UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Ensure admin exists
        ensureAdminExists();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Users table
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_EMAIL + " TEXT UNIQUE," +
                COL_PASSWORD + " TEXT," +
                COL_PHONE + " TEXT," +
                COL_ROLE + " TEXT DEFAULT 'user'" +
                ");";

        // Login audit table
        String createAudit = "CREATE TABLE " + TABLE_LOGINS + " (" +
                COL_LOGIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USER_ID + " INTEGER," +
                COL_LOGIN_TIME + " LONG" +
                ");";

        // Books table
        String createBooks = "CREATE TABLE " + TABLE_BOOKS + " (" +
                COL_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TITLE + " TEXT," +
                COL_AUTHOR + " TEXT," +
                COL_IMAGE + " INTEGER," +
                COL_CATEGORY + " TEXT," +
                COL_PHONE_BOOK + " TEXT," +
                COL_EMAIL_BOOK + " TEXT" +
                ");";

        db.execSQL(createUsers);
        db.execSQL(createAudit);
        db.execSQL(createBooks);

        // Insert default admin
        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // -------------------- DEFAULT ADMIN --------------------
    private void insertDefaultAdmin(SQLiteDatabase db) {
        String adminEmail = "admin@mail.com";
        String adminPassword = "Admin@123"; // default password
        String adminName = "Admin";

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?",
                new String[]{adminEmail});

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, adminName);
            values.put(COL_EMAIL, adminEmail);
            values.put(COL_PASSWORD, adminPassword);
            values.put(COL_PHONE, "");
            values.put(COL_ROLE, "admin");
            db.insert(TABLE_USERS, null, values);
        }
        cursor.close();
    }

    private void ensureAdminExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        insertDefaultAdmin(db);
    }

    // -------------------- USER METHODS --------------------
    public long insertUser(String fullName, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, fullName);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_PHONE, "");
        values.put(COL_ROLE, role != null ? role : "user");
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE email = ?",
                new String[]{email});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?",
                new String[]{email}
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String emailStr = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
            String pass = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
            cursor.close();
            return new User(id, name, emailStr, pass, phone, role);
        }

        return null;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)});
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        User user = new User(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_PASSWORD)),
                c.getString(c.getColumnIndexOrThrow(COL_PHONE)),
                c.getString(c.getColumnIndexOrThrow(COL_ROLE))
        );
        c.close();
        return user;
    }

    // -------------------- LOGIN AUDIT --------------------
    public void recordLogin(int userId, long loginTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_ID, userId);
        cv.put(COL_LOGIN_TIME, loginTime);
        db.insert(TABLE_LOGINS, null, cv);
    }

    // -------------------- BOOK METHODS --------------------
    public long addBook(String title, String author, int imageRes, String category, String phone, String email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_AUTHOR, author);
        cv.put(COL_IMAGE, imageRes);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_PHONE_BOOK, phone);
        cv.put(COL_EMAIL_BOOK, email);
        return db.insert(TABLE_BOOKS, null, cv);
    }

    public List<BookModel> getUserBooks(int userId) {
        List<BookModel> books = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);

        if (c.moveToFirst()) {
            do {
                String placeholderUri = "android.resource://com.example.bookswap/" + R.drawable.image_placeholder;
                BookModel book = new BookModel(
                        c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                        c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                        placeholderUri,
                        c.getString(c.getColumnIndexOrThrow(COL_CATEGORY)),
                        c.getString(c.getColumnIndexOrThrow(COL_PHONE_BOOK)),
                        c.getString(c.getColumnIndexOrThrow(COL_EMAIL_BOOK))
                );
                books.add(book);
            } while (c.moveToNext());
        }
        c.close();
        return books;
    }

    // -------------------- USER MODEL --------------------
    public static class User {
        public int id;
        public String name;
        public String email;
        public String password;
        public String phone;
        public String role;

        public User(int id, String name, String email, String password, String phone, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.role = role;
        }
    }
}
