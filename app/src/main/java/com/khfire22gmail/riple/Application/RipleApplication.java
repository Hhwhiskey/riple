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
                Permission.PUBLISH_ACTION
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.fb_app_id))
                .setNamespace(getString(R.string.fb_namespace))
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(configuration);
    }
}