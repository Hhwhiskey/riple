package com.khfire22gmail.riple.fragments;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import java.util.Iterator;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

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
    private TextView trickleEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trickle_tab, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator(new AnticipateInterpolator(2f)));
        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        //Swipe Refresh
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.swipe_trickle);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                loadAllDropsFromParse();
                new Task().execute();
            }
        });

        trickleEmptyView = (TextView) view.findViewById(R.id.trickle_tab_empty_view);

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

    @Override
    public void onResume() {

        super.onResume();
    }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadAllDropsFromParse() {

        final ParseQuery<ParseObject> dropQuery = ParseQuery.getQuery("Drop");

        dropQuery.orderByDescending("createdAt");
        dropQuery.include("authorPointer");

        dropQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {

                    allDropsList.clear();

                    for (int i = 0; i < list.size(); i++) {

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

        if (filteredDropList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            trickleEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            trickleEmptyView.setVisibility(View.GONE);
        }

        mTrickleAdapter = new DropAdapter(getActivity(), filteredDropList, "trickle");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mTrickleAdapter);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        scaleAdapter.setDuration(500);
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