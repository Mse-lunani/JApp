package com.mike.android.japp.data;

/**
 * Created by LENOVO V310 on 7/1/2018.
 */
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.mike.android.japp.data.dbcontract.Entry;


public class dbprovider extends ContentProvider {
    public static final String LOG_TAG = dbprovider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int yolo = 1;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int yolo_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(dbcontract.CONTENT_AUTHORITY, dbcontract.PATH, yolo);
        sUriMatcher.addURI(dbcontract.CONTENT_AUTHORITY, dbcontract.PATH + "/#", yolo_ID);

    }
    private dbhelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new dbhelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case yolo:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case yolo_ID:

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }





    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case yolo:
                return Entry.CONTENT_LIST_TYPE;
            case yolo_ID:
                return Entry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case yolo:
                return insertJournal(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertJournal(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(Entry.COLUMN_ENTRY_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }


        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(Entry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case yolo:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            case yolo_ID:
                // Delete a single row given by the ID in the URI
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case yolo:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case yolo_ID:

                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link Entry#COLUMN_ENTRY_TITLE} key is present,
        // check that the name value is not null.
        if (values.containsKey(Entry.COLUMN_ENTRY_TITLE)) {
            String name = values.getAsString(Entry.COLUMN_ENTRY_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(Entry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

}
