package com.emergency.services.db;

import com.emergency.services.db.DBInfo.AlarmTable;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class AlarmProvider extends ContentProvider {
    private static final String DATABASE_NAME = "AlienAlarm.db";
    private static final int DATABASE_VERSION = 1;
    private static final int ALARM_TABLE = 1;

    private DatabaseHelper mDBHelper;

    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(DBInfo.AUTHORITY, DBInfo.AlarmTable.TABLE_NAME, ALARM_TABLE);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DatabaseHelper(getContext(), DATABASE_NAME);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        String type = null;
        switch (sUriMatcher.match(uri)) {
        case ALARM_TABLE:
            type = AlarmTable.CONTENT_TYPE;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return type;
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
        case ALARM_TABLE:
            tableName = AlarmTable.TABLE_NAME;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return tableName;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getTableName(uri));
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long rowId = db.insert(getTableName(uri), null, values);
        if (rowId == -1) {
            throw new SQLException("Failed to insert");
        } else {
            notifyChange(uri, null);
            Uri currentUri = ContentUris.withAppendedId(uri, rowId);
            return currentUri;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int result = db.delete(AlarmTable.TABLE_NAME, selection, selectionArgs);
        if (result > 0) {
            notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int result = db.update(getTableName(uri), values, selection, selectionArgs);
        if (result > 0) {
            notifyChange(uri, null);
        }
        return result;
    }

    private void notifyChange(Uri uri, ContentObserver observer) {
        getContext().getContentResolver().notifyChange(uri, observer);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createAlarmTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        private void createAlarmTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + AlarmTable.TABLE_NAME + "( " + AlarmTable._ID + " INTEGER PRIMARY KEY,"//
                    + AlarmTable.NAME + " TEXT,"//
                    + AlarmTable.IS_ENABLE + " INTEGER,"//
                    + AlarmTable.HOUR + " INTEGER,"//
                    + AlarmTable.MINUTE + " INTEGER,"//
                    + AlarmTable.TIMES + " INTEGER,"//
                    + AlarmTable.INTERVAL + " INTEGER,"//
                    + AlarmTable.REPEATABILTTY + " INTEGER,"//
                    + AlarmTable.VIBRATE + " INTEGER,"//
                    + AlarmTable.RINGTONE + " TEXT);";
            db.execSQL(sql);
        }
    }
}
