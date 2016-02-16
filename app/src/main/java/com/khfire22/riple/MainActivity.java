package com.khfire22.riple;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.khfire22.riple.ViewPagers.MainSlidingTabLayout;
import com.khfire22.riple.ViewPagers.MainViewPagerAdapter;
import com.khfire22.riple.activities.AboutActivity;
import com.khfire22.riple.activities.SettingsActivity;
import com.khfire22.riple.activities.TitleActivity;
import com.khfire22.riple.utils.ConnectionDetector;
import com.khfire22.riple.utils.MessageService;
import com.khfire22.riple.utils.NetworkStateChangeReceiver;
import com.khfire22.riple.utils.SaveToSharedPrefs;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
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
    private ConnectionDetector detector;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1000;
    public GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    //    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    public Location mLastLocation;
    public Double mLatitudeDouble;
    public Double mLongitudeDouble;
    public String mLastLocationString;
    public String userLocationString;
    private NetworkStateChangeReceiver networkReceiver;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // First we need to check availability of play services
        if (checkGooglePlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        detector = new ConnectionDetector(this);
        currentUser = ParseUser.getCurrentUser();

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            startService(serviceIntent);
        }

        // Instantiate the broadcast receiver/Network detector so it can be terminated onPause
        networkReceiver = new NetworkStateChangeReceiver();

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab_create_drop);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
                displayName = (String) currentUser.get("displayName");

                if (parseProfilePicture == null && displayName == null) {
                    Toast.makeText(getApplicationContext(), R.string.picAndNameToast, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (parseProfilePicture == null) {
                    Toast.makeText(getApplicationContext(), R.string.picToast, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);

                } else if (displayName == null) {
                    Toast.makeText(getApplicationContext(), R.string.nameToast, Toast.LENGTH_LONG).show();
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


//    public void saveTipPreferences(String key, Boolean value){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(key, value);
//        editor.putBoolean("allTipsBoolean", false);
//        editor.commit();
//    }

    // Tip dialog box method that will show if tips enabled by the user
    public void viewUserTip() {

        boolean test = sharedPreferences.getBoolean("postDropTips", true);

        if (test) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);

            builder.setTitle("Post a Drop");
            builder.setMessage(R.string.post_drop_tip);

            builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    SaveToSharedPrefs(this, "postDropTips", false);

//                    SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs(MainActivity.this, "postDropTips", false);
//                    saveToSharedPrefs.saveBooleanPreferences();
                }
            });

            builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    public void createDropDialog() {

        // Check for connection before showing post dialog
        if (!detector.isConnectedToInternet()) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {

            // Show keyboard
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

        // Show the postDropTips tip if enabled by user
        viewUserTip();
    }

    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideSoftKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //If the Drop dialog is cleared or canceled, clear the stored string from S.P
    public void removeDropStringFromSharedPreferences() {
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

                                // Check the currentUser Report count
                                ParseQuery query = ParseQuery.getQuery("UserReportCount");
                                query.whereEqualTo("userPointer", currentUser);
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(final ParseObject parseObject, ParseException e) {
                                        int reportCount = parseObject.getInt("reportCount");

                                        if (reportCount > 49) {
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
        if (!detector.isConnectedToInternet()) {
            Toast.makeText(MainActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }

        checkGooglePlayServices();

//        // Resuming the periodic location updates
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // onPause, cease the network detector
//        try {
//            unregisterReceiver(networkReceiver);
//        } catch (Exception e) {
//            Log.d(TAG, "onPause: " + e);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs();

        // Check for valid network before these respond
        if (!detector.isConnectedToInternet()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        } else {

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

            //If checkbox is actuated activate all tips
            if (id == R.id.tips) {

                item.setChecked(!item.isChecked());

                //If the box is checked
                if (item.isChecked()) {
                    saveToSharedPrefs.saveAllTipsBoolean(this, true);
                    Toast.makeText(MainActivity.this, "All tips will be displayed.", Toast.LENGTH_LONG).show();

                    //If the box is unchecked, hide all tips
                } else {
                    saveToSharedPrefs.saveAllTipsBoolean(this, false);
                    Toast.makeText(MainActivity.this, "Tips will no longer be displayed.", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            //noinspection SimplifiableIfStatement
            if (id == R.id.logoutButton) {
//                sinchClient.stopListeningOnActiveConnection();
//                sinchClient.terminate();

                ParseUser.logOut();
                Intent intentLogout = new Intent(getApplicationContext(), TitleActivity.class);
                startActivity(intentLogout);

//                finish();

                return true;
            }
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

                finish();
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

        Crashlytics.setUserIdentifier(currentUser.getUsername());
        Crashlytics.setUserName(currentUser.getString("displayName"));
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }

    /******************************************************************************************
     * Google api callback methods
     */


    /**
     * Method to verify google play services on the device
     */

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
        /*
		* Google Play Services is missing or update is required
		*  return code could be
		* SUCCESS,
		* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
		* SERVICE_DISABLED, SERVICE_INVALID.
		*/
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mGoogleApiClient.isConnected()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeDouble = Double.valueOf(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeDouble = Double.valueOf(String.valueOf(mLastLocation.getLongitude()));
                mLastLocationString = String.valueOf(mLastLocation);

                if (mLatitudeDouble != null && mLongitudeDouble != null) {

                    // Call to geoCode method
                    getCompleteAddressString(mLatitudeDouble, mLongitudeDouble);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "location error = " + connectionResult.getErrorCode());
    }

    // Convert long and lat and convert to location with geoCoder
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            // Save all location info to this userLocationString
            userLocationString = city + ", " + state + ", " + country;
        }

        Log.d(TAG, "Location = " + userLocationString);

        // Call to save location info in shared prefs
        SaveToSharedPrefs.saveStringPreferences(this, "userLastLocation", userLocationString);

        // Call to save location info to Parse
        saveUserLocationToParse(userLocationString);

        return userLocationString;
    }

    // Save the currentUser location to Parse
    public void saveUserLocationToParse(String currentUserLocation) {

        currentUser.put("userLastLocation", currentUserLocation);
        currentUser.saveInBackground();
    }


}







