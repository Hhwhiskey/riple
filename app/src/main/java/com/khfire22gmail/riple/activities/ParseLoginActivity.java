package com.khfire22gmail.riple.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22gmail.riple.MainActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.utils.MessageService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(getApplicationContext(), MainActivity.class);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            emailVerified = user.getBoolean("emailVerified");
            if (emailVerified) {
                startActivity(intent);
            } else {
                Toast.makeText(ParseLoginActivity.this, "Please verify your email" +
                                " address before you begin using Riple!",
                        Toast.LENGTH_LONG ).show();
            }
        }

        setContentView(R.layout.activity_parse_login);

        loginButton = (Button) findViewById(R.id.button_login);
        signUpButton = (Button) findViewById(R.id.button_signup);
        emailField = (EditText) findViewById(R.id.email_login);
        passwordField = (EditText) findViewById(R.id.password_login);
        forgotPassword = (TextView) findViewById(R.id.forgot_password_tv);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailField.getText().toString();
                email = email.toLowerCase();
                password = passwordField.getText().toString();

                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            emailVerified = user.getBoolean("emailVerified");
//                            if (emailVerified) {
                                startActivity(intent);

//                            } else {
                               Toast.makeText(ParseLoginActivity.this, "Please verify your email" +
                                       " address before you begin using Riple!",
                                       Toast.LENGTH_LONG ).show();
//                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Wrong username/password combo",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailField.getText().toString();
                email = email.toLowerCase();
                password = passwordField.getText().toString();


                int passwordLength = passwordField.getText().length();

                if ( passwordLength > 5) {

                    final ParseUser user = new ParseUser();
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.put("username", email);
                    user.put("email", email);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ParseLoginActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Not so fast...");
                    builder.setMessage("I will not post any offensive material and I will report any offensive material I encounter");
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

                            user.signUpInBackground(new SignUpCallback() {
                                public void done(com.parse.ParseException e) {
                                    if (e == null) {
                                        if (user != null) {
                                            emailVerified = user.getBoolean("emailVerified");
//                                            if (emailVerified) {
                                                startActivity(intent);

//                                           } else {
                                                Toast.makeText(ParseLoginActivity.this, "Please verify your email" +
                                                                " address before you begin using Riple!",
                                                        Toast.LENGTH_LONG ).show();
//                                            }
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "There was an error signing up. User name must be unique"
                                                , Toast.LENGTH_LONG).show();
                                    }


                                    //Create riple count tracker on UserRipleCount table to avoid ACL restrictions
                                    ParseObject userRipleCount = new ParseObject("UserRipleCount");
                                    userRipleCount.put("userPointer", user);
                                    userRipleCount.put("ripleCount", 0);
                                    userRipleCount.saveInBackground();

//                                  //Also create riple count tracker on the currentUser table for ease of use
                                    user.put("userRipleCount", 0);
                                    user.saveInBackground();

                                }
                            });

                        }
                    });
                    builder.show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Your Username must be at least 3 characters and your password " +
                                    "must be at least 6 characters",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    // TODO: 11/30/2015
    @Override
    public void onDestroy() {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }
}