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
import android.widget.Toast;

import com.khfire22gmail.riple.MainActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.sinch.MessageService;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ParseLoginActivity extends AppCompatActivity {

    private Button signUpButton;
    private Button loginButton;
    private EditText emailField;
    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String email;
    private String password;
    private Intent intent;
    private Intent serviceIntent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(getApplicationContext(), MainActivity.class);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            startActivity(intent);
            startService(serviceIntent);
        }

        setContentView(R.layout.activity_parse_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
//        emailField = (EditText) findViewById(R.id.loginEmail);
        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField = (EditText) findViewById(R.id.loginPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailField.getText().toString();
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            startActivity(intent);
                            startService(serviceIntent);
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

                username = usernameField.getText().toString();
                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                int usernameLength = usernameField.getText().length();
                int passwordLength = passwordField.getText().length();

                if (usernameLength > 2 && passwordLength > 5) {

                    final ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(username);
                    user.put("username", username);
                    user.put("email", username);

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

                                        startActivity(intent);
                                        startService(serviceIntent);


                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "There was an error signing up. User name must be unique"
                                                , Toast.LENGTH_LONG).show();
                                    }
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
    }

    // TODO: 11/30/2015
    @Override
    public void onDestroy() {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }
}