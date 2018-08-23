package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventory.data.Inventory.InventoryEntry;

public class InventoryProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Inventory.CONTENT_AUTHORITY, Inventory.PATH_INVENTORY, PRODUCTS);
        sUriMatcher.addURI(Inventory.CONTENT_AUTHORITY, Inventory.PATH_INVENTORY + "/#", PRODUCT_ID);
    }

    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper((getContext()));
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(Inventory.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = Inventory.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("This is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return Inventory.InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return Inventory.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match " + match);
        }
    }
    private Uri insertProduct(Uri uri, ContentValues values) {
        String nameProduct = values.getAsString(Inventory.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (nameProduct == null) {
            throw new IllegalArgumentException("Product name requires valid");
        }

        Integer priceProduct = values.getAsInteger(Inventory.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (priceProduct != null && priceProduct < 0) {
            throw new IllegalArgumentException("Product price requires valid");
        }

        Integer quantityProduct = values.getAsInteger(Inventory.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantityProduct != null && quantityProduct < 0) {
            throw new IllegalArgumentException("Product quantity requires valid");
        }

        String supplierName = values.getAsString(Inventory.InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product supplier requires valid");
        }

        Integer supplierPhone = values.getAsInteger(Inventory.InventoryEntry.COLUMN_SUPPLIER_CONTACT);
        if (supplierPhone != null && supplierPhone < 0) {
            throw new IllegalArgumentException("Supplier contact requires valid");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(Inventory.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.v("message:", "Failed to insert new row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(Inventory.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = Inventory.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Inventory.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[]
            selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = Inventory.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(Inventory.InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String nameProduct = values.getAsString(Inventory.InventoryEntry.COLUMN_PRODUCT_NAME);
            if (nameProduct == null) {
                throw new IllegalArgumentException("Product name requires valid name");
            }
        }
        if (values.containsKey(Inventory.InventoryEntry.COLUMN_PRODUCT_PRICE)) {
            Integer priceProduct = values.getAsInteger(Inventory.InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (priceProduct != null && priceProduct < 0) {
                throw new
                        IllegalArgumentException("Product price requires valid price");
            }
        }
        if (values.containsKey(Inventory.InventoryEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantityProduct = values.getAsInteger(Inventory.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantityProduct != null && quantityProduct < 0) {
                throw new
                        IllegalArgumentException("Product quantity requires valid number");
            }
        }
        if (values.containsKey(Inventory.InventoryEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String supplierName = values.getAsString(Inventory.InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product supplier requires valid name");
            }
        }
        if (values.containsKey(Inventory.InventoryEntry.COLUMN_SUPPLIER_CONTACT)) {
            Integer supplierPhone = values.getAsInteger(Inventory.InventoryEntry.COLUMN_SUPPLIER_CONTACT);
            if (supplierPhone != null && supplierPhone < 0) {
                throw new
                        IllegalArgumentException("Supplier contact requires valid phone number");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(Inventory.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
