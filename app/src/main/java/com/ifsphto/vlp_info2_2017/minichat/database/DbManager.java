package com.ifsphto.vlp_info2_2017.minichat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;

/**
 * Created by vinibrenobr11 on 11/11/2017 at 18:10:27
 */
public class DbManager extends SQLiteOpenHelper {

    public DbManager(Context context) {
        super(context, Tags.Database.MSG_DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
