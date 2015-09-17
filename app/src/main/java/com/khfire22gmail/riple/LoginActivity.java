package com.khfire22gmail.riple;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


import com.facebook.appevents.AppEventsLogger;
import com.khfire22gmail.riple.Utils.ConnectionDetector;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private SimpleFacebook mSimpleFacebook;
    private Switch fbSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Calls the keyhash method
        printKeyHash(this);

        //Calls the connection detector
        ConnectionDetector detector = new ConnectionDetector(this);
        if (!detector.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }

        //FB login switch
        fbSwitch = (Switch) findViewById(R.id.fbSwitch);
        fbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginFacebook();
                } else {
                    logoutFacebook();
                }
            }
        });
    }

    //FB login code
    private void loginFacebook() {

        OnLoginListener onLoginListener = new OnLoginListener() {

            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                // change the state of the button or do whatever you want
                Log.i("Kevin", "Logged in");
                Toast.makeText(getApplicationContext(), "You have logged into Facebook!", Toast.LENGTH_SHORT).show();

                //getFriends();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "You have failed to login to Facebook!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String reason) {
                Toast.makeText(getApplicationContext(), "You have failed to login to Facebook!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {
                // exception from facebook
                throwable.printStackTrace();
            }
        };

        mSimpleFacebook.login(onLoginListener);
    }

    private void getFriends() {
        mSimpleFacebook.getFriends(onFriendsListener);
    }

    //FB logout code
    private void logoutFacebook() {
        //logout listener
        OnLogoutListener onLogoutListener = new OnLogoutListener() {

            @Override
            public void onLogout() {
                Toast.makeText(getApplicationContext(), "You have logged out of Facebook", Toast.LENGTH_SHORT).show();
            }
        };

        mSimpleFacebook.logout(onLogoutListener);
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
        //Every activity that wants to use simplefacebook but have this in the onResume
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    //Every activity that wants to use simplefacebook must have this in the onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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


    //This is used to generate a keyhash for facebook
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retrieving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    OnFriendsListener onFriendsListener = new OnFriendsListener() {
        @Override
        public void onComplete(List<Profile> friends) {
            Log.i("Kevin", "Number of friends = " + friends.size());
        }
        @Override
        public void onFail(String reason) {
            Log.i("Kevin", "Fail reason = " + reason);
        }


    /*
     * You can override other methods here:
     * onThinking(), onFail(String reason), onException(Throwable throwable)
     */
    };
}
    /* d();

    //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

