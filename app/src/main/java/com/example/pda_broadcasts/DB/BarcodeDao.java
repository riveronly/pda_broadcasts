package com.example.pda_broadcasts.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BarcodeDao {
    @Insert
    void insertBarcode(Barcode... barcodes);

    @Update
    void updateBarcode(Barcode... barcodes);

    @Delete
    void deleteBarcode(Barcode... barcodes);

    @Query("SELECT * FROM barcode ORDER BY mId")
    List<Barcode> queryAllBarcode();

    @Query("DELETE FROM barcode")
    void deleteAllBarcode();

    @Query("SELECT * FROM barcode WHERE mBarCode = :barcode ORDER BY mId")
    List<Barcode> queryExistBarcode(String barcode);
}
