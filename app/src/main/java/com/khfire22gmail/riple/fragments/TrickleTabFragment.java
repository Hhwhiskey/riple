package com.khfire22gmail.riple.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

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
import java.util.Iterator;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


/**
 * Created by Kevin on 9/8/2015.
 */
// TODO WaveSwipeRefreshLayout
public class TrickleTabFragment extends Fragment /*implements WaveSwipeRefreshLayout.OnRefreshListener*/ {

    public static final String TAG = TrickleTabFragment.class.getSimpleName();

    private ListView mListview;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private String currentUserObject;
    private String currentUserName;
    private RecyclerView.ItemAnimator animator;
    private RecyclerView mRecyclerView;
    public static DropAdapter mTrickleAdapter;
    final ArrayList <DropItem> hasRelationList = new ArrayList<>();
    public static final ArrayList <ParseObject> trickleObjectsList = new ArrayList<>();
    public static final ArrayList <DropItem> allDropsList  = new ArrayList<>();
    public static ArrayList<DropItem> trickleTabInteractionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trickle_tab, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        loadSavedPreferences();
        loadAllDropsFromParse();

        return view;
    }

    public void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean trickleTipBoolean = sharedPreferences.getBoolean("trickleTipBoolean", true);
        if (trickleTipBoolean) {
            trickleTip();
        }
    }

    public void savePreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void trickleTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrickleTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Trickle...");
        builder.setMessage("This is the Trickle. A steady flow of Drops where every Riple begins. " +
                "Add these Drops to your to-do list with the conveniently placed switch. " +
                "Touch the author or the Drop to get a better look at them, respectively. Please do" +
                " your part and report harassment, by touching the setting button seen on each Drop" +
                ". If someone posts something offensive here then report it so it can be taken care of promptly");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePreferences("trickleTipBoolean", false);

            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /*private void initView() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) getActivity().findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(0x00000000);
//        mWaveSwipeRefreshLayout.setMaxDropHeight(1500);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.trickle_recycler_view);
    }*/

    @Override
    public void onResume() {
       /* mWaveSwipeRefreshLayout.setRefreshing(true);
        refresh();*/
        super.onResume();
    }

    /*@Override
    public void onRefresh() {
        refresh();
    }*/

    /*private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                *//*mWaveSwipeRefreshLayout.setRefreshing(false);
                getItemsFromParse();
                dropAdapter = new DropAdapter(getActivity(), list, "trickle");*//*
            }
        }, 3000);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            mWaveSwipeRefreshLayout.setRefreshing(true);
//            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadAllDropsFromParse() {

        final ParseQuery<ParseObject> dropQuery = ParseQuery.getQuery("Drop");

        dropQuery.orderByDescending("createdAt");
        dropQuery.include("authorPointer");
//        dropQuery.include("objectId");
//        dropQuery.include("displayName");

        dropQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {

                    allDropsList.clear();

                    for (int i = 0; i < list.size(); i++) {

//                        String authorId = authorData.getObjectId();

                        //Collects Drop Objects
                        trickleObjectsList.add(list.get(i));

                        final DropItem dropItemAll = new DropItem();


                        //Drop Author Data/////////////////////////////////////////////////////////
                        ParseObject authorData = (ParseObject) list.get(i).get("authorPointer");

                        ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                        if (parseProfilePicture != null) {
                            parseProfilePicture.getDataInBackground(new GetDataCallback() {

                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItemAll.setParseProfilePicture(bmp);
                                    }
                                }
                            });
                        }

                        //dropItemAll.setAuthorName(authorName);
                        dropItemAll.setAuthorName((String) authorData.get("displayName"));
                        //Author id
                        dropItemAll.setAuthorId(authorData.getObjectId());


                        //Drop Data///////////////////////////////////////////////////////////////
                        //DropObjectId
                        dropItemAll.setObjectId(list.get(i).getObjectId());
                        //Drop description
                        dropItemAll.setDescription(list.get(i).getString("description"));
                        //CreatedAt
                        dropItemAll.setCreatedAt(list.get(i).getCreatedAt());
                        //Riple Count
                        dropItemAll.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                        //Comment Count
                        dropItemAll.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        allDropsList.add(dropItemAll);
                        Log.d("KevinData", "ArrayListContains" + allDropsList);


                    }
                }

                loadRelationDropsFromParse();
            }
        });

    }

    public void loadRelationDropsFromParse() {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation relation = user.getRelation("hasRelationTo");

        final ParseQuery hasRelationQuery = relation.getQuery();

//        hasRelationToQuery.orderByDescending("createdAt");

        hasRelationQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        DropItem dropItemRelation = new DropItem();

                        dropItemRelation.setObjectId(list.get(i).getObjectId());

                        hasRelationList.add(dropItemRelation);
                    }
                }
                filterDrops(hasRelationList, allDropsList);
            }
        });

    }



    public void filterDrops(ArrayList <DropItem> hasRelationList, ArrayList <DropItem> filteredDropList) {
        Iterator<DropItem> allDropsIterator = filteredDropList.iterator();


        while(allDropsIterator.hasNext()) {
            DropItem dropItemAll = allDropsIterator.next();

            for(DropItem dropItemRelation  : hasRelationList) {
                if(dropItemAll.getObjectId().equals(dropItemRelation.getObjectId())){
                    allDropsIterator.remove();
                }
            }
        }

        trickleTabInteractionList = filteredDropList;
        updateRecyclerView(filteredDropList);
    }

    private void updateRecyclerView(ArrayList<DropItem> filteredDropList) {
        Log.d("KEVIN", "TRICKLE LIST SIZE: " + filteredDropList.size());

        mTrickleAdapter = new DropAdapter(getActivity(), filteredDropList, "trickle");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mTrickleAdapter);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        scaleAdapter.setDuration(250);
    }
}