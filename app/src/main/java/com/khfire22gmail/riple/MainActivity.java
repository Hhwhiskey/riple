package com.khfire22gmail.riple;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.khfire22gmail.riple.application.RipleApplication;
import com.khfire22gmail.riple.settings.AboutActivity;
import com.khfire22gmail.riple.settings.SettingsActivity;
import com.khfire22gmail.riple.sinch.MessageService;
import com.khfire22gmail.riple.slider.SlidingTabLayout;
import com.khfire22gmail.riple.slider.ViewPagerAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.SinchClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ViewPager mPager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Riple", "Drops", "Trickle", "Friends"};
    int numOfTabs = 4;
    private String dropDescription;
    private AutoCompleteTextView dropDescriptionView;
    private SinchClient sinchClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);

//        ScrollingFABBehavior(this, obtainStyledAttributes(R.attr.toolBarHeight));

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab_create_drop);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showPopup(view);
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) myFab.getLayoutParams();
            p.setMargins(0, 0, 0, 0); // get rid of margins since shadow area is now the margin
            myFab.setLayoutParams(p);
        }

        // Store the current users facebookId
        storeUserOnParse();

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
        mPager.setCurrentItem(2);

        // Allow fragments to stay in memory
        mPager.setOffscreenPageLimit(0);

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

    // Create Drop Popup
    public void showPopup(View anchorView) {

        final View popupView = getLayoutInflater().inflate(R.layout.activity_create_drop, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.setAnimationStyle(R.style.Animation);

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        Button postDropButton = (Button) popupView.findViewById(R.id.button_post_drop);
        dropDescriptionView = (AutoCompleteTextView) popupView.findViewById(R.id.drop_description);

        popupWindow.showAtLocation(findViewById(R.id.root_layout), 30, 30, 30);

        // Allow user to input Drop
        postDropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dropDescription = dropDescriptionView.getEditableText().toString();
                try {
                    createDrop(dropDescription);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                popupWindow.dismiss();
            }
        });
    }

    // Take user input and post the Drop
    public void createDrop(String dropDescription) throws InterruptedException {

        final ParseObject drop = new ParseObject("Drop");
        final ParseUser user = ParseUser.getCurrentUser();

        if(dropDescription != null) {
            drop.put("author", user.getObjectId());
            drop.put("facebookId", user.get("facebookId"));
            drop.put("name", user.get("username"));
            drop.put("description", dropDescription);
            drop.saveInBackground(new SaveCallback() {// saveInBackground first and then run relation
                @Override
                public void done(ParseException e) {
                    ParseRelation<ParseObject> relationCreatedDrops = user.getRelation("createdDrops");
                    relationCreatedDrops.add(drop);
                    user.saveInBackground();

                    ParseRelation<ParseObject> relationHasRelationTo = user.getRelation("hasRelationTo");
                    relationHasRelationTo.add(drop);
                    user.saveInBackground();
                }
            });
        }
//        RipleTabFragment fragment = (RipleTabFragment) getSupportFragmentManager().findFragmentById(R.id.riple_recycler_view);
//        fragment.RefreshRipleTab();
        Toast.makeText(getApplicationContext(), "You have posted a new Drop!", Toast.LENGTH_SHORT).show();

        Thread.sleep(500);

        Intent intent = getIntent();
        finish();
        startActivity(intent);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.settingsButton) {
            ParseUser.logOut();
            Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }

        if (id == R.id.aboutButton) {
            ParseUser.logOut();
            Intent intentSettings = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intentSettings);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutButton) {
            ParseUser.logOut();
            Intent intentLogout = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLogout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void storeUserOnParse() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            try {
                                // Save the user profile info in a user property
                                final ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("facebookId", jsonObject.getString("id"));
                                currentUser.put("username", jsonObject.getString("name"));
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                    }
                                });

                            } catch (JSONException e) {
                                Log.d(RipleApplication.TAG,
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(RipleApplication.TAG,
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d(RipleApplication.TAG,
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d(RipleApplication.TAG,
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });

        request.executeAsync();
    }

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
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }*/

