package com.khfire22gmail.riple.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.Application.RipleApplication;
import com.khfire22gmail.riple.LoginActivity;
import com.khfire22gmail.riple.R;
import com.parse.ParseUser;
import com.sromku.simple.fb.SimpleFacebook;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTab extends Fragment {

    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;
    private SimpleFacebook mSimpleFacebook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.tab_riple,container,false);

        userProfilePictureView = (ProfilePictureView) view.findViewById(R.id.ripleProfilePic);
        userNameView = (TextView) view.findViewById(R.id.ripleUserName);

        //Fetch Facebook user info if it is logged
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            makeMeRequest();
//            getFacebookInfo();
        }

        return view;
    }

    /*//Graph get fb stuff
    private void getFacebookInfo() {
//         make the API call
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me?fields=id,name,picture",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
//             handle the result
                        Log.i("Kevin", "GraphResponse" + response);
                    }
                }
        ).executeAsync();
    }*/

    private void makeMeRequest() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getString("id"));
                                userProfile.put("name", jsonObject.getString("name"));

                                /*if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));*/

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
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

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                String url;
                Bundle parametersPicture = new Bundle();
                parametersPicture.putString("fields", "picture.width(150).height(150)");

                /*GraphResponse lResponsePicture = new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/",
                        parametersPicture, null).executeAndWait();
                if (lResponsePicture != null && lResponsePicture.getError() == null &&
                        lResponsePicture.getJSONObject() != null) {
                    url = lResponsePicture.getJSONObject().getJSONObject("picture")
                            .getJSONObject("data").getString("url");

                }*/

                if (userProfile.has("facebookId")) {
                    userProfilePictureView.setProfileId(userProfile.getString("facebookId"));

                    //AuthData
                    //userProfilePictureView.setProfileId("504322442");

                    //Parse "facebookId"
                    //userProfilePictureView.setProfileId("10153007567377444");

                    //Graph ID
                    //userProfilePictureView.setProfileId("10153036644382443");


                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

                if (userProfile.has("name")) {
                    userNameView.setText(userProfile.getString("name"));
                } else {
                    userNameView.setText("");
                }

                /*if (userProfile.has("gender")) {
                    userGenderView.setText(userProfile.getString("gender"));
                } else {
                    userGenderView.setText("");
                }

                if (userProfile.has("email")) {
                    userEmailView.setText(userProfile.getString("email"));
                } else {
                    userEmailView.setText("");
                }*/

            } catch (JSONException e) {
                Log.d(RipleApplication.TAG, "Error parsing saved user data.");
            }
        }
    }
    public void onLogoutClick(View v) {
        logout();
    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
