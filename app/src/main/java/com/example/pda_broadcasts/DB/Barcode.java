package com.example.pda_broadcasts.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Barcode {

    @PrimaryKey(autoGenerate = true)
    int mId;

    @ColumnInfo(name = "mBarCode")
    String mBarCode;

    public Barcode(String mBarCode) {
        this.mBarCode = mBarCode;
    }

    public String getBarCode() {
        return mBarCode;
    }

    public void setBarCode(String mBarCode) {
        this.mBarCode = mBarCode;
    }
}
