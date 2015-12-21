package com.khfire22gmail.riple.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
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
import java.io.FileOutputStream;
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
    private int dimension;
    private Bitmap resizedBitmap;
    private Bitmap resizedAndCroppedBitmap;
    private Bitmap bitmap;


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
<<<<<<< HEAD
<<<<<<< Updated upstream
=======
=======
>>>>>>> crop-branch
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            resizedBitmap = scaleCenterCrop(bitmap, 1000, 1000);

            try {
                //Write file
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

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            bitmap.recycle();
//            byte[] byteArray = stream.toByteArray();

//            Intent intentPhoto = new Intent(SettingsActivity.this, CropActivity.class);
//            intentPhoto.putExtra("selectedImage", byteArray);
//            startActivity(intentPhoto);
//            saveImageToParse(byteArray);

        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
<<<<<<< HEAD
>>>>>>> Stashed changes
=======
>>>>>>> crop-branch
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("MyApp", String.valueOf(bitmap));


                resizedAndCroppedBitmap = scaleCenterCrop(bitmap, 250, 500);
//                smallBitmap = getResizedBitmap(bitmap);
//                Bitmap thumbNail = thumbNailBitmap(smallBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedAndCroppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Log.d("MyApp", "smallBitmap compressed stream size = " + byteArray.length);

//                rotateImageIfRequired();

                ImageView imageView = (ImageView) findViewById(R.id.edit_profile_picture);
                imageView.setImageBitmap(resizedAndCroppedBitmap);

                /*//*if (byteArray.length > 10485759) {
                    Log.d("MyApp", "Picture is too large");
                    compressedBitmap = Bitmap.createScaledBitmap(thumbNail, 240, 240, true);
                    stream = new ByteArrayOutputStream();
                    compressedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    Log.d("MyApp", "byteArray = " + byteArray.length);
                    saveImageToParse(byteArray);

                } else {*//**//*
                    saveImageToParse(byteArray);
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recreate();
    }*/



    /*public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = newWidth;

        float scaleHeight = newHeight;

        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }*/

    public Bitmap getResizedBitmap(Bitmap image) {
        Bitmap originalImage = image;
        Bitmap background = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = 500/originalWidth;
        float xTranslation = 0.0f, yTranslation = (500 - originalHeight * scale)/2.0f;
        Matrix transformation = new Matrix();
//        transformation.postRotate(degree);
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);

        return background;
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

<<<<<<< HEAD
<<<<<<< Updated upstream
        if (scaledHeight > scaledWidth) {
            newWidth = 500;
            newHeight = 500;
        }

=======
>>>>>>> Stashed changes
=======
>>>>>>> crop-branch
        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
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





    public Bitmap thumbNailBitmap(Bitmap image) {
        int dimension = getSquareCropDimensionForBitmap(image);
        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

        return image;
    }

    public int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        //If the bitmap is wider than it is tall
        //use the height as the square crop dimension
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            dimension = bitmap.getHeight();

            //If the bitmap is taller than it is wide
            //use the width as the square crop dimension
        }else {
            dimension = bitmap.getWidth();
        }
        return dimension;
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
