package com.khfire22gmail.riple.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.appevents.AppEventsLogger;
import com.khfire22gmail.riple.MainActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.application.RipleApplication;
import com.khfire22gmail.riple.sinch.MessageService;
import com.khfire22gmail.riple.utils.ConnectionDetector;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class TitleActivity extends AppCompatActivity {

    private Dialog progressDialog;
    private Switch fbSwitch;
    private Switch parseSwitch;
    private Intent intent;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Splash Video
        VideoView drops = (VideoView)findViewById(R.id.login_video);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.drops_outside;
        drops.setVideoURI(Uri.parse(path));
        drops.start();

        drops.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        // Bypass login screen if user is currently logged in
        intent = new Intent(getApplicationContext(), MainActivity.class);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            startActivity(intent);
            startService(serviceIntent);
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


        //Login Switches///////////////////////////////////////////////////////////////////////
        //Parse login switch
        parseSwitch = (Switch) findViewById(R.id.parse_switch);
        parseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    parseLogin();

                } else {

                }
            }
        });

        //Facebook login switch
        fbSwitch = (Switch) findViewById(R.id.fb_switch);
        fbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(TitleActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Not so fast...");
                    builder.setMessage("I will not post any offensive material and I will report any offensive material I encounter");
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

                } else {
                    fbLogout();
                }
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    //Login Functions ///////////////////////////////////////////////////////////////////////////

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
                    Toast.makeText(getApplicationContext(), "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_SHORT).show();

                } else if (user.isNew()) {
                    Log.d(RipleApplication.TAG, "User signed up and logged in through Facebook!");
                    Toast.makeText(getApplicationContext(), "User signed up and logged in through Facebook!", Toast.LENGTH_SHORT).show();
                    launchMainActivity();

                } else {
                    Log.d(RipleApplication.TAG, "You have logged in through Facebook!");
                    Toast.makeText(getApplicationContext(), "User logged in through Facebook!", Toast.LENGTH_SHORT).show();
                    launchMainActivity();
                }
            }
        });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(TitleActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void fbLogout() {
        // Log the user out
        ParseUser.logOut();
        Toast.makeText(getApplicationContext(), "You have logged out of Riple", Toast.LENGTH_SHORT).show();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////

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
