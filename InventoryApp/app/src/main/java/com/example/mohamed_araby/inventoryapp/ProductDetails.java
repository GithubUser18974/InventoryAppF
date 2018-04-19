package com.example.mohamed_araby.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed_araby.inventoryapp.data.ProductContract;

public class ProductDetails extends AppCompatActivity {
    private int pQuantity;
    private String supplierEmail;
    private String supplierPhone;
    private TextView pName;
    private TextView pQuantuityText;
    private TextView pPriceText;
    private TextView supplierNameText;
    private TextView supplierMailText;
    private TextView supplierPhoneText;
    private Button increment;
    private Button decrement;
    private Button deleteProduct;
    private Button phoneCall;
    private Button sendEmail;
    private Cursor productCursor;
    private Uri productUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        pName = findViewById(R.id.details_product_name_tv);
        pQuantuityText = findViewById(R.id.details_product_quantity_tv);
        pPriceText = findViewById(R.id.details_product_price_tv);
        supplierNameText = findViewById(R.id.details_supplier_name_tv);
        supplierMailText = findViewById(R.id.details_supplier_email_tv);
        supplierPhoneText = findViewById(R.id.details_supplier_phone_tv);
        increment = findViewById(R.id.details_product_quantity_dec_btn);
        decrement = findViewById(R.id.details_product_quantity_inc_btn);
        deleteProduct = findViewById(R.id.delete_product);
        phoneCall = findViewById(R.id.call_supplier);
        sendEmail = findViewById(R.id.email_supplier);

        final Uri uri = getIntent().getData();
        productUri = uri;
        updateViewsWithData(uri);
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getContentResolver().delete(uri, null, null);
                                finish();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pQuantity--;
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductContract.ProductEntry.COLUMN_QUANTITY, pQuantity);
                try {
                    getContentResolver().update(uri, contentValues, null, null);
                    pQuantuityText.setText(String.valueOf(pQuantity));
                } catch (IllegalArgumentException ex) {
                    pQuantity++;
                    Toast.makeText(ProductDetails.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pQuantity++;
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductContract.ProductEntry.COLUMN_QUANTITY, pQuantity);
                try {
                    getContentResolver().update(uri, contentValues, null, null);
                    pQuantuityText.setText(String.valueOf(pQuantity));
                } catch (IllegalArgumentException ex) {
                    Toast.makeText(ProductDetails.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall(supplierPhone);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String arr[] = {supplierEmail};
                composeEmail(arr);
            }
        });
    }

    private Cursor query(Uri uri) {
        String[] projection = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, projection,
                    null, null, null);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return cursor;
    }

    private void updateViewsWithData(Uri uri) {
        productCursor = query(uri);
        productCursor.moveToFirst();
        pName.setText(productCursor.getString(productCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        pQuantity = productCursor.getInt(productCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY));
        pQuantuityText.setText(String.valueOf(pQuantity));
        pPriceText.setText(productCursor.getString(productCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE)));
        supplierNameText.setText(productCursor.getString(productCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)));
        supplierEmail = productCursor.getString(productCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL));
        supplierMailText.setText(supplierEmail);
        supplierPhone = productCursor.getString(productCursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
        supplierPhoneText.setText(supplierPhone);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewsWithData(productUri);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_deletion));
        builder.setPositiveButton(getString(R.string.yes), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void makePhoneCall(String phone) {
        String phoneNumber = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void composeEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_add) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.setData(productUri);
            startActivity(intent);
        } else
            onBackPressed();
        return true;
    }
}
