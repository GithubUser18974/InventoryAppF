package com.example.mohamed_araby.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ProductProvider extends ContentProvider {
    private static final int PRODUCTS = 1;
    private static final int PRODUCT_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCTS);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    private ProductDbHelper productDbHelper;

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = productDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match) {
            case PRODUCTS:
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.TABLE_NAME, selectionArgs, selection,
                        null, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("unknown query " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Cannot query unknown query " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = productDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Error " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        int result;
        switch (match) {
            case PRODUCTS:
                result = updateProduct(uri, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = updateProduct(uri, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(" unknown query " + uri);
        }
        return result;
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null || name.length() == 0)
                throw new IllegalArgumentException("Enter Name Please");
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0)
                throw new IllegalArgumentException("quantity must be  less than or equal to Zero");
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
            if (price == null || price < 1)
                throw new IllegalArgumentException("Price cannot be less than or equal to Zero");
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null || supplier.length() == 0)
                throw new IllegalArgumentException("Enter Supplier name please");
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierMail = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierMail == null || supplierMail.length() == 0)
                throw new IllegalArgumentException("Enter Supplier Email Please");
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhone = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null || supplierPhone.length() == 0)
                throw new IllegalArgumentException("Enter Supplier Phone Please");
        }

        if (contentValues.size() == 0)
            return 0;

        SQLiteDatabase sqLiteDatabase = productDbHelper.getWritableDatabase();
        int rowsUpdated = sqLiteDatabase.update(ProductContract.ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Enter Product name");

        Integer quantity = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 1)
            throw new IllegalArgumentException(" quantity must not be  less than or equal to Zero");

        Integer price = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
        if (price == null || price < 1)
            throw new IllegalArgumentException("Price cannot be less than or equal to Zero");

        String supplier = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null || supplier.length() == 0)
            throw new IllegalArgumentException("Enter Supplier name please");

        String supplierMail = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierMail == null || supplierMail.length() == 0)
            throw new IllegalArgumentException("Enter Supplier Email please");

        String supplierPhone = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone == null || supplierPhone.length() == 0)
            throw new IllegalArgumentException("Enter Supplier Phone Number please");

        SQLiteDatabase sqLiteDatabase = productDbHelper.getWritableDatabase();
        long resultID = sqLiteDatabase.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);
        if (resultID == -1) {
            Log.e(getContext().toString(), "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, resultID);
    }
}
