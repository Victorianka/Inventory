package com.example.android.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventory.data.Inventory.InventoryEntry;
import com.example.android.inventory.data.DbHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //to access database
    private DbHelper mInventoryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton add = findViewById(R.id.add_product);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });

        mInventoryHelper = new DbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabase();
    }

    private void displayDatabase() {
        SQLiteDatabase db = mInventoryHelper.getReadableDatabase();

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_SUPPLIER_CONTACT
        };
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) findViewById(R.id.inventory_view);

        try {
            displayView.setText(getString(R.string.contains) + cursor.getCount() + getString(R.string.products));
            displayView.append(
                    InventoryEntry._ID + " | " +
                            InventoryEntry.COLUMN_PRODUCT_NAME + " | " +
                            InventoryEntry.COLUMN_PRODUCT_PRICE + " | " +
                            InventoryEntry.COLUMN_PRODUCT_QUANTITY + " | " +
                            InventoryEntry.COLUMN_PRODUCT_SUPPLIER + " | " +
                            InventoryEntry.COLUMN_SUPPLIER_CONTACT + "\n");

            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_CONTACT);
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName =cursor.getString(nameColumnIndex);
                int currentPrice =cursor.getInt(priceColumnIndex);
                int currentQuantity =cursor.getInt(quantityColumnIndex);
                int currentSupplierName =cursor.getInt(supplierNameColumnIndex);
                int currentSupplierPhone =cursor.getInt(supplierPhoneColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone ));
            }

        } finally {
            cursor.close();
        }
    }
}