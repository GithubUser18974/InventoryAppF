package com.example.mohamed_araby.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed_araby.inventoryapp.data.ProductContract;

public class ProductCursureAdapter extends CursorAdapter {
    public ProductCursureAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.mlist_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView productName = view.findViewById(R.id.list_product_name);
        TextView productQuatity = view.findViewById(R.id.list_product_quantity);
        TextView productPrice = view.findViewById(R.id.list_product_price);

        int price = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY));
        // store the item position because the cursor will move when displaying new item
        final int position = cursor.getPosition();

        productName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        productQuatity.setText(context.getString(R.string.quantity) + String.valueOf(quantity));
        productPrice.setText(context.getString(R.string.price) + String.valueOf(price));

        Button button = view.findViewById(R.id.list_product_sell_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY));
                int productID = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
                quantity--;
                try {
                    update(productID, quantity, context);
                } catch (IllegalArgumentException ex) {
                    quantity++;
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                int id = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
                Uri productUri = Uri.withAppendedPath(ProductContract.ProductEntry.CONTENT_URI, "/" + id);
                Intent intent = new Intent(context, ProductDetails.class);
                intent.setData(productUri);
                context.startActivity(intent);
            }
        });
    }


    private void update(int id, int quantity, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        Uri uri = Uri.withAppendedPath(ProductContract.ProductEntry.CONTENT_URI, "/" + id);
        context.getContentResolver().update(uri, contentValues, null, null);
    }


}
