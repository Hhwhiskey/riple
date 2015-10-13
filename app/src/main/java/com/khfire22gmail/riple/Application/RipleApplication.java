package com.khfire22gmail.riple.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.khfire22gmail.riple.R;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;


/**
 * Created by Kevin on 9/7/2015.
 */
public class RipleApplication extends Application {

    public static final String TAG = "Kevin";

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initializes parse in application
        Parse.initialize(this, getString(R.string.parse_id), getString(R.string.parse_client_key));

        // Initializes FB Parse
        ParseFacebookUtils.initialize(this);

        //Saves current parse instance in the background
        ParseInstallation.getCurrentInstallation().saveInBackground();


        //Simple Facebook requires these in the application file
        Permission[] permissions = new Permission[]{
                Permission.USER_PHOTOS,
                Permission.EMAIL,
                Permission.PUBLISH_ACTION,
                Permission.PUBLIC_PROFILE,
                Permission.READ_FRIENDLISTS
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.fb_app_id))
                .setNamespace(getString(R.string.fb_namespace))
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(configuration);


        /*// Instantiate a SinchClient using the SinchClientBuilder.
        android.content.Context context = this.getApplicationContext();
        SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey(getString(R.string.SINCH_APP_KEY))
                .applicationSecret(getString(R.string.SINCH_APP_SECRET))
                .environmentHost(getString(R.string.SINCH_ENVIRONMENT_HOST))
                .userId("<user id>")
                .build();*/
    }

    public static void updateParseInstallation() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("userId", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
        ParseUser.enableRevocableSessionInBackground();
    }
}