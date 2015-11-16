package com.khfire22gmail.riple.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Settings extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static final String TAG = null;
    private ProfilePictureView editProfilePicture;
    String aboutUserText;
    EditText aboutUserField;
    public ParseUser currentUser = ParseUser.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        editProfilePicture = (ProfilePictureView) findViewById(R.id.edit_profile_picture);
        editProfilePicture.setProfileId("");
        aboutUserField = (EditText)findViewById(R.id.about_user_field);


        editProfilePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_CODE);
            }
        });

        Button saveAllSettingsButton = (Button) findViewById(R.id.save_all_settings);
        saveAllSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 11/11/2015 Allow the "SAVE" button press to save all current settings to parse.
                aboutUserText = aboutUserField.getText().toString();
                aboutUser(aboutUserText);

            }
        });
    }

    /*public String aboutUserInput() {

        return aboutUser;
    }*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        //data gives you the image uri. Try to convert that to bitmap
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.i("Kevin", "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.i("Kevin", "Exception in onActivityResult : " + e.getMessage());
        }
    }

    public void uploadProfilePicture(){

        ParseObject user = new ParseObject("_User");

    }

    public void aboutUser(String aboutUserText){

        currentUser.put("aboutUser", aboutUserText);
        currentUser.saveInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_details, menu);
        return true;
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
}
