package com.khfire22gmail.riple;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import com.parse.ParseFile;
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
    ParseUser currentUser;
    private View root_layout;

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
        mPager.setCurrentItem(0);

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

//        popupWindow.setBackgroundDrawable(drawable.popup_background);

//        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
//        windowManager.dimAmount = 0.75f;
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

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

    private Bitmap takeScreenShot(Activity activity) {
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        int height = this.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    // Take user input and post the Drop
    public void createDrop(String dropDescription) throws InterruptedException {

        final ParseObject drop = new ParseObject("Drop");
        final ParseUser user = ParseUser.getCurrentUser();
        final ParseFile userPicture = (ParseFile) user.get("parseProfilePicture");

        if(dropDescription != null) {
            drop.put("author", user.getObjectId());
            drop.put("name", user.get("username"));
            drop.put("description", dropDescription);
            drop.put("authorPicture", userPicture);
            drop.saveInBackground(new SaveCallback() {// saveInBackground first and then run relation
                @Override
                public void done(ParseException e) {
                    ParseRelation<ParseObject> relationCreatedDrops = user.getRelation("createdDrops");
                    relationCreatedDrops.add(drop);
                    user.saveInBackground();

                    ParseRelation<ParseObject> relationHasRelationTo = user.getRelation("hasRelationTo");
                    relationHasRelationTo.add(drop);
//                    user.saveInBackground();
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                Toast.makeText(getApplicationContext(), "You have posted a new Drop!", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }
                    });
                }
            });
        }
//        RipleTabFragment fragment = (RipleTabFragment) getSupportFragmentManager().findFragmentById(R.id.riple_recycler_view);
//        fragment.RefreshRipleTab();


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
            Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }

        if (id == R.id.aboutButton) {
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

