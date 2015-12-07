package com.khfire22gmail.riple.sinch;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.khfire22gmail.riple.R;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Kevin on 12/6/2015.
 */
public class FriendsAdapter extends ArrayAdapter <ParseObject> {

    public FriendsAdapter(Context context, List<ParseObject> objects) {
        super(context, R.layout.user_list_item, objects);
    }
}
