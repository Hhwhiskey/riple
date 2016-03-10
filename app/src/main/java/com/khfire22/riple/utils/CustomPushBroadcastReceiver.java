package com.khfire22.riple.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.khfire22.riple.R;
import com.khfire22.riple.activities.MessagingActivity;
import com.khfire22.riple.activities.ViewDropActivity;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin on 1/25/2016.
 */
public class CustomPushBroadcastReceiver extends BroadcastReceiver {


    private static int NOTIFICATION_ID;
    String currentUserId = ParseUser.getCurrentUser().getObjectId();

    public CustomPushBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle data = intent.getExtras();

        if (data != null) {
            String jsonData = data.getString("com.parse.Data");

            // Try for message notification
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                final String pusherId = jsonObject.getString("sendPusherId");
                final String pusherName = jsonObject.getString("sendPusherName");
                final String pushMessageBody = jsonObject.getString("pushMessageBody");

                ParseQuery pusherPictureQuery = ParseQuery.getQuery("_User");
                pusherPictureQuery.getInBackground(pusherId, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseObject, ParseException e) {
                        if (e != null) {
                        } else {
                            ParseFile pusherProfileImage = parseObject.getParseFile("parseProfilePicture");
                            pusherProfileImage.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    if (e == null) {
                                        if (bmp != null) {
                                            Bitmap resizedPusherBitmap = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                                            generateMessageNotification(context, pusherName, pusherId, pushMessageBody, resizedPusherBitmap);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (JSONException notAMessage) {
                notAMessage.printStackTrace();
            }

            // Try for allDrop notification
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                final String dropAuthorId = jsonObject.getString("dropAuthorId");
                final String dropAuthorName = jsonObject.getString("dropAuthorName");
                final String dropAuthorRank = jsonObject.getString("dropAuthorRank");
                final String dropAuthorRipleCount = jsonObject.getString("dropAuthorRipleCount");
                final String dropAuthorLocation = jsonObject.getString("dropAuthorLocation");
                final String dropAuthorInfo = jsonObject.getString("dropAuthorInfo");

                final String dropObjectId = jsonObject.getString("dropObjectId");
                final String dropContent = jsonObject.getString("dropContent");
                final String dropCreatedAt = jsonObject.getString("dropCreatedAt");

                // Specifies that a notification without this variable is in fact a comment notification
                final Boolean allDropsBoolean = jsonObject.getBoolean("allDropsBoolean");

                // If this was sent by the currentUser, then don't show the notification
                if (!dropAuthorId.equals(currentUserId)) {

                    ParseQuery pusherPictureQuery = ParseQuery.getQuery("_User");
                    pusherPictureQuery.getInBackground(dropAuthorId, new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseObject, ParseException e) {
                            if (e != null) {
                            } else {
                                ParseFile pusherProfileImage = parseObject.getParseFile("parseProfilePicture");
                                pusherProfileImage.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, ParseException e) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        if (e == null) {
                                            if (bmp != null) {
                                                Bitmap resizedPusherBitmap = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                                                generateAllDropsNotification(context, dropAuthorName, dropAuthorId, dropAuthorRank, dropAuthorRipleCount, dropAuthorLocation, dropAuthorInfo, dropObjectId, dropCreatedAt, dropContent, resizedPusherBitmap);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            } catch (JSONException notANewDrop) {
                notANewDrop.printStackTrace();
            }

            // Drop completion notification
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                final String dropAuthorId = jsonObject.getString("authorId");
                final String dropAuthorName = jsonObject.getString("authorDisplayName");
                final String dropAuthorRank = jsonObject.getString("authorRank");
                final String dropAuthorRipleCount = jsonObject.getString("authorRipleCount");
                final String dropAuthorLocation = jsonObject.getString("authorLocation");
                final String dropAuthorInfo = jsonObject.getString("authorInfo");

                final String dropObjectId = jsonObject.getString("dropObjectId");
                final String dropContent = jsonObject.getString("dropContent");
                final String dropCreatedAt = jsonObject.getString("dropCreatedAt");

                final String completedObjectId = jsonObject.getString("completedObjectId");
                final String completedDisplayName = jsonObject.getString("completedDisplayName");

                if (!completedObjectId.equals(currentUserId)) {

                    ParseQuery pusherPictureQuery = ParseQuery.getQuery("_User");
                    pusherPictureQuery.getInBackground(completedObjectId, new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseObject, ParseException e) {
                            if (e != null) {
                            } else {
                                ParseFile pusherProfileImage = parseObject.getParseFile("parseProfilePicture");
                                pusherProfileImage.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, ParseException e) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        if (e == null) {
                                            if (bmp != null) {
                                                Bitmap resizedPusherBitmap = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                                                generateCompletedDropNotification(context, dropAuthorName, dropAuthorId, dropAuthorRank, dropAuthorRipleCount, dropAuthorLocation, dropAuthorInfo, dropObjectId, dropCreatedAt, dropContent, completedDisplayName, resizedPusherBitmap);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            } catch (JSONException notACompletedDrop) {
                notACompletedDrop.printStackTrace();
            }


            // Try for comment notification
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean interactedDropsSharedPrefs = sharedPreferences.getBoolean("interactedDropsCB", true);

            if (interactedDropsSharedPrefs) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    final String dropAuthorId = jsonObject.getString("dropAuthorId");
                    final String dropAuthorName = jsonObject.getString("dropAuthorName");
                    final String dropAuthorRank = jsonObject.getString("dropAuthorRank");
                    final String dropAuthorRipleCount = jsonObject.getString("dropAuthorRipleCount");
                    final String dropAuthorLocation = jsonObject.getString("dropAuthorLocation");
                    final String dropAuthorInfo = jsonObject.getString("dropAuthorInfo");

                    final String dropObjectId = jsonObject.getString("dropObjectId");
                    final String dropDescription = jsonObject.getString("dropContent");
                    final String dropCreatedAt = jsonObject.getString("dropCreatedAt");

                    final String commenterId = jsonObject.getString("commenterId");
                    final String commenterName = jsonObject.getString("commenterName");
                    final String commentText = jsonObject.getString("commentText");

                    if (!commenterId.equals(currentUserId)) {

                        ParseQuery pusherPictureQuery = ParseQuery.getQuery("_User");
                        pusherPictureQuery.getInBackground(commenterId, new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseObject, ParseException e) {
                                if (e != null) {
                                } else {
                                    ParseFile pusherProfileImage = parseObject.getParseFile("parseProfilePicture");
                                    pusherProfileImage.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] bytes, ParseException e) {
                                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            if (e == null) {
                                                if (bmp != null) {
                                                    Bitmap resizedPusherBitmap = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                                                    generateCommentNotification(context, dropAuthorName, dropAuthorId, dropAuthorRank, dropAuthorRipleCount, dropAuthorLocation, dropAuthorInfo, dropObjectId, dropCreatedAt, dropDescription, commenterId, commenterName, commentText, resizedPusherBitmap);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }


                } catch (JSONException notAComment) {
                    notAComment.printStackTrace();
                }
            }
        }
    }

    private void generateMessageNotification(Context context, String pusherName, String pusherId, String messageBody, Bitmap pusherBitmap) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sharedPrefUnreadCount = sharedPreferences.getInt(pusherId, 0);

        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ripleicon2)
                        .setLargeIcon(pusherBitmap)
                        .setContentTitle(pusherName)
                        .setContentText(messageBody)
                        .setTicker(pusherName + ":" + " " + messageBody)
                        .setSound(Uri.parse(String.valueOf(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.waterdropsound))))
                        .setVibrate(new long[]{0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, pusherId, sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, MessagingActivity.class);
        intentExtra.putExtra("RECIPIENT_ID", pusherId);
        intentExtra.putExtra("unreadCount", sharedPrefUnreadCount);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(pusherId, 1, mBuilder.build());
    }

    private void generateAllDropsNotification(Context context, String commenterName, String authorId, String authorRank, String authorRipleCount, String userLastLocation, String authorInfo, String dropObjectId, String createdAt, String dropDescription, Bitmap pusherBitmap) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sharedPrefUnreadCount = sharedPreferences.getInt("unreadDrops", 0);

        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ripleicon2)
                        .setLargeIcon(pusherBitmap)
                        .setContentTitle("Drop from " + commenterName + "!")
                        .setContentText(dropDescription)
                        .setTicker(commenterName + " has posted a new Drop!" + " " + dropDescription)
                        .setSound(Uri.parse(String.valueOf(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.waterdropsound))))
                        .setVibrate(new long[]{0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, "unreadDrops", sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, ViewDropActivity.class);
        intentExtra.putExtra("authorId", authorId);
        intentExtra.putExtra("authorName", commenterName);
        intentExtra.putExtra("authorRank", authorRank);
        intentExtra.putExtra("clickedUserRipleCount", authorRipleCount);
        intentExtra.putExtra("userLastLocation", userLastLocation);
        intentExtra.putExtra("clickedUserInfo", authorInfo);

        intentExtra.putExtra("dropObjectId", dropObjectId);
        intentExtra.putExtra("dropDescription", dropDescription);
        intentExtra.putExtra("createdAt", createdAt);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 2, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify("New Drop", 2, mBuilder.build());
    }

    private void generateCommentNotification(Context context, String authorName, String authorId, String authorRank, String authorRipleCount, String userLastLocation, String authorInfo, String dropObjectId, String createdAt, String dropDescription, String commenterId, String commenterName, String commentText, Bitmap pusherBitmap) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sharedPrefUnreadCount = sharedPreferences.getInt("unreadComments", 0);

        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ripleicon2)
                        .setLargeIcon(pusherBitmap)
                        .setContentTitle("Comment from " + commenterName + "!")
                        .setContentText(commentText)
                        .setTicker(commenterName + " has posted a new comment!" + " " + commentText)
                        .setSound(Uri.parse(String.valueOf(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.waterdropsound))))
                        .setVibrate(new long[]{0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, "unreadComments", sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, ViewDropActivity.class);
        intentExtra.putExtra("authorId", authorId);
        intentExtra.putExtra("authorName", authorName);
        intentExtra.putExtra("authorRank", authorRank);
        intentExtra.putExtra("clickedUserRipleCount", authorRipleCount);
        intentExtra.putExtra("userLastLocation", userLastLocation);
        intentExtra.putExtra("clickedUserInfo", authorInfo);

        intentExtra.putExtra("dropObjectId", dropObjectId);
        intentExtra.putExtra("dropDescription", dropDescription);
        intentExtra.putExtra("createdAt", createdAt);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 3, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(dropObjectId, 3, mBuilder.build());
    }

    private void generateCompletedDropNotification(Context context, String dropAuthorName, String dropAuthorId, String dropAuthorRank, String dropAuthorRipleCount, String dropAuthorLocation, String dropAuthorInfo, String dropObjectId, String dropCreatedAt, String dropContent, String completedDisplayName, Bitmap resizedPusherBitmap) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sharedPrefUnreadCount = sharedPreferences.getInt("completedDrops", 0);

        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ripleicon2)
                        .setLargeIcon(resizedPusherBitmap)
                        .setContentTitle(completedDisplayName + " completed this Drop!")
                        .setContentText(dropContent)
                        .setTicker(completedDisplayName + " completed this Drop!" + " " + dropContent)
                        .setSound(Uri.parse(String.valueOf(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.waterdropsound))))
                        .setVibrate(new long[]{0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, "completedDrops", sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, ViewDropActivity.class);
        intentExtra.putExtra("authorId", dropAuthorId);
        intentExtra.putExtra("commenterName", dropAuthorName);
        intentExtra.putExtra("authorRank", dropAuthorRank);
        intentExtra.putExtra("clickedUserRipleCount", dropAuthorRipleCount);
        intentExtra.putExtra("userLastLocation", dropAuthorLocation);
        intentExtra.putExtra("clickedUserInfo", dropAuthorInfo);

        intentExtra.putExtra("dropObjectId", dropObjectId);
        intentExtra.putExtra("dropDescription", dropContent);
        intentExtra.putExtra("createdAt", dropCreatedAt);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 4, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(dropObjectId, 4, mBuilder.build());

    }
}
