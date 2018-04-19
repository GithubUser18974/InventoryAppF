package com.example.mohamed_araby.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohamed_araby.inventoryapp.data.ProductContract;

public class EditorActivity extends AppCompatActivity {
    private Uri productUri = null;
    private EditText ProductNameEditText;
    private EditText productQuantityEditText;
    private EditText productPriceEditText;
    private EditText supplierNameEditText;
    private EditText supplierMailEditText;
    private EditText supplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ProductNameEditText = findViewById(R.id.product_name_et);
        productQuantityEditText = findViewById(R.id.product_quantity_et);
        productPriceEditText = findViewById(R.id.product_price_et);
        supplierNameEditText = findViewById(R.id.supplier_name_et);
        supplierMailEditText = findViewById(R.id.supplier_email_et);
        supplierPhoneEditText = findViewById(R.id.supplier_phone_et);

        try {
            if (getIntent().getData() != null) {
                productUri = getIntent().getData();
                setTitle(R.string.update_product);
                getProductsData();
            }
        } catch (NullPointerException ex) {
            Log.e("EditorActivity", "Empty Products ");
            setTitle(R.string.add_product);
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("EditorActivity", e.getMessage());
            setTitle(R.string.add_product);
        }
    }

    private ContentValues getInputData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, ProductNameEditText.getText().toString().trim());
        contentValues.put(ProductContract.ProductEntry.COLUMN_QUANTITY, productQuantityEditText.getText().toString().trim());
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRICE, productPriceEditText.getText().toString().trim());
        contentValues.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameEditText.getText().toString().trim());
        contentValues.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierMailEditText.getText().toString().trim());
        contentValues.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneEditText.getText().toString().trim());
        return contentValues;
    }

    private void clearInputFields() {
        ProductNameEditText.setText("");
        productQuantityEditText.setText("");
        productPriceEditText.setText("");
        supplierNameEditText.setText("");
        supplierMailEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    private boolean isDataExist() {
        if (!ProductNameEditText.getText().toString().trim().equals(""))
            return true;
        else if (!productQuantityEditText.getText().toString().trim().equals(""))
            return true;
        else if (!productPriceEditText.getText().toString().trim().equals(""))
            return true;
        else if (!supplierNameEditText.getText().toString().trim().equals(""))
            return true;
        else if (!supplierMailEditText.getText().toString().trim().equals(""))
            return true;
        else if (!supplierPhoneEditText.getText().toString().trim().equals(""))
            return true;
        else
            return false;
    }

    private void getProductsData() {
        String[] projection = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ProductContract.ProductEntry.CONTENT_URI, projection, null, null, null);
        } catch (Exception ex) {
            Log.e("EditorActivity", ex.getMessage());
        }
        cursor.moveToFirst();
        ProductNameEditText.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        productQuantityEditText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY))));
        productPriceEditText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE))));
        supplierNameEditText.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)));
        supplierMailEditText.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL)));
        supplierPhoneEditText.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)));
    }

    /**
     * updates the product with the new values
     */
    private void updateProduct() {
        try {
            int result = getContentResolver().update(productUri, getInputData(), null, null);
            clearInputFields();
            Toast.makeText(this, "" + result + " Item are Updated", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                if (productUri != null) {
                    updateProduct();
                } else {
                    try {
                        getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, getInputData());
                        clearInputFields();
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException ex) {
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                // when the user presses the up arrow
                if (isDataExist()) {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            };
                    showUnsavedChangesDialog(discardButtonClickListener);
                } else
                    finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isDataExist()) {
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
        } else
            finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are You Sure");
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

}
