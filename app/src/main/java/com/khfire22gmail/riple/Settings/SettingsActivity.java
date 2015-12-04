package com.khfire22gmail.riple.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.khfire22gmail.riple.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = null;
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
    private TextView displayNameView;
    private TextView displayNameEdit;
    private String displayNameString;

    private TextView userInfoView;
    private String userInfoEntry;

    private EditText aboutUserField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUser = ParseUser.getCurrentUser();


        editProfilePictureView = (ImageView) findViewById(R.id.edit_profile_picture);
        displayNameEdit = (TextView) findViewById(R.id.edit_display_name_tv);
        displayNameView = (TextView) findViewById(R.id.display_name_tv);




        if ((currentUser != null) && currentUser.isAuthenticated()) {

            parseProfilePicture = currentUser.getParseFile("parseProfilePicture");
            parseDisplayName = (String) currentUser.get("displayName");
            facebookId = (String) currentUser.get("facebookId");

        }
        // Facebook picture code
        if(parseProfilePicture != null) {
            Glide.with(this)
                    .load(parseProfilePicture.getUrl())
                    .crossFade()
                    .fallback(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(editProfilePictureView);
        } else {
            if (facebookId != null){
                Log.d("MyApp", "FB ID (Main Activity) = " + facebookId);
                new DownloadImageTask((ImageView) findViewById(R.id.edit_profile_picture))
                        .execute("https://graph.facebook.com/" + facebookId+ "/picture?type=large");
            }
        }
        ImageView image = (ImageView) findViewById(R.id.edit_profile_picture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });
        TextView displayNameTV = (TextView) findViewById(R.id.edit_display_name_tv);
        displayNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = getLayoutInflater().inflate(R.layout.activity_edit_display_name, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Edit your user name...");

                final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.edit_display_name);

                builder.setView(view);

                // Set up the buttons
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        displayNameString = input.getText().toString();
                        int dropTextField = input.getText().length();

                        if (dropTextField > 2) {
                            currentUser.put("displayName", displayNameString);
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(getApplicationContext(), "Your user name has been changed", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "User names must be at least 3 characters.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Your user name was not changed.", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.show();
            }
        });

        homeButton = (Button) findViewById(R.id.button_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        displayNameView.setText(parseDisplayName);


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }


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



    // User uploaded picture code
    private void selectImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("MyApp", String.valueOf(bitmap));

                smallBitmap = getResizedBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Log.d("MyApp", "smallBitmap compressed stream size = " + byteArray.length);

//                rotateImageIfRequired();

                ImageView imageView = (ImageView) findViewById(R.id.edit_profile_picture);
                imageView.setImageBitmap(bitmap);

                if (byteArray.length > 10485759) {
                    Log.d("MyApp", "Picture is too large");
                    compressedBitmap = Bitmap.createScaledBitmap(smallBitmap, 240, 240, true);
                    stream = new ByteArrayOutputStream();
                    compressedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    Log.d("MyApp", "byteArray = " + byteArray.length);
                    saveImageToParse(byteArray);

                } else {
                    saveImageToParse(byteArray);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recreate();
    }

    //Rotate Image////////////////////////////////////////////////
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
/////////////////////////////////////////////////////////////////////////
    public Bitmap getResizedBitmap(Bitmap image) {
        Bitmap originalImage = image;
        Bitmap background = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888);
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = 240/originalWidth;
        float xTranslation = 0.0f, yTranslation = (240 - originalHeight * scale)/2.0f;
        Matrix transformation = new Matrix();
//        transformation.postRotate(degree);
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);
        return background;
    }

    private void saveImageToParse(byte[] byteArray) {
        final ParseFile file = new ParseFile("parseProfilePicture.png", byteArray);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    currentUser.put("parseProfilePicture", file);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            recreate();
                        }
                    });
                }
            }
        });

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
