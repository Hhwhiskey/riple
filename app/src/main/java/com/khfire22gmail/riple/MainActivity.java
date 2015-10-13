package com.khfire22gmail.riple;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.khfire22gmail.riple.application.RipleApplication;
import com.khfire22gmail.riple.slider.SlidingTabLayout;
import com.khfire22gmail.riple.slider.ViewPagerAdapter;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Riple", "Drops", "Trickle", "Chat"};
    int Numboftabs = 4;
    ViewPager mViewPager;

    private String dropTitle;
    private String dropDescription;
    private AutoCompleteTextView dropTitleView;
    private AutoCompleteTextView dropDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeFacebookId();

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // mViewPager.setCurrentItem(R.layout.tab_drop);
        // mViewPager.setCurrentItem(position);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

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
        tabs.setViewPager(pager);


        Button button = (Button) findViewById(R.id.button_test);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, CreateDrop.class);
//                startActivity(intent);
                showPopup(view);
            }
        });
    }

    public void showPopup(View anchorView) {

        final View popupView = getLayoutInflater().inflate(R.layout.activity_create_drop, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Example: If you have a TextView inside `popup_layout.xml`
        // TextView textView = (TextView) popupView.findViewById(R.id.);

        // Initialize more widgets from `popup_layout.xml`

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        Button postDropButton = (Button) popupView.findViewById(R.id.button_post_drop);
        dropDescriptionView = (AutoCompleteTextView) popupView.findViewById(R.id.drop_description);
        dropTitleView = (AutoCompleteTextView) popupView.findViewById(R.id.drop_title);

        popupWindow.showAtLocation(findViewById(R.id.root_layout), 30, 30, 30);

        /*// Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, location[0], location[1] + anchorView.getHeight());*/

        postDropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HOHO", "You clicked me man...what's up");
                dropTitle = dropTitleView.getEditableText().toString();
                dropDescription = dropDescriptionView.getEditableText().toString();
                createDrop(dropTitle, dropDescription);
                popupWindow.dismiss();

                Log.d("Kevin", "Title = " + dropTitle);
            }
        });
    }

    // Create a new Drop
    public void createDrop(String dropTitle, String dropDescription) {

        Log.d("Kevin", "Title = " + dropTitle);
        Log.d("Kevin", "Description = " + dropDescription);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject drop = new ParseObject("Drop");

        drop.put("author", currentUser.getObjectId());
        drop.put("facebookId", currentUser.get("facebookId"));
        drop.put("name", currentUser.get("name"));
        drop.put("title", dropTitle);
        drop.put("description", dropDescription);

        drop.saveInBackground();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutButton) {
            ParseUser.logOut();
            // Go to the login view
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void storeFacebookId() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        String fbName = (String) currentUser.get("author");
        String fbId = (String) currentUser.get("userProfilePictureView");


        if (fbId == null || fbName == null) {

            if (currentUser.has("profile")) {

                JSONObject profile = currentUser.getJSONObject("profile");

                try {

                    if (profile.has("author")) {
                        String name = profile.getString("author");
                        currentUser.put("author", name);

                    } else {
                        currentUser.put("author", "Anonymous User");
                    }

                    if (profile.has("userProfilePictureView")) {
                        String facebookId = profile.getString("userProfilePictureView");
                        currentUser.put("userProfilePictureView", facebookId);

//                    } else {
//                         TODO Add default avatar
//                          Show the default, blank user profile picture
//                        currentUser.put(R.drawable.ic_user_default);
                    }


                } catch (JSONException jse) {
                    Log.d(RipleApplication.TAG, "Error parsing saved user data.");
                }
            }
        }
    }
}

    /*private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }*/

