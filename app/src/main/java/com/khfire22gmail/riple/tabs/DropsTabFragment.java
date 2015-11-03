package com.khfire22gmail.riple.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kevin on 9/8/2015.
 */
public class DropsTabFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Button button;
    private List<DropItem> mDropList;
    private List<DropItem> dropList;
    private DropAdapter mDropAdapter;
    private RecyclerView.ItemAnimator animator;
    private CheckBox completeCheckBox;
    public static ArrayList<ParseObject> dropObjectsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_drop, container, false);

        dropObjectsList = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.drop_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadDropItemsFromParse();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator(animator);
//        mRecyclerView.setItemAnimator(new SlideInOutLeftDefaultItemAnimator(mRecyclerView));

        return view;
    }

    public void loadDropItemsFromParse() {

        final List<DropItem> dropList = new ArrayList<>();

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation relation = user.getRelation("todoDrops");

        ParseQuery query = relation.getQuery();

        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        dropObjectsList.add(list.get(i));

                        DropItem dropItem = new DropItem();

                        //ObjectId
                        dropItem.setObjectId(list.get(i).getObjectId());
                        //Picture
                        dropItem.setFacebookId(list.get(i).getString("facebookId"));
                        //Author name
                        dropItem.setAuthorName(list.get(i).getString("name"));
                        //Author id
                        dropItem.setAuthorId(list.get(i).getString("author"));
                        //Date
                        dropItem.setCreatedAt(list.get(i).getCreatedAt());

//                      dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop Title
//                        dropItem.setTitle(list.get(i).getString("title"));

                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenter to drop
//                              dropItem.setCommenter(list.get(i).getString("commenter"));

                        dropList.add(dropItem);
                    }

                    Log.i("KEVIN", "PARSE LIST SIZE: " + dropList.size());
                    updateRecyclerView(dropList);
                }
            }
        });
    }

    private void updateRecyclerView(List<DropItem> items) {
        Log.d("KEVIN", "DROP LIST SIZE: " + items.size());

        mDropList = items;

        mDropAdapter = new DropAdapter(getActivity(), mDropList, "drop");
        mRecyclerView.setAdapter(mDropAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


