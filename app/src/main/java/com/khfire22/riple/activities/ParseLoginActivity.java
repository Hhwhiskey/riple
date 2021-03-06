package com.khfire22.riple.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22.riple.MainActivity;
import com.khfire22.riple.R;
import com.khfire22.riple.utils.ConnectionDetector;
import com.khfire22.riple.utils.MessageService;
import com.khfire22.riple.utils.SaveToSharedPrefs;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

public class ParseLoginActivity extends AppCompatActivity {

    private Button signUpButton;
    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;
    private String email;
    private String password;
    private Intent intent;
    private Intent serviceIntent;
    private Context context;
    private Boolean emailVerified;
    private TextView forgotPassword;
    private ConnectionDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detector = new ConnectionDetector(this);

        intent = new Intent(getApplicationContext(), MainActivity.class);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);


//        //If there is no connection present show toast
//        if (!detector.isConnectedToInternet()) {
//            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
//            //Otherwise, enter the MainActivity
//        } else {
//
//            if (user != null) {
//                emailVerified = user.getBoolean("emailVerified");
//                if (emailVerified) {
//                    boolean banBoolean = user.getBoolean("isBan");
//                    if (!banBoolean) {
//                        startActivity(intent);
//                        startService(serviceIntent);
//                    }
//                }
//            }
//        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storedEmail = sharedPreferences.getString("storedEmail", "");

        setContentView(R.layout.activity_parse_login);

        loginButton = (Button) findViewById(R.id.button_login);
        signUpButton = (Button) findViewById(R.id.button_signup);
        emailField = (EditText) findViewById(R.id.email_login);
        emailField.requestFocus();
        emailField.setText(storedEmail);
        passwordField = (EditText) findViewById(R.id.password_login);
        forgotPassword = (TextView) findViewById(R.id.forgot_password_tv);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(ParseLoginActivity.this, R.string.no_connection, Toast.LENGTH_LONG).show();
                } else {
                    email = emailField.getText().toString();
                    email = email.toLowerCase();
                    password = passwordField.getText().toString();

                    ParseUser.logInInBackground(email, password, new LogInCallback() {
                        public void done(ParseUser user, com.parse.ParseException e) {

                            if (user != null) {

                                final boolean banBoolean = user.getBoolean("isBan");
                                emailVerified = user.getBoolean("emailVerified");

                                if (emailVerified) {
                                    if (!banBoolean) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ParseLoginActivity.this, R.style.MyAlertDialogStyle);
                                        builder.setTitle("Not so fast...");
                                        builder.setMessage("I will not post any spam or inappropriate/offensive material and I will report any that I encounter while I use Riple");
                                        builder.setNegativeButton("Cya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                moveTaskToBack(true);
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                                System.exit(1);

                                            }
                                        });


                                        builder.setPositiveButton("I promise", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                startActivity(intent);
//                                        startService(serviceIntent);
                                                finish();
                                            }
                                        });

                                        builder.show();


                                        // Store the emai in the email field's shared prefs
                                        SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs();
                                        saveToSharedPrefs.saveStringPreferences(ParseLoginActivity.this, "storedEmail", email);

                                    } else {
                                        showBanDialog();
                                    }
                                } else {
                                    Toast.makeText(ParseLoginActivity.this, "Please verify your email" +
                                                    " address before you begin using Riple!",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(ParseLoginActivity.this, "Wrong username/password combo", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(ParseLoginActivity.this, R.string.no_connection, Toast.LENGTH_LONG).show();
                } else {

                    email = emailField.getText().toString();
                    email = email.toLowerCase();
                    password = passwordField.getText().toString();

                    int passwordLength = passwordField.getText().length();

                    if (passwordLength > 5) {

                        final ParseUser user = new ParseUser();
                        user.setUsername(email);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.put("username", email);
                        user.put("email", email);
                        user.put("userInfo", "");
                        user.signUpInBackground(new SignUpCallback() {
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    if (user != null) {
                                        emailVerified = user.getBoolean("emailVerified");
                                        if (emailVerified) {
                                            startActivity(intent);
//                                                    startService(serviceIntent);
                                        } else {
                                            Toast.makeText(ParseLoginActivity.this, "An email has been sent. Please verify your email" +
                                                            " address before you begin using Riple!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "There was an error signing up. You must enter a " +
                                                    "properly formatted and unique email address"
                                            , Toast.LENGTH_LONG).show();
                                }

                                //Create riple count tracker on UserRipleCount table to avoid ACL restrictions
                                ParseObject userRipleCount = new ParseObject("UserRipleCount");
                                userRipleCount.put("userPointer", user);
                                userRipleCount.put("ripleCount", 0);
                                userRipleCount.saveInBackground();

                                //Create report count tracker on UserReportCount table to avoid ACL restrictions
                                ParseObject userReportCount = new ParseObject("UserReportCount");
                                userReportCount.put("userPointer", user);
                                userReportCount.put("reportCount", 0);
                                userReportCount.saveInBackground();

//                                  //Also create riple count tracker on the currentUser table for ease of use
                                user.put("userRipleCount", 0);
                                user.saveInBackground();

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Your must enter a valid email and your password " +
                                        "must be at least 6 characters",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(ParseLoginActivity.this, R.string.no_connection, Toast.LENGTH_LONG).show();
                } else {

                    email = emailField.getText().toString();
                    email = email.toLowerCase();
                    if (!email.isEmpty()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ParseLoginActivity.this, R.style.MyAlertDialogStyle);

                        builder.setTitle("Password Reset");
                        builder.setMessage("Are you sure you want to reset your password?");

                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                email = emailField.getText().toString();
                                if (email != null) {
                                    resetParsePassword(email);
                                }
                            }
                        });
                        builder.show();

                    } else {
                        Toast.makeText(ParseLoginActivity.this, "Please enter your email before resetting your password", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
    }

    public void resetParsePassword(String email) {
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ParseLoginActivity.this, "An email was successfully " +
                            "sent with password reset instructions.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ParseLoginActivity.this, "Please enter a " +
                            "valid email first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showBanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ParseLoginActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("You have been banned");
        builder.setMessage("You have been banned from Riple, due to reports made against you. If you feel this is mistake, please email Riple for support and it will be investigated. If you do decide to use Riple again, please follow the rules so that everyone can enjoy what Riple has to offer. Thank you.");
        builder.setNegativeButton("EMAIL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"kevinhodgesriple@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ban investigation request");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, ""));
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}