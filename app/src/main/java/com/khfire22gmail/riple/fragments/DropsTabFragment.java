package com.khfire22gmail.riple.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.TextView;

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

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Kevin on 9/8/2015.
 */
public class DropsTabFragment extends Fragment {

    private RecyclerView mDropRecyclerView;
    private Button button;
    private List<DropItem> mDropList;
    private List<DropItem> dropList;
    private DropAdapter mDropAdapter;
    public static ArrayList<ParseObject> dropObjectsList = new ArrayList<>();
    public static ArrayList<DropItem> dropTabInteractionList;
    private boolean dropTips;
    private TextView dropEmptyView;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drop_tab, container, false);

        mDropRecyclerView = (RecyclerView) view.findViewById(R.id.drop_recycler_view);
        mDropRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDropRecyclerView.setItemAnimator(new SlideInLeftAnimator(new AnticipateInterpolator(2f)));
        mDropRecyclerView.getItemAnimator().setRemoveDuration(500);
        dropEmptyView = (TextView) view.findViewById(R.id.drop_tab_empty_view);

        //Swipe Refresh
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.drop_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                loadDropItemsFromParse();
                new Task().execute();
            }
        });






        //        loadSavedPreferences();

        loadDropItemsFromParse();

        return view;
    }

    public void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean dropTipBoolean = sharedPreferences.getBoolean("dropTipBoolean", true);
        if (dropTipBoolean) {
            dropTip();
        }
    }

    public void savePreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void dropTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DropsTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Drops...");
        builder.setMessage("This is your Drops list. Your very own to-do list of Drops. Once added to " +
                "this list, challenge yourself to complete them as you go about your" +
                " day. Once complete, hit the checkbox, the author of the Drop will receive a" +
                " Riple, and you will have helped make the world a better place; a win-win.");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePreferences("dropTipBoolean", false);

            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public void loadDropItemsFromParse() {

        final ArrayList<DropItem> dropList = new ArrayList<>();

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation relation = user.getRelation("todoDrops");

        ParseQuery query = relation.getQuery();

        query.include("authorPointer");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {

                    for (int i = 0; i < list.size(); i++) {

                        //Collects Drop Objects
//                        dropObjectsList.add(list.get(i));

                        final DropItem dropItem = new DropItem();

                        //Drop Author Data//////////////////////////////////////////////////////////
                        ParseObject authorData = (ParseObject) list.get(i).get("authorPointer");

                        ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                        if (parseProfilePicture != null) {
                            parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                        updateRecyclerView(dropList);
                                    }
                                }
                            });
                        }

                        //dropItemAll.setAuthorName(authorName);
                        dropItem.setAuthorName((String) authorData.get("displayName"));
                        //Author id
                        dropItem.setAuthorId(authorData.getObjectId());
                        //Author Rank
                        dropItem.setAuthorRank(authorData.getString("userRank"));
                        //Author Riple Count
                        dropItem.setAuthorRipleCount(String.valueOf(authorData.getInt("userRipleCount")));

                        //Drop Data////////////////////////////////////////////////////////////////
                        //DropObjectId
                        dropItem.setObjectId(list.get(i).getObjectId());
                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));
                        //CreatedAt
                        dropItem.setCreatedAt(list.get(i).getCreatedAt());

                        //Riple Count
                        int ripleCount = (list.get(i).getInt("ripleCount"));
                        if (ripleCount == 1) {
                            dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riple"));
                        } else {
                            dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                        }

                        //Comment Count
                        int commentCount = (list.get(i).getInt("commentCount"));
                        if (commentCount == 1) {
                            dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comment"));
                        }else {
                            dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));
                        }

                        dropList.add(dropItem);
                    }
                }
                dropTabInteractionList = dropList;


            }
        });
    }

    private void updateRecyclerView(ArrayList<DropItem> dropList) {
        Log.d("kevinDropList", "Drop LIST SIZE: " + dropList.size());

        if (dropList.isEmpty()) {
            mDropRecyclerView.setVisibility(View.GONE);
            dropEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mDropRecyclerView.setVisibility(View.VISIBLE);
            dropEmptyView.setVisibility(View.GONE);
        }

        // Alpha animation
        mDropAdapter = new DropAdapter(getActivity(), dropList, "drop");
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mDropAdapter);
        mDropRecyclerView.setAdapter(alphaAdapter);
        alphaAdapter.setDuration(1000);

        // Alpha and scale animation
//        mDropAdapter = new DropAdapter(getActivity(), dropList, "drop");
//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mDropAdapter);
//        mDropRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
//        scaleAdapter.setDuration(500);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class Task extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }
}