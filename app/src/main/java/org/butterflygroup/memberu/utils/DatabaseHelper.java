package org.butterflygroup.memberu.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MemberU.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_MEMBER_CARDS = "member_cards";
    public static final String TABLE_APP_SETTINGS = "app_settings";
    public static final String TABLE_VISIT_LOGS = "visit_logs";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "email TEXT, "
                + "phone TEXT, "
                + "password_hash TEXT, "
                + "pin TEXT)";
        db.execSQL(createUsers);

        String createCategories = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "category_name TEXT UNIQUE)";
        db.execSQL(createCategories);

        String createMemberCards = "CREATE TABLE " + TABLE_MEMBER_CARDS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "category_id INTEGER, "
                + "merchant_name TEXT, "
                + "member_number TEXT, "
                + "tier TEXT, "
                + "qr_payload TEXT)";
        db.execSQL(createMemberCards);

        String createAppSettings = "CREATE TABLE " + TABLE_APP_SETTINGS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "is_dark_mode INTEGER, "
                + "is_notif_enabled INTEGER)";
        db.execSQL(createAppSettings);

        String createVisitLogs = "CREATE TABLE " + TABLE_VISIT_LOGS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "card_id INTEGER, "
                + "visit_date TEXT)";
        db.execSQL(createVisitLogs);
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (category_name) VALUES ('Gym'), ('Barbershop'), ('Laundry'), ('Lainnya')");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (name, email, phone) VALUES ('Raka', 'raka@email.com', '08123456789')");

        ContentValues card1 = new ContentValues();
        card1.put("user_id", 1);
        card1.put("category_id", 1);
        card1.put("merchant_name", "Gym Fitness Cimahi");
        card1.put("member_number", "MBR-2026-001");
        card1.put("tier", "Gold");
        db.insert(TABLE_MEMBER_CARDS, null, card1);

        ContentValues card2 = new ContentValues();
        card2.put("user_id", 1);
        card2.put("category_id", 2);
        card2.put("merchant_name", "Barbershop Cimahi");
        card2.put("member_number", "MBR-2026-002");
        card2.put("tier", "Silver");
        db.insert(TABLE_MEMBER_CARDS, null, card2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER_CARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISIT_LOGS);
        onCreate(db);
    }

    public Cursor getAllMemberCards() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.*, c.category_name FROM " + TABLE_MEMBER_CARDS + " m " +
                "JOIN " + TABLE_CATEGORIES + " c ON m.category_id = c.id";
        return db.rawQuery(query, null);
    }

    public boolean insertMemberCard(int userId, int categoryId, String merchantName, String memberNumber, String tier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("merchant_name", merchantName);
        values.put("member_number", memberNumber);
        values.put("tier", tier);
        values.put("qr_payload", memberNumber);

        long result = db.insert(TABLE_MEMBER_CARDS, null, values);

        return result != -1;
    }

    public Cursor getMemberCardById(int memberCardId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.*, c.category_name FROM " + TABLE_MEMBER_CARDS + " m " +
                "JOIN " + TABLE_CATEGORIES + " c ON m.category_id = c.id " +
                "WHERE m.id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(memberCardId)});
    }

    public boolean deleteMemberCard(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEMBER_CARDS, "id = ?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }
}