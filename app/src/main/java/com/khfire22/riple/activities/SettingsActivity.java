package com.khfire22.riple.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22.riple.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int REQUEST_CODE = 1;
    private ImageView editProfilePicture;
    public ParseUser currentUser;
    private ParseFile parseProfilePicture;
    private Bitmap compressedBitmap;
    private Bitmap smallBitmap;
    private Context context;
    private String facebookId;
    private ImageView editProfilePictureView;
    private Button homeButton;
    private String parseDisplayName;
    private TextView displayNameTV;
    private TextView displayNameEdit;
    private String displayNameString;
    private String userInfoEntry;
    private EditText aboutUserField;
    private int dimension;
    private Bitmap resizedBitmap;
    private Bitmap resizedAndCroppedBitmap;
    private Bitmap bitmap;
    private ProgressDialog selectDialog;
    private String userInfoString;
    private TextView userInfoTV;
    private Context mContext;
    private TextView userLocationTV;
    private String mlastLocationString;
    private SharedPreferences sharedPreferences;
    private String mUserLocaton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUser = ParseUser.getCurrentUser();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserLocaton = sharedPreferences.getString("userLocation", "");


        //Instantiate the views
        editProfilePictureView = (ImageView) findViewById(R.id.edit_profile_picture);
        displayNameEdit = (TextView) findViewById(R.id.edit_display_name_tv);
        displayNameTV = (TextView) findViewById(R.id.display_name_tv);
        userInfoTV = (TextView) findViewById(R.id.user_info_tv);
        userLocationTV = (TextView) findViewById(R.id.user_location_tv);

        //if currentUser is not null, get their name, picture and facebookId from Parse
        if ((currentUser != null) && currentUser.isAuthenticated()) {

            parseProfilePicture = currentUser.getParseFile("parseProfilePicture");
            parseDisplayName = (String) currentUser.get("displayName");
            facebookId = (String) currentUser.get("facebookId");
            userInfoString = (String) currentUser.get("userInfo");

        }

        //If currentUser has a picture, decode it at 300x300 and display it in TV
        if (parseProfilePicture != null) {
            parseProfilePicture.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 300, 300, true);
                        editProfilePictureView.setImageBitmap(resized);
                    }
                }
            });
        } else {
            //Otherwise, if use has a facebook picture, get that instead.
            if (facebookId != null) {
                Log.d("MyApp", "FB ID (Main Activity) = " + facebookId);
                new DownloadImageTask((ImageView) findViewById(R.id.edit_profile_picture))
                        .execute("https://graph.facebook.com/" + facebookId + "/picture?type=large");
            }
        }

        //Set curentUser name and info to textviews
        displayNameTV.setText(parseDisplayName);
        userInfoTV.setText(userInfoString);
        userLocationTV.setText(mUserLocaton);


        //Get the currentUser displayImage if it's available, and set it to editProfilePictureView
