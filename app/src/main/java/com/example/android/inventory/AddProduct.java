package com.example.android.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.Inventory.InventoryEntry;

public class AddProduct extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentProductUri;

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mProductSupplier;
    private EditText mSupplierContact;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            Log.d("message", "onTouch");

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        Log.d("message", "onCreate");

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductName = findViewById(R.id.edit_product_name);
        mProductPrice = findViewById(R.id.edit_product_price);
        mProductQuantity = findViewById(R.id.edit_product_quantity);
        mProductSupplier = findViewById(R.id.edit_product_supplier);
        mSupplierContact = findViewById(R.id.edit_supplier_contact);

        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductSupplier.setOnTouchListener(mTouchListener);
        mSupplierContact.setOnTouchListener(mTouchListener);
    }

    private void addProduct() {
        String productName = mProductName.getText().toString();
        String productPrice = mProductPrice.getText().toString();
        String productQuantity = mProductQuantity.getText().toString();
        String productSupplier = mProductSupplier.getText().toString();
        String supplierContact = mSupplierContact.getText().toString();

        if (mCurrentProductUri == null) {
            if (TextUtils.isEmpty(productName)) {
                Toast.makeText(this, getString(R.string.product_name_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPrice)) {
                Toast.makeText(this, getString(R.string.price_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantity)) {
                Toast.makeText(this, getString(R.string.quantity_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productSupplier)) {
                Toast.makeText(this, getString(R.string.supplier_name_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierContact)) {
                Toast.makeText(this, getString(R.string.supplier_phone_required), Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
            values.put(InventoryEntry.COLUMN_SUPPLIER_CONTACT, supplierContact);

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.saving_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.saving_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            if (TextUtils.isEmpty(productName)) {
                Toast.makeText(this, getString(R.string.product_name_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPrice)) {
                Toast.makeText(this, getString(R.string.price_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantity)) {
                Toast.makeText(this, getString(R.string.quantity_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productSupplier)) {
                Toast.makeText(this, getString(R.string.supplier_name_required), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierContact)) {
                Toast.makeText(this, getString(R.string.supplier_phone_required), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
            values.put(InventoryEntry.COLUMN_SUPPLIER_CONTACT, supplierContact);

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.updating_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.updating_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Log.d("Message", "Opened AddProduct");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                addProduct();
                finish();
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddProduct.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_SUPPLIER_CONTACT
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
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_CONTACT);

            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mProductName.setText(currentName);
            mProductPrice.setText(Integer.toString(currentPrice));
            mProductQuantity.setText(Integer.toString(currentQuantity));
            mProductSupplier.setText(currentSupplierName);
            mSupplierContact.setText(Integer.toString(currentSupplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mProductSupplier.setText("");
        mSupplierContact.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_discarding);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
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

