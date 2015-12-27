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

    // Private Constants ///////////////////////////////////////////////////////////////////////////

    private static final int GUIDELINES_ON_TOUCH = 1;
    private CropImageView selectedPicture;
    private ParseUser currentUser = ParseUser.getCurrentUser();
    ProgressDialog saveDialog;

    // Activity Methods ////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);

//        Bundle extras = getIntent().getExtras();
//        byte[] byteArray = extras.getByteArray("selectedImage");


//        byte[] byteArray = getIntent().getByteArrayExtra("selectedImage");

        Bitmap originalBmp = null;
        String filename = getIntent().getStringExtra("selectedImage");
        try {
            FileInputStream is = this.openFileInput(filename);
            originalBmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Initialize Views.
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        cropImageView.setImageBitmap(originalBmp);

//        final ToggleButton fixedAspectRatioToggleButton = (ToggleButton) findViewById(R.id.fixedAspectRatioToggle);
//        final TextView aspectRatioXTextView = (TextView) findViewById(R.id.aspectRatioX);
//        final SeekBar aspectRatioXSeekBar = (SeekBar) findViewById(R.id.aspectRatioXSeek);
//        final TextView aspectRatioYTextView = (TextView) findViewById(R.id.aspectRatioY);
//        final SeekBar aspectRatioYSeekBar = (SeekBar) findViewById(R.id.aspectRatioYSeek);
//        final Spinner guidelinesSpinner = (Spinner) findViewById(R.id.showGuidelinesSpin);
        final ImageView croppedImageView = (ImageView) findViewById(R.id.croppedImageView);
        final Button rotateButton = (Button) findViewById(R.id.button_rotate);
        final Button cropButton = (Button) findViewById(R.id.button_crop);
        final Button saveButton = (Button) findViewById(R.id.button_save_picture);


        // Initializes fixedAspectRatio toggle button.
//        fixedAspectRatioToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cropImageView.setFixedAspectRatio(true);
//                cropImageView.setAspectRatio(aspectRatioXSeekBar.getProgress(), aspectRatioYSeekBar.getProgress());
//                aspectRatioXSeekBar.setEnabled(isChecked);
//                aspectRatioYSeekBar.setEnabled(isChecked);
//            }
//        });
        // Set seek bars to be disabled until toggle button is checked.
//        aspectRatioXSeekBar.setEnabled(false);
//        aspectRatioYSeekBar.setEnabled(false);
//
//        aspectRatioXTextView.setText(String.valueOf(aspectRatioXSeekBar.getProgress()));
//        aspectRatioYTextView.setText(String.valueOf(aspectRatioXSeekBar.getProgress()));
//
//        // Initialize aspect ratio X SeekBar.
//        aspectRatioXSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar aspectRatioXSeekBar, int progress, boolean fromUser) {
//                if (progress < 1) {
//                    aspectRatioXSeekBar.setProgress(1);
//                }
//                cropImageView.setAspectRatio(aspectRatioXSeekBar.getProgress(), aspectRatioYSeekBar.getProgress());
//                aspectRatioXTextView.setText(String.valueOf(aspectRatioXSeekBar.getProgress()));
//            }

//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Do nothing.
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // Do nothing.
//            }
//        });

//        // Initialize aspect ratio Y SeekBar.
//        aspectRatioYSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar aspectRatioYSeekBar, int progress, boolean fromUser) {
//                if (progress < 1) {
//                    aspectRatioYSeekBar.setProgress(1);
//                }
//                cropImageView.setAspectRatio(aspectRatioXSeekBar.getProgress(), aspectRatioYSeekBar.getProgress());
//                aspectRatioYTextView.setText(String.valueOf(aspectRatioYSeekBar.getProgress()));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Do nothing.
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // Do nothing.
//            }
//        });

//        // Set up the Guidelines Spinner.
//        guidelinesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                cropImageView.setGuidelines(i);
//            }
//
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                // Do nothing.
//            }
//        });
//        guidelinesSpinner.setSelection(GUIDELINES_ON_TOUCH);

        // Initialize the Crop button.
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            cropImageView.rotateImage(90);
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap croppedImage = cropImageView.getCroppedImage();
                croppedImageView.setImageBitmap(croppedImage);
                Toast.makeText(CropActivity.this, "Look's great! Don't forget to save your picture once you are happy with your work!", Toast.LENGTH_LONG).show();
            }
        });

//        final Bitmap croppedBmp = cropImageView.getCroppedImage();
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
                Intent intent = new Intent(CropActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

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

                        }
                    });
                }
            }
        });

    }


}