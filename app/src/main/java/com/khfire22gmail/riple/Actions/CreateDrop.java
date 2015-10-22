package com.khfire22gmail.riple.actions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.khfire22gmail.riple.R;


public class CreateDrop extends AppCompatActivity {

    private String dropTitle;
    private String dropDescription;
    private AutoCompleteTextView dropTitleView;
    private AutoCompleteTextView dropDescriptionView;
    private View editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drop);
        editText.requestFocus();

        //        dropDescription = String.valueOf(dropDescriptionView.getText());
//        dropTitle = String.valueOf(dropTitleView.getText());

        /*dropDescriptionView = (AutoCompleteTextView) findViewById(R.id.drop_description);
        dropTitleView = (AutoCompleteTextView) findViewById(R.id.drop_title);

        dropTitle = dropTitleView.getEditableText().toString();
        dropDescription = dropDescriptionView.getEditableText().toString();*/

        /*Button button = (Button) findViewById(R.id.button_post_drop);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                *//*Intent intent = new Intent(CreateDrop.this, MainActivity.class);
                startActivity(intent);*//*
                createDrop();

            }
        });*/
    }



   /* public String getDropTitle() {
        return dropTitle;
    }

    public void setDropTitle(String dropTitle) {
        this.dropTitle = dropTitle;
    }

    public String getDropDescription() {
        return dropDescription;
    }

    public void setDropDescription(String dropDescription) {
        this.dropDescription = dropDescription;
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_drop, menu);
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
