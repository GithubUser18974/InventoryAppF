package com.example.mohamed_araby.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCT_TABEL =
                "CREATE TABLE "
                        + ProductContract.ProductEntry.TABLE_NAME + " ("
                        + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                        + ProductContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                        + ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                        + ProductContract.ProductEntry.COLUMN_IMAGE + " TEXT ,"
                        + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                        + ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                        + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_PRODUCT_TABEL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_PETS_TABLE = "DROP TABLE IF EXIST " + ProductContract.ProductEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_PETS_TABLE);
        onCreate(db);
    }
}
