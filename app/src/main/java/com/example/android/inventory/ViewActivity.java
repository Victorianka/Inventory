package com.example.android.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.Inventory;

public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView mProductNameView;
    private TextView mProductPriceView;
    private TextView mProductQuantityView;
    private TextView mProductSupplierNameView;
    private TextView mProductSupplierContactView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        mProductNameView = findViewById(R.id.product_name_view_text);
        mProductPriceView = findViewById(R.id.product_price_view_text);
        mProductQuantityView = findViewById(R.id.product_quantity_view_text);
        mProductSupplierNameView = findViewById(R.id.product_supplier_name_view_text);
        mProductSupplierContactView = findViewById(R.id.product_supplier_phone_number_view_text);
//Have you heard of ButterKnife library? It can dramatically reduce the amount of code you need to write when manipulating the views. I would encourage you to check it out. My recent YouTube tutorial might be able to help with that.
//(https://www.youtube.com/watch?v=NGVDvFEwpRM)
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            invalidateOptionsMenu();
        } else {
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        Log.d("message", "onCreate ViewActivity");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Inventory.InventoryEntry._ID,
                Inventory.InventoryEntry.COLUMN_PRODUCT_NAME,
                Inventory.InventoryEntry.COLUMN_PRODUCT_PRICE,
                Inventory.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                Inventory.InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                Inventory.InventoryEntry.COLUMN_SUPPLIER_CONTACT
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
        }
        final int idColumnIndex = cursor.getColumnIndex(Inventory.InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(Inventory.InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(Inventory.InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Inventory.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(Inventory.InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(Inventory.InventoryEntry.COLUMN_SUPPLIER_CONTACT);

        String currentName = cursor.getString(nameColumnIndex);
        final int currentPrice = cursor.getInt(priceColumnIndex);
        final int currentQuantity = cursor.getInt(quantityColumnIndex);
        String currentSupplierName = cursor.getString(supplierNameColumnIndex);
        final int currentSupplierContact = cursor.getInt(supplierPhoneColumnIndex);

        mProductNameView.setText(currentName);
        mProductPriceView.setText(Integer.toString(currentPrice));
        mProductQuantityView.setText(Integer.toString(currentQuantity));
        mProductSupplierNameView.setText(currentSupplierName);
        mProductSupplierContactView.setText(Integer.toString(currentSupplierContact));

        Button productDecreaseButton = findViewById(R.id.decrease_button);
        productDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseCount(idColumnIndex, currentQuantity);
            }
        });

        Button productIncreaseButton = findViewById(R.id.increase_button);
        productIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCount(idColumnIndex, currentQuantity);
            }
        });

        Button productDeleteButton = findViewById(R.id.delete_button);
        productDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        Button phoneButton = findViewById(R.id.phone_button);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = String.valueOf(currentSupplierContact);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void decreaseCount(int productID, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            updateProduct(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_change), Toast.LENGTH_SHORT).show();
            Log.d("Log msg", " - productID " + productID + " - quantity " + productQuantity + " , decreaseCount has been called.");
        } else {
            Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseCount(int productID, int productQuantity) {
        productQuantity = productQuantity + 1;
        if (productQuantity >= 0) {
            updateProduct(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_change), Toast.LENGTH_SHORT).show();
            Log.d("Log msg", " - productID " + productID + " - quantity " + productQuantity + " , decreaseCount has been called.");
        }
    }

    private void updateProduct(int productQuantity) {
        Log.d("message", "updateProduct at ViewActivity");

        if (mCurrentProductUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Inventory.InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(Inventory.InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.saving_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.saving_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.updating_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.updating_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.deleting_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.deleting_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_deleting);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
//Line 211: Any button will dismiss the popup dialog by default, so you don't have to add dialog.dismiss explicitly. If this is the only thing you would like to do, then you can replace the whole OnClickListener with a null, like so:
//setNegativeButton("Cancel", null);
}


