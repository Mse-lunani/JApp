package com.mike.android.japp;

/**
 * Created by LENOVO V310 on 7/1/2018.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.mike.android.japp.data.dbcontract.Entry;
public class JournalAdapter extends CursorAdapter{
   
    public JournalAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);


        // Find the columns of pet attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(Entry.COLUMN_ENTRY_TITLE);


        // Read the pet attributes from the Cursor for the current pet
        String entryTitle = cursor.getString(titleColumnIndex);


        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(entryTitle);

    }
}




