package com.khfire22gmail.riple.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.khfire22gmail.riple.MainActivity;
import com.khfire22gmail.riple.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class CropActivity extends AppCompatActivity {

    private ParseUser currentUser = ParseUser.getCurrentUser();
    ProgressDialog saveDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Bitmap originalBmp = null;
        String filename = getIntent().getStringExtra("selectedImage");
        try {
            FileInputStream is = this.openFileInput(filename);
            originalBmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        cropImageView.setImageBitmap(originalBmp);
        cropImageView.setFixedAspectRatio(true);

        final ImageView croppedImageView = (ImageView) findViewById(R.id.croppedImageView);
        final Button rotateButton = (Button) findViewById(R.id.button_rotate);
        final Button cropButton = (Button) findViewById(R.id.button_crop);
        final Button saveButton = (Button) findViewById(R.id.button_save_picture);

        //Button to rotate image
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(90);
            }
        });
        //Button to crop image
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap croppedImage = cropImageView.getCroppedImage();
                croppedImageView.setImageBitmap(croppedImage);
                Toast.makeText(CropActivity.this, "Look's great! Don't forget to save your picture once you are happy with your work!", Toast.LENGTH_LONG).show();
            }
        });
        //Button to save cropped image to parse
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                Bitmap croppedImage = cropImageView.getCroppedImage();

                croppedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                croppedImage.recycle();
                byte[] byteArray = stream.toByteArray();
                saveImageToParse(byteArray);

                saveDialog = new ProgressDialog(CropActivity.this);
                saveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                saveDialog.setMessage("Saving picture... Please wait...");
                saveDialog.setIndeterminate(true);
                saveDialog.setCanceledOnTouchOutside(false);
                saveDialog.show();
            }
        });
    }
    //Save the cropped image to parse
    private void saveImageToParse(final byte[] byteArray) {
        final ParseFile file = new ParseFile("parseProfilePicture.png", byteArray);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    currentUser.put("parseProfilePicture", file);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            saveDialog.dismiss();
                            Intent intent = new Intent(CropActivity.this, MainActivity.class);
                            startActivity(intent);

                        }
                    });
                }
            }
        });

    }
}