package com.khfire22gmail.riple;

import android.support.v4.app.FragmentActivity;
/**
 * Created by Kevin on 9/8/2015.
 */
public class CursorAdapter extends FragmentActivity {

    /*private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupCursorAdapter();
        // Find list and bind to adapter
        ListView lvContacts = (ListView) findViewById(R.id.lvContacts);
        lvContacts.setAdapter(adapter);
        // Initialize the loader with a special ID and the defined callbacks from above
        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), contactsLoader);
    }

    // Defines the asynchronous callback for the contacts data loader
    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                // Create and return the actual cursor loader for the contacts data
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    // Define the columns to retrieve
                    String[] projectionFields =  new String[] { ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts.PHOTO_URI };
                    // Construct the loader
                    CursorLoader cursorLoader = new CursorLoader(SimpleCursor.this,
                            ContactsContract.Contacts.CONTENT_URI, // URI
                            projectionFields,  // projection fields
                            null, // the selection criteria
                            null, // the selection args
                            null // the sort order
                    );
                    // Return the loader for use
                    return cursorLoader;
                }

                // When the system finishes retrieving the Cursor through the CursorLoader,
                // a call to the onLoadFinished() method takes place.
                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    // The swapCursor() method assigns the new Cursor to the adapter
                    adapter.swapCursor(cursor);
                }

                // This method is triggered when the loader is being reset
                // and the loader data is no longer available. Called if the data
                // in the provider changes and the Cursor becomes stale.
                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    // Clear the Cursor we were using with another call to the swapCursor()
                    adapter.swapCursor(null);
                }
            };

    // Create simple cursor adapter to connect the cursor dataset we load with a ListView
    private void setupCursorAdapter() {
        // Column data from cursor to bind views from
        String[] uiBindFrom = { ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI };
        // View IDs which will have the respective column data inserted
        int[] uiBindTo = { R.id.tvName, R.id.ivImage };
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),
        adapter = new SimpleCursorAdapter(
                this, R.layout.item_contact,
                null, uiBindFrom, uiBindTo,
                0);
    }*/
}
