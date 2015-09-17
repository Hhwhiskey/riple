package com.khfire22gmail.riple.Application;

import android.app.Application;

import com.khfire22gmail.riple.R;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;


/**
 * Created by Kevin on 9/7/2015.
 */
public class RipleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

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
}