package com.khfire22gmail.riple.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.ViewUserActivity;
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
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTabFragment extends Fragment {

    private ImageView profilePictureView;
    private TextView nameView;
    private RecyclerView mRecyclerView;
    private List<DropItem> mRipleList;
    private DropAdapter mRipleAdapter;
    private RecyclerView.ItemAnimator animator;
    private TextView profileRankView;
    private TextView profileRipleCountView;
    private TextView parseRankView;
    private ParseFile parseProfilePicture;
    private ParseUser currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_riple_tab,container,false);


//        Profile Card
        profilePictureView = (ImageView) view.findViewById(R.id.profile_card_picture);
        nameView = (TextView) view.findViewById(R.id.profile_name);
        profileRankView = (TextView) view.findViewById(R.id.profile_rank);
        profileRipleCountView = (TextView) view.findViewById(R.id.profile_riple_count);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.riple_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

        currentUser = ParseUser.getCurrentUser();

        //Update Profile Card
//        updateViewsWithProfileInfo();

        //Query the users created and completed drops_blue
        updateUserInfo();
        loadRipleItemsFromParse();


        profilePictureView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewOtherUser();
            }
        });


//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
//        profilePictureView.setPresetSize(ProfilePictureView.LARGE);

//        Fetch Facebook user info if it is logged
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if ((currentUser != null) && currentUser.isAuthenticated()) {
//
//        }

        return view;
    }

    private void viewOtherUser() {

        String currentUserId = currentUser.getObjectId();
        String currentUserName = currentUser.getString("username");

        Log.d("sDropViewUser", "Clicked User's Id = " + currentUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + currentUserName);

        Intent intent = new Intent(getActivity(), ViewUserActivity.class);
        intent.putExtra("clickedUserId", currentUserId);
        intent.putExtra("clickedUserName",currentUserName);
        getActivity().startActivity(intent);
    }



    public void loadRipleItemsFromParse() {
        final List<DropItem> ripleList = new ArrayList<>();

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation createdRelation = user.getRelation("createdDrops");
        ParseRelation completedRelation = user.getRelation("completedDrops");

        ParseQuery createdQuery = createdRelation.getQuery();
        ParseQuery completedQuery = completedRelation.getQuery();

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(createdQuery);
        queries.add(completedQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.orderByDescending("createdAt");

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

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

                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenterName to drop
//                              dropItem.setCommenterName(list.get(i).getString("commenterName"));

                        ripleList.add(dropItem);

                    }
                }

                updateRecyclerView(ripleList);
            }
        });
    }

    public void updateRecyclerView(List<DropItem> mRipleList) {

        mRipleAdapter = new DropAdapter(getActivity(), mRipleList, "created");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mRipleAdapter);
        scaleAdapter.setDuration(250);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
    }

    private void updateUserInfo() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        String userName = currentUser.getString("username");
        String facebookId = currentUser.getString("facebookId");

        if ((currentUser != null) && currentUser.isAuthenticated()) {

            parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
        }

        //get parse profile picture if exists, if not, store Facebook picture on Parse and show

        if(parseProfilePicture != null) {
            Glide.with(this)
                    .load(parseProfilePicture.getUrl())
                    .crossFade()
                    .fallback(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(profilePictureView);
        } else {
            if (facebookId != null){
                Log.d("MyApp", "FB ID (Main Activity) = " + facebookId);
                new DownloadImageTask(profilePictureView)
                        .execute("https://graph.facebook.com/" + facebookId + "/picture?type=large");
                //loader.displayImage("https://graph.facebook.com/" + fbId + "/picture?type=large", mProfPicField);
            }
        }

        // Update UserName
        if (userName != null) {
            nameView.setText(userName);
        } else {
            nameView.setText("Anonymous");
        }

        String localRank = "\"Drop\"";
        int parseRipleCount = currentUser.getInt("userRipleCount");

        if (parseRipleCount > 19) {
            localRank = ("\"Volunteer\"");//2
        }
        if (parseRipleCount > 39) {
            localRank = ("\"Do-Gooder\"");//3
        }
        if (parseRipleCount > 79) {
            localRank = ("\"Contributor\"");//4
        }
        if (parseRipleCount > 159) {
            localRank = ("\"Patron\"");//5
        }
        if (parseRipleCount > 319) {
            localRank = ("\"Beneficent\"");//6
        }
        if (parseRipleCount > 639) {
            localRank = ("\"Humanitarian\"");//7
        }
        if (parseRipleCount > 1279) {
            localRank = ("\"Saint\"");//8
        }
        if (parseRipleCount > 2559) {
            localRank = ("\"Gandhi\"");//9
        }
        if (parseRipleCount > 5119) {
            localRank = ("\"Mother Teresa\"");//10
        }

        if (localRank != null) {
            currentUser.put("userRank", localRank);
        }

        profileRipleCountView.setText(String.valueOf(parseRipleCount) + " Riples");

        if (localRank != null) {
            profileRankView.setText(localRank);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImageView;


        public DownloadImageTask(ImageView bmImage) {
            this.bmImageView = bmImage;
        }


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("MyApp", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            if (bmImageView != null) {
                bmImageView.setImageBitmap(result);
                //convert bitmap to byte array and upload to Parse
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                final ParseFile file = new ParseFile("parseProfilePicture.png", byteArray);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            currentUser.put("parseProfilePicture", file);
                            currentUser.saveInBackground();
                        }
                    }
                });
            }
        }
    }

    /*private void startLoginActivity() {
        Intent intent = new Intent(this.getActivity(), TitleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }*/



}