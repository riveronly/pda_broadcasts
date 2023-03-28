package com.example.pda_broadcasts.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Barcode.class}, version = 1, exportSchema = false)
public abstract class BarcodeDataBase extends RoomDatabase {
    private static BarcodeDataBase INSTANCE;

    static synchronized BarcodeDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BarcodeDataBase.class, "Barcode_DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public abstract BarcodeDao getBlockDao();
}
