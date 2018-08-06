package com.example.android.inventory.data;

import android.provider.BaseColumns;

public class Inventory {

    public Inventory() {}

    public final static class InventoryEntry implements BaseColumns {

        public final static String TABLE_NAME = "product";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "product_name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "product_supplier";
        public final static String COLUMN_SUPPLIER_CONTACT = "supplier_contact";
    }
}
