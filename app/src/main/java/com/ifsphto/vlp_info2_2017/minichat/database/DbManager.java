package com.ifsphto.vlp_info2_2017.minichat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ifsphto.vlp_info2_2017.minichat.object.Message;
import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;

import java.util.ArrayList;

/**
 * Created by vinibrenobr11 on 11/11/2017 at 18:10:27
 */
public class DbManager extends SQLiteOpenHelper {

    private String table;

    public DbManager(Context context, String table) {
        super(context, Tags.Database.MSG_DATABASE_NAME, null, 1);
        this.table = table;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(Tags.Database.CREATE.replace("?", table));
    }

    public ArrayList<Message> select(String user) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(table, null, null, null
                , null, null, "id");

        ArrayList<Message> messages = new ArrayList<>();

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            messages.add(new Message(cursor.getString(1).equals(user), cursor.getString(2)));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return messages;
    }

    public boolean insert(String author, String message) {

        ContentValues cv = new ContentValues();
        cv.put(Tags.Database.MSG_COLUMN_AUTHOR, author);
        cv.put(Tags.Database.MSG_COLUMN_MESSAGE, message);

        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.insert(table, null, cv) != -1;
        }
    }
}
