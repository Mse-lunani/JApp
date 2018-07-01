package com.mike.android.japp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.LoaderManager;
import android.content.ContentUris;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.widget.AdapterView;
import android.widget.ListView;

import com.mike.android.japp.data.dbcontract.Entry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    
    private static final int ENTRY_LOADER = 0;

    /** Adapter for the ListView */
    JournalAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Edit.class);
                startActivity(intent);


            }
        });

        ListView journalListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        journalListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of entry data in the Cursor.
        // There is no journal data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new JournalAdapter(this, null);
        journalListView.setAdapter(mCursorAdapter);

        journalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, Edit.class);


                Uri currentPetUri = ContentUris.withAppendedId(Entry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current journal.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(ENTRY_LOADER, null, this);
    }

    private void deleteAllRecords() {
        int rowsDeleted = getContentResolver().delete(Entry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from journal database");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            deleteAllRecords();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                Entry._ID,
                Entry.COLUMN_ENTRY_TITLE,
                Entry.COLUMN_ENTRY };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                Entry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
