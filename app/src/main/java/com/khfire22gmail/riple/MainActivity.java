package com.khfire22gmail.riple;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.khfire22gmail.riple.activities.AboutActivity;
import com.khfire22gmail.riple.activities.SettingsActivity;
import com.khfire22gmail.riple.activities.TitleActivity;
import com.khfire22gmail.riple.slider.SlidingTabLayout;
import com.khfire22gmail.riple.slider.ViewPagerAdapter;
import com.khfire22gmail.riple.utils.MessageService;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.SinchClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ViewPager mPager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Riple", "Drops", "Trickle", "Friends"};
    int numOfTabs = 4;
    private AutoCompleteTextView dropDescriptionView;
    private SinchClient sinchClient;
    private View root_layout;
    private String dropDescription;
    private ParseFile parseProfilePicture;
    private String displayName;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab_create_drop);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                showPopup(view);
                ParseUser currentUser = ParseUser.getCurrentUser();
                parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
                displayName = (String) currentUser.get("displayName");

                if (parseProfilePicture == null && displayName == null) {
                    Toast.makeText(getApplicationContext(), "Please upload a picture and set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (parseProfilePicture == null) {
                    Toast.makeText(getApplicationContext(), "Please upload a picture first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);

                } else if (displayName == null) {
                    Toast.makeText(getApplicationContext(), "Please set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);

                } else {
                    createDropDialog();
                }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) myFab.getLayoutParams();
            p.setMargins(0, 0, 0, 0); // get rid of margins since shadow area is now the margin
            myFab.setLayoutParams(p);
        }

       /* // Store the new facebook User on parse

        ParseUser currentUser = ParseUser.getCurrentUser();
        String displayName =  currentUser.getString("displayName");

        if (displayName == null); {
            storeFacebookUserOnParse();
        }*/

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // mViewPager.setCurrentItem(R.layout.fragment_drop_tab);
        // mViewPager.setCurrentItem(position);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, numOfTabs);

        // Assigning ViewPager View and setting the adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(adapter);

        // Sets default tab on app load
        mPager.setCurrentItem(0);

        // Allow fragments to stay in memory
        mPager.setOffscreenPageLimit(3);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mPager);
    }

    public void createDropDialog() {

        final View view = getLayoutInflater().inflate(R.layout.activity_create_drop, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Post a new Drop");

        final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.drop_description);

        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dropDescription = input.getText().toString();
                int dropTextField = input.getText().length();

                if (dropTextField > 0) {
                    try {
                        createDrop(dropDescription);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "This is a fairly short Drop, try " +
                            "adding a little more description to it before it's posted.", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Take user input and post the Drop
    public void createDrop(String dropDescription) throws InterruptedException {

        final ParseObject drop = new ParseObject("Drop");
        final ParseUser currentUser = ParseUser.getCurrentUser();

        if(dropDescription != null) {

            drop.put("authorPointer", currentUser);
            drop.put("description", dropDescription);
            drop.saveInBackground(new SaveCallback() {// saveInBackground first and then run relation
                @Override
                public void done(ParseException e) {
                    ParseRelation<ParseObject> relationCreatedDrops = currentUser.getRelation("createdDrops");
                    relationCreatedDrops.add(drop);
                    currentUser.saveInBackground();

                    ParseRelation<ParseObject> relationHasRelationTo = currentUser.getRelation("hasRelationTo");
                    relationHasRelationTo.add(drop);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "You have posted a new Drop!", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);

//                                RipleTabFragment localFrag = (RipleTabFragment) getSupportFragmentManager().findFragmentById(R.id.riple_layout);
//                                localFrag.loadRipleItemsFromParse();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SharedPreferences tipsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isChecked = tipsSharedPreferences.getBoolean("allTipsBoolean", true);
        MenuItem checkBox = menu.findItem(R.id.tips);
        checkBox.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.settingsButton) {
            Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }

        if (id == R.id.aboutButton) {
            Intent intentSettings = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intentSettings);
            return true;
        }

        if (id == R.id.tips) {
            item.setChecked(!item.isChecked());

            SharedPreferences.Editor editor = null;
            if (item.isChecked()) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                editor = sharedPreferences.edit();
                editor.putBoolean("allTipsBoolean", true);
                editor.apply();
                Toast.makeText(MainActivity.this, "All tips will be displayed.", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                editor = sharedPreferences.edit();
                editor.putBoolean("allTipsBoolean", false);
                editor.apply();
                Toast.makeText(MainActivity.this, "Tips will no longer be displayed.", Toast.LENGTH_LONG).show();
            }
                return true;
        }

            //noinspection SimplifiableIfStatement
            if (id == R.id.logoutButton) {
                ParseUser.logOut();
                Intent intentLogout = new Intent(getApplicationContext(), TitleActivity.class);
                startActivity(intentLogout);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

    /*public void savePreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }*/







    @Override
    public void onClick(View v) {

    }
}





    /*private void logout() {
        // Log the user out
        ParseUser.logOut();
        //todo Turn off Sinch functions upon logout of Riple
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();

        // Go to the login view
        Intent intent = new Intent(getApplicationContext(), TitleActivity.class);
        startActivity(intent);
    }*/

