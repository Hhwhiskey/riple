package com.khfire22gmail.riple.tabs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


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
    public static ArrayList<ParseObject> dropObjectsList = new ArrayList<>();
    public static ArrayList<DropItem> dropTabInteractionList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drop_tab, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.drop_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadDropItemsFromParse();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator(animator);
//        mRecyclerView.setItemAnimator(new SlideInOutLeftDefaultItemAnimator(mRecyclerView));

        return view;
    }

    public void loadDropItemsFromParse() {

        final ArrayList<DropItem> dropList = new ArrayList<>();

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

                        //Collects Drop Objects
                        dropObjectsList.add(list.get(i));

                        final DropItem dropItem = new DropItem();

                        ParseFile profilePicture = (ParseFile) list.get(i).get("authorPicture");
                        if (profilePicture != null) {
                            profilePicture.getDataInBackground(new GetDataCallback() {

                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                    }
                                }
                            });
                        }

                        //ObjectId
                        dropItem.setObjectId(list.get(i).getObjectId());
                        //Picture
//                        dropItem.setFacebookId(list.get(i).getString("facebookId"));
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

                        //Id that connects commenterName to drop
//                              dropItem.setCommenterName(list.get(i).getString("commenterName"));

                        dropList.add(dropItem);
                    }
                }
                dropTabInteractionList = dropList;
                updateRecyclerView(dropList);

            }
        });
    }

    private void updateRecyclerView(ArrayList<DropItem> dropList) {
        Log.d("kevinDropList", "Drop LIST SIZE: " + dropList.size());

        mDropAdapter = new DropAdapter(getActivity(), dropList, "drop");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mDropAdapter);
        scaleAdapter.setDuration(250);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}