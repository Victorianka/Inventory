package com.example.android.inventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.example.android.inventory.data.Inventory.InventoryEntry;
import com.example.android.inventory.data.DbHelper;

public class AddProduct extends AppCompatActivity {

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mProductSupplier;
    private EditText mSupplierContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        mProductName = findViewById(R.id.edit_product_name);
        mProductPrice = findViewById(R.id.edit_product_price);
        mProductQuantity = findViewById(R.id.edit_product_quantity);
        mProductSupplier = findViewById(R.id.edit_product_supplier);
        mSupplierContact = findViewById(R.id.edit_supplier_contact);
    }

    private void addProduct() {
        String productName = mProductName.getText().toString().trim();

        String productPrice = mProductPrice.getText().toString().trim();
        int productPriceInt = Integer.parseInt(productPrice);

        String productQuantity = mProductQuantity.getText().toString().trim();
        int productQuantityInt = Integer.parseInt(productQuantity);

        String productSupplier = mProductSupplier.getText().toString().trim();

        String supplierContact = mSupplierContact.getText().toString().trim();
        int supplierContactInt = Integer.parseInt(supplierContact);

        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
        values.put(InventoryEntry.COLUMN_SUPPLIER_CONTACT, supplierContactInt);

        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error encountered", Toast.LENGTH_SHORT).show();
            Log.d("Error", "Inserting product on the title row");
        } else {
            Toast.makeText(this, "Product saved to position " + newRowId, Toast.LENGTH_SHORT).show();
            Log.d("Success", "Product saved");
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
