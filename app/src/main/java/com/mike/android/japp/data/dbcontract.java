package com.mike.android.japp.data;

/**
 * Created by LENOVO V310 on 7/1/2018.
 */
import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public final class dbcontract {
    // empty construct to prevent someone from accidently instantianting the contract class
    private dbcontract(){}

    public static final String CONTENT_AUTHORITY = "com.mike.android.japp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "journal";

//class for constants to be placed in the table
public static final class Entry implements BaseColumns {
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
    public final static String TABLE_NAME = "journal";

    public final static String _ID = BaseColumns._ID;

    /**
     * Name of the pet.
     *
     * Type: TEXT
     */
    public final static String COLUMN_ENTRY_TITLE ="name";

    /**
     * Breed of the pet.
     *
     * Type: TEXT
     */
    public final static String COLUMN_ENTRY = "entry";


}
}
