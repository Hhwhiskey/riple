package com.khfire22gmail.riple.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.khfire22gmail.riple.LoginActivity;
import com.khfire22gmail.riple.R;
import com.parse.ParseUser;


public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //onLogoutClick();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutButton) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLogoutClick() {
        logout();

    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    }
}
