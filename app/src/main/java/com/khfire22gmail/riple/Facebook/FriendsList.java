package com.khfire22gmail.riple.facebook;

import android.content.Intent;
import android.os.Bundle;

import com.khfire22gmail.riple.MainActivity;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by Kevin on 9/13/2015.
 */
public class FriendsList extends MainActivity {

    private SimpleFacebook mSimpleFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Retrieve friend list from Facebook


        //mSimpleFacebook.getFriends(onFriendsListener);

    // * You can override other methods here:
     //* onThinking(), onFail(String reason), onException(Throwable throwable)

}
