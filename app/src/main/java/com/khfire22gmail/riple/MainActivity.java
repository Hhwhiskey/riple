package com.khfire22gmail.riple;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.khfire22gmail.riple.ViewPagers.MainSlidingTabLayout;
import com.khfire22gmail.riple.ViewPagers.MainViewPagerAdapter;
import com.khfire22gmail.riple.activities.AboutActivity;
import com.khfire22gmail.riple.activities.SettingsActivity;
import com.khfire22gmail.riple.activities.TitleActivity;
import com.khfire22gmail.riple.utils.MessageService;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.SinchClient;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ViewPager mPager;
    MainViewPagerAdapter adapter;
    MainSlidingTabLayout tabs;
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
    Context mContext;
    SharedPreferences settings;
    private boolean isBoxChecked;
    private MenuItem checkBox;
    private boolean checkTest;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = ParseUser.getCurrentUser();

//        SharedPreferences settings = getSharedPreferences("MY_APP", MODE_PRIVATE);

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

        // Creating The MainViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new MainViewPagerAdapter(getSupportFragmentManager(), Titles, numOfTabs);

        // Assigning ViewPager View and setting the adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(adapter);

        // Sets default tab on app load
        mPager.setCurrentItem(0);

        // Allow fragments to stay in memory
        mPager.setOffscreenPageLimit(3);

        // Assigning the Sliding Tab Layout View
        tabs = (MainSlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new MainSlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mPager);

        //Log User with Crashlytics
        logUser();
    }

    public void showUserTips() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean test = sharedPreferences.getBoolean("postDropTips", true);

        if (test) {
            viewUserTip();
        }
    }

    public void saveTipPreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.putBoolean("allTipsBoolean", false);
        editor.commit();

//        MainActivity mainActivity = new MainActivity();
//        mainActivity.isBoxChecked(false);
    }

    public void viewUserTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);

        builder.setTitle("Post a Drop");
        builder.setMessage("Here, you can post a Drop for everyone to see. A Drop is an idea " +
                "you have to make the world a better place. No matter how big or small your Drop " +
                "is, you can create huge Riples. Post your Drop and then watch the Riples spread.");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveTipPreferences("postDropTips", false);
            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    //Open up a dialog box for the creation of a Drop
//    public void createDropDialog() {
//
//        final View view = getLayoutInflater().inflate(R.layout.activity_create_drop, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
//        builder.setTitle("Post a Drop");
//
//        final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.drop_description);
//
//        builder.setView(view);
//
//        // Set up the buttons
//        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dropDescription = input.getText().toString();
//                int dropTextField = input.getText().length();
//
//                if (dropTextField > 0) {
//                    try {
//                        createDrop(dropDescription);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Try adding some text before your" +
//                            "Drop is posted.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//
//        showUserTips();
//    }

    public void createDropDialog() {

        showSoftKeyboard();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String copiedString = sharedPreferences.getString("storedDropString", "");

        final View view = getLayoutInflater().inflate(R.layout.activity_create_drop, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Post a new Drop");

        final EditText input = (EditText) view.findViewById(R.id.drop_description);

        input.setText(copiedString);


        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dropDescription = input.getText().toString();
                int dropTextField = input.getText().length();

                if (dropTextField > 49) {
                    try {
                        createDrop(dropDescription);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Hold on...This is a fairly short Drop. Try " +
                    "having at least 50 characters before you post.", Toast.LENGTH_LONG).show();

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("storedDropString", dropDescription);
                    editor.commit();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                //If the Drop is posted, clear the Text Field
               removeDropStringFromSharedPreferences();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //If the Drop is posted, clear the Text Field
               removeDropStringFromSharedPreferences();
                hideSoftKeyboard();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideSoftKeyboard();
            }
        });

        builder.show();
    }

    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) (this).getSystemService((this).INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    public void hideSoftKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //If the Drop dialog is cleared or canceled, clear the stored string from S.P
    public void removeDropStringFromSharedPreferences () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("storedDropString", "");
        editor.commit();
    }

    // Take user input and post the Drop
    public void createDrop(String dropDescription) throws InterruptedException {
        //Create a Drop Object and get the currentUser
        final ParseObject drop = new ParseObject("Drop");
        final ParseUser currentUser = ParseUser.getCurrentUser();

        //If the Drop is posted, clear the Text Field
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("storedDropString", "");
        editor.commit();

        if (dropDescription != null) {
            //Add following fields to Drop data
            drop.put("authorPointer", currentUser);
            drop.put("description", dropDescription);
            drop.saveInBackground(new SaveCallback() {// saveInBackground first and then run relation
                @Override
                public void done(ParseException e) {
                    //Get currentUser createdRelation instance
                    ParseRelation<ParseObject> relationCreatedDrops = currentUser.getRelation("createdDrops");
                    relationCreatedDrops.add(drop);
//                    currentUser.saveInBackground();
                    //Get currentuser hasRelationTo instance
                    ParseRelation<ParseObject> relationHasRelationTo = currentUser.getRelation("hasRelationTo");
                    relationHasRelationTo.add(drop);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Notify the user that their Drop has been posted and show it in the RipleTabFragment
                                Toast.makeText(getApplicationContext(), "You have posted a new Drop!", Toast.LENGTH_SHORT).show();

//                                Intent intent = getIntent();
//                                finish();
//                                startActivity(intent);

//                                DropAdapter dropAdapter = new DropAdapter();
//                                dropAdapter.notifyDataSetChanged();

                                // Check the currentUser Report count
                                ParseQuery query = ParseQuery.getQuery("UserReportCount");
                                query.whereEqualTo("userPointer", currentUser);
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(final ParseObject parseObject, ParseException e) {
                                        int reportCount = parseObject.getInt("reportCount");

                                        if (reportCount > 0) {
                                            //Get the banned users Drops for flush
                                            ParseQuery deleteUserQuery = ParseQuery.getQuery("Drop");
                                            deleteUserQuery.whereEqualTo("authorPointer", currentUser);
                                            deleteUserQuery.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List list, ParseException e) {
                                                    //Delete the banned users Drops
                                                    try {
                                                        ParseObject.deleteAll(list);
                                                    } catch (ParseException e1) {
                                                        e1.printStackTrace();
                                                    }

                                                    //Get the banned users comments for flush
                                                    ParseQuery commentFlushQuery = ParseQuery.getQuery("Comments");
                                                    commentFlushQuery.whereEqualTo("commenterPointer", currentUser);
                                                    commentFlushQuery.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List commentFlushList, ParseException e) {
                                                            //Delete the banned users comments
                                                            try {
                                                                ParseObject.deleteAll(commentFlushList);
                                                            } catch (ParseException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                    //Set the banned users banned boolean to true
                                                    currentUser.put("isBan", true);
                                                    currentUser.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            //Notify the user that they have been banned
                                                            Toast.makeText(MainActivity.this, "As a result of reports against you, you have been permanently banned.", Toast.LENGTH_LONG).show();

                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //Log user out and return to login activity after 3 seconds
                                                                    ParseUser.logOut();
                                                                    Intent intentLogout = new Intent(getApplicationContext(), TitleActivity.class);
                                                                    startActivity(intentLogout);
                                                                    finish();
                                                                }
                                                            }, 3000);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
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

    public void isBoxChecked(boolean status) {
        this.isBoxChecked = status;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        isBoxChecked = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkTest = sharedPreferences.getBoolean("allTipsBoolean", true);
        checkBox = menu.findItem(R.id.tips);
        checkBox.setChecked(checkTest);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        //If checkbox is actuated
        if (id == R.id.tips) {

//            if (checkTest) {
//                item.setChecked(item.isChecked());
//            } else {
//               item.setChecked(!item.isChecked());
//            }

            item.setChecked(!item.isChecked());

            //If the box is checked
            SharedPreferences.Editor editor;
            if (item.isChecked()) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                editor = sharedPreferences.edit();
                editor.putBoolean("allTipsBoolean", true);
                editor.putBoolean("ripleTips", true);
                editor.putBoolean("dropTips", true);
                editor.putBoolean("trickleTips", true);
                editor.putBoolean("friendTips", true);
                editor.putBoolean("postDropTips", true);
                editor.putBoolean("viewUserTips", true);
                editor.putBoolean("viewDropTips", true);
                editor.commit();

                Toast.makeText(MainActivity.this, "All tips will be displayed.", Toast.LENGTH_LONG).show();

            //If the box is unchecked
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                editor = sharedPreferences.edit();
                editor.putBoolean("allTipsBoolean", false);
                editor.putBoolean("ripleTips", false);
                editor.putBoolean("dropTips", false);
                editor.putBoolean("trickleTips", false);
                editor.putBoolean("friendTips", false);
                editor.putBoolean("postDropTips", false);
                editor.putBoolean("viewUserTips", false);
                editor.putBoolean("viewDropTips", false);
                editor.commit();

                Toast.makeText(MainActivity.this, "Tips will no longer be displayed.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutButton) {
            ParseUser.logOut();
            Intent intentLogout = new Intent(getApplicationContext(), TitleActivity.class);
            startActivity(intentLogout);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you would like to exit Riple now?");
        builder.setNegativeButton("Stay Here", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//               moveTaskToBack(true);
                finish();
//                moveTaskToBack(true);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(1);
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {

    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserName(currentUser.getUsername());
    }
}







