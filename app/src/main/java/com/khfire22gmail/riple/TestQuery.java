package com.khfire22gmail.riple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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

/**
 * Created by Kevin on 1/1/2016.
 */
public class TestQuery {

    public void loadRipleItemsFromParse() {

        final ArrayList<DropItem> ripleListFromParse = new ArrayList<>();

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseRelation createdRelation = currentUser.getRelation("createdDrops");
        ParseRelation completedRelation = currentUser.getRelation("completedDrops");

        ParseQuery createdQuery = createdRelation.getQuery();
        ParseQuery completedQuery = completedRelation.getQuery();

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(createdQuery);
        queries.add(completedQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.include("authorPointer");
        mainQuery.orderByDescending("createdAt");
        mainQuery.setLimit(10);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> listParse, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < listParse.size(); i++) {

                        final DropItem dropItem = new DropItem();

                        //Drop Author Data//////////////////////////////////////////////////////////
                        ParseObject authorData = (ParseObject) listParse.get(i).get("authorPointer");

                        ParseFile parseProfilePicture = (ParseFile) authorData.get("commenterParseProfilePicture");
                        if (parseProfilePicture != null) {
                            parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
//                                        updateRecyclerView(ripleListFromParse);
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

                        //Drop Data////////////////////////////////////////////////////////////////
                        //DropObjectId
                        dropItem.setObjectId(listParse.get(i).getObjectId());
                        //CreatedAt
                        dropItem.setCreatedAt(listParse.get(i).getCreatedAt());
                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                        //Drop description
                        dropItem.setDescription(listParse.get(i).getString("description"));

                        //Riple Count
                        int ripleCount = (listParse.get(i).getInt("ripleCount"));
                        if (ripleCount == 1) {
                            dropItem.setRipleCount(String.valueOf(listParse.get(i).getInt("ripleCount") + " Riple"));
                        } else {
                            dropItem.setRipleCount(String.valueOf(listParse.get(i).getInt("ripleCount") + " Riples"));
                        }

                        //Comment Count
                        int commentCount = (listParse.get(i).getInt("commentCount"));
                        if (commentCount == 1) {
                            dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comment"));
                        }else {
                            dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comments"));
                        }

                        ripleListFromParse.add(dropItem);
//                        ParseObject.pinAllInBackground("pinnedQuery", listParse);
                    }
                }
            }
        });
    }
}