//        if (parseProfilePicture != null) {
//            Glide.with(this)
//                    .load(parseProfilePicture.getUrl())
//                    .crossFade()
//                    .fallback(R.drawable.ic_user_default)
//                    .error(R.drawable.ic_user_default)
//                    .signature(new StringSignature(UUID.randomUUID().toString()))
//                    .into(editProfilePictureView);
//            //If the user has a valid facebookId, use their facebook picture by default

        //OCL for new image selection
        ImageView image = (ImageView) findViewById(R.id.edit_profile_picture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });
        //OCL for display name edit
        final TextView editDisplayName = (TextView) findViewById(R.id.edit_display_name_tv);
        editDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDisplayName();
            }
        });

        //OCL for display name edit
        final TextView displayName = (TextView) findViewById(R.id.display_name_tv);
        displayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDisplayName();
            }
        });


        //OCL for user info edit
        TextView editUserInfo = (TextView) findViewById(R.id.edit_info_tv);
        editUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserInfo();
            }
        });

        //OCL for user info edit
        TextView userInfo = (TextView) findViewById(R.id.user_info_tv);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserInfo();
            }
        });
    }

    public void editDisplayName() {

        //                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String storedDisplayName = sharedPreferences.getString("storedDisplayName", "");

        final View view = getLayoutInflater().inflate(R.layout.activity_edit_display_name, null);
        //Open dialog that allows user to change their name
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Edit your display name...");

        final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.edit_display_name);

        input.setText(storedDisplayName);

        builder.setView(view);

        // Save changes
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                displayNameString = input.getText().toString();
                int displayNameTextField = input.getText().length();
                //Take user input and save it to parse as "displayName"
                if (displayNameTextField > 1) {
                    currentUser.put("displayName", displayNameString);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //Restart Settings Activity
                            Toast.makeText(getApplicationContext(), "Your display name has been changed", Toast.LENGTH_LONG).show();

                            //Save the current users display name to shared prefs
                            SharedPreferences.Editor editor;
//                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            editor = sharedPreferences.edit();
                            editor.putString("storedDisplayName", displayNameString);
                            editor.commit();

                            //Call to update the users name after edit
                            updateDisplayNameTextView(displayNameString);

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Display name must be at least 2 characters.", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Discard changes
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Your display name was not changed.", Toast.LENGTH_LONG).show();

            }
        });

        builder.show();
    }

    public void editUserInfo() {

        String storedDisplayName = sharedPreferences.getString("storedUserInfo", "");

        final View view = getLayoutInflater().inflate(R.layout.activity_edit_user_info, null);
        //Open dialog that allows user to change their info
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Edit your info...");

        final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.edit_user_info);

        input.setText(storedDisplayName);

        builder.setView(view);

        // Save changes
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                userInfoString = input.getText().toString();
                int dropTextField = input.getText().length();
                //Take user input and save it to parse as "userInfo"
                if (dropTextField > 0) {
                    currentUser.put("userInfo", userInfoString);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //Restart Settings Activity
                            Toast.makeText(getApplicationContext(), "Your info has been saved", Toast.LENGTH_LONG).show();

                            //Save the current user info to shared prefs
                            SharedPreferences.Editor editor;
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            editor = sharedPreferences.edit();
                            editor.putString("storedUserInfo", userInfoString);
                            editor.commit();

                            //Call to update user info view after edit
                            updateInfoTextView(userInfoString);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Enter some text first.", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Discard changes
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Your info was not changed.", Toast.LENGTH_LONG).show();

            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        builder.show();
    }


    //Updates the users display name after edit
    public void updateDisplayNameTextView(String updatedName) {
        TextView displayNameView = (TextView) findViewById(R.id.display_name_tv);
        displayNameView.setText(updatedName);
    }

    //Updates the users info after edit
    private void updateInfoTextView(String updatedInfo) {
        TextView userInfoView = (TextView) findViewById(R.id.user_info_tv);
        userInfoView.setText(updatedInfo);
    }

    //Async to download the currentUser FB picture
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        //Get bitmap of users FB picture
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("MyApp", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        //Once picture is downloaded, assign it to bmImage and convert it into a byteArray
        //then save it to Parse
        protected void onPostExecute(Bitmap result) {
            if (bmImage != null) {
                bmImage.setImageBitmap(result);
                //convert bitmap to byte array and upload to Parse
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                final ParseFile file = new ParseFile("parseProfilePicture.png", byteArray);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            currentUser.put("parseProfilePicture", file);
                            currentUser.saveInBackground();
                        }
                    }
                });
            }
        }

    }

    //Open image chooser intent for custom image upload
    private void selectImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);

    }

    //After image has been selected...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //Resize the image with the scaleBitmap method and assign it to resizedBitmap
            resizedBitmap = scaleBitmap(bitmap);

            try {

                //Write file to internal storage for extra intent transfer
                String filename = "bitmap.png";
                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                resizedBitmap.recycle();

                //Pop intent
                Intent cropIntent = new Intent(this, CropActivity.class);
                cropIntent.putExtra("selectedImage", filename);
                startActivity(cropIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Scale the image so it keeps its dimensions, but resolution is usable by cropper
    private Bitmap scaleBitmap(Bitmap bm) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        int maxWidth = 900;
        int maxHeight = 900;
        //If width is greater than height, give landscape ratio
        if (width > height) {
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
            //Otherwise give portrait ratio
        } else if (height > width) {
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
            //If sides are equivalent, give square ratio
        } else {
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);

        return bm;
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
