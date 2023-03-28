package com.example.pda_broadcasts.DB;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class BarcodeRepository {
    private final BarcodeDao blockDao;
    private final BarcodeDataBase blockDatabase;

    public BarcodeRepository(Context context) {
        this.blockDatabase = BarcodeDataBase.getDatabase(context);
        this.blockDao = blockDatabase.getBlockDao();
    }

    public void insertBarcodes(Barcode... Barcodes) {
        new InsertAsyncTask(blockDao).execute(Barcodes);
    }

    void updateBarcodes(Barcode... Barcodes) {
        new UpdateAsyncTask(blockDao).execute(Barcodes);
    }

    void deleteBarcodes(Barcode... Barcodes) {
        new DeleteAsyncTask(blockDao).execute(Barcodes);
    }

    public List<Barcode> queryExistBarcode(String barcode) {
        return blockDao.queryExistBarcode(barcode);
    }

    public List<Barcode> queryAllBarcode() {
        return blockDao.queryAllBarcode();
    }

    public void deleteAllBarcode(){
        blockDao.deleteAllBarcode();
    }


    static class InsertAsyncTask extends AsyncTask<Barcode, Void, Void> {
        private final BarcodeDao blockDao;

        InsertAsyncTask(BarcodeDao blockDao) {
            this.blockDao = blockDao;
        }

        @Override
        protected Void doInBackground(Barcode... barcodes) {
            blockDao.insertBarcode(barcodes);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Barcode, Void, Void> {
        private final BarcodeDao blockDao;

        UpdateAsyncTask(BarcodeDao blockDao) {
            this.blockDao = blockDao;
        }

        @Override
        protected Void doInBackground(Barcode... barcodes) {
            blockDao.updateBarcode(barcodes);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Barcode, Void, Void> {
        private final BarcodeDao blockDao;

        DeleteAsyncTask(BarcodeDao blockDao) {
            this.blockDao = blockDao;
        }

        @Override
        protected Void doInBackground(Barcode... barcodes) {
            blockDao.deleteBarcode(barcodes);
            return null;
        }
    }

}
