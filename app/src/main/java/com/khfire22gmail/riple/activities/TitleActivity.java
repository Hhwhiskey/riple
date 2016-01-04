package com.khfire22gmail.riple.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.khfire22gmail.riple.MainActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.application.RipleApplication;
import com.khfire22gmail.riple.utils.ConnectionDetector;
import com.khfire22gmail.riple.utils.MessageService;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class TitleActivity extends AppCompatActivity {

    private Dialog progressDialog;
    private Button fbButton;
    private Button emailButton;
    private Intent intent;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        //Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Splash Video


//        VideoView drops = (VideoView)findViewById(R.id.title_video);
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.duckfinal;
//        drops.setVideoURI(Uri.parse(path));
//        drops.start();
//
//        drops.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });

        // Bypass login screen if user is currently logged in
        intent = new Intent(getApplicationContext(), MainActivity.class);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        ParseUser currentUser = ParseUser.getCurrentUser();


        if (currentUser != null) {
            boolean banBoolean = currentUser.getBoolean("isBan");
            if (!banBoolean) {
                startActivity(intent);
                startService(serviceIntent);
            } else {
                showBanDialog();
            }
        }

        // Check if there is a currently logged in use and it's linked to a Facebook account.
        /*ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            launchMainActivity();
            // If user is not null and ParseFB is linked, then go to app with login completed
        }*/

        //Calls the keyhash method
        //printKeyHash(this);

        //Calls the connection detector
        ConnectionDetector detector = new ConnectionDetector(this);
        if (!detector.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }


        //Login Buttons///////////////////////////////////////////////////////////////////////
        //Parse login Button
        emailButton = (Button) findViewById(R.id.button_email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseLogin();
            }
        });


        //Facebook login Button
        fbButton = (Button) findViewById(R.id.button_facebook);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TitleActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Not so fast...");
                builder.setMessage("I will not post any spam or inappropriate/offensive material and I will report any that I encounter while I use Riple");
                builder.setNegativeButton("Cya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });

                builder.setPositiveButton("I promise", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fbLogin();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

//        VideoView drops = (VideoView)findViewById(R.id.title_video);
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.duckfinal;
//        drops.setVideoURI(Uri.parse(path));
//        drops.start();
//
//        drops.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void parseLogin() {
        Intent intent = new Intent(TitleActivity.this, ParseLoginActivity.class);
        startActivity(intent);
    }

    public void fbLogin() {
        List<String> permissions = Arrays.asList("public_profile", "email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {

                if (user == null) {
                    Log.d(RipleApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
                    Toast.makeText(getApplicationContext(), "Uh oh. Facebook login has been canceled.", Toast.LENGTH_LONG).show();

                } else if (user.isNew()) {
                    Log.d(RipleApplication.TAG, "User signed up and logged in through Facebook!");
                    Toast.makeText(getApplicationContext(), "You have signed up and logged in through Facebook!", Toast.LENGTH_LONG).show();
                    storeFacebookUserOnParse();

                } else {
                    Log.d(RipleApplication.TAG, "You have logged in through Facebook!");
//                    Toast.makeText(getApplicationContext(), "User logged in through Facebook!", Toast.LENGTH_SHORT).show();

                    boolean banBoolean = user.getBoolean("isBan");
                    if (!banBoolean) {
                        launchMainActivity();
                    } else {
//                        Toast.makeText(TitleActivity.this, "You have been banned from Riple. If you decide to use Riple again, please follow the rules. Thank you.", Toast.LENGTH_LONG).show();
                        showBanDialog();

                    }
                }
            }
        });
    }

    private void showBanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TitleActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("You have been banned");
        builder.setMessage("You have been banned from Riple, due to reports made against you. If you feel this is mistake, please email Riple for support and it will be investigated. If you do decide to use Riple again, please follow the rules so that everyone can enjoy what Riple has to offer. Thank you.");
        builder.setNegativeButton("EMAIL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"kevinhodgesriple@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ban investigation request");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, ""));
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void storeFacebookUserOnParse() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            try {

                                // Save the facebook user if they are new
                                final ParseUser currentUser = ParseUser.getCurrentUser();

                                currentUser.put("facebookId", jsonObject.getString("id"));
                                currentUser.put("username", jsonObject.getString("name"));
                                currentUser.put("displayName", jsonObject.getString("name"));
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        //Create riple count tracker on UserRipleCount table to avoid ACL restrictions
                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        ParseObject userRipleCount = new ParseObject("UserRipleCount");
                                        userRipleCount.put("userPointer", currentUser);
                                        userRipleCount.put("ripleCount", 0);
                                        userRipleCount.saveInBackground();

                                        //Create report count tracker on UserReportCount table to avoid ACL restrictions
                                        ParseObject userReportCount = new ParseObject("UserReportCount");
                                        userReportCount.put("userPointer", currentUser);
                                        userReportCount.put("reportCount", 0);
                                        userReportCount.saveInBackground();

                                        //Also create riple count tracker on the currentUser table for ease of use
                                        currentUser.put("userRipleCount", 0);
                                        currentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                fbLogin();
                                            }
                                        });
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

    private void launchMainActivity() {
        Intent intent = new Intent(TitleActivity.this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);

    }
}

