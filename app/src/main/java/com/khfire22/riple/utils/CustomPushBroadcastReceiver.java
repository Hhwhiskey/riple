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

//    static int unreadCount = 0;
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
            } catch (JSONException e) {
                e.printStackTrace();
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
                                                generateAllDropsNotification(context, dropAuthorName, dropAuthorId, dropAuthorRank, dropAuthorRipleCount, dropAuthorLocation,  dropAuthorInfo, dropObjectId, dropCreatedAt, dropContent, resizedPusherBitmap);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Try for comment notification
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
                                            generateCommentNotification(context, dropAuthorName, dropAuthorId, dropAuthorRank, dropAuthorRipleCount, dropAuthorLocation, dropAuthorInfo, dropObjectId, dropCreatedAt, dropDescription, commenterId, commenterName, commentText, resizedPusherBitmap);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
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
                        .setVibrate(new long[] {0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, pusherId, sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, MessagingActivity.class);
        intentExtra.putExtra("RECIPIENT_ID", pusherId);
        intentExtra.putExtra("unreadCount", sharedPrefUnreadCount);

//        int requestCode = ("someString" + System.currentTimeMillis()).hashCode();

        PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void generateAllDropsNotification(Context context, String commenterName, String authorId, String authorRank, String authorRipleCount, String userLastLocation, String authorInfo, String dropObjectId, String createdAt, String dropDescription, Bitmap pusherBitmap) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sharedPrefUnreadCount = sharedPreferences.getInt("unreadAllDrops", 0);

        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ripleicon2)
                        .setLargeIcon(pusherBitmap)
                        .setContentTitle("Drop from " + commenterName + "!")
                        .setContentText(dropDescription)
                        .setTicker(commenterName + " has posted a new Drop!" + " " + dropDescription)
                        .setSound(Uri.parse(String.valueOf(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.waterdropsound))))
                        .setVibrate(new long[] {0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, "unreadAllDrops", sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, ViewDropActivity.class);
        intentExtra.putExtra("authorId", authorId);
        intentExtra.putExtra("commenterName", commenterName);
        intentExtra.putExtra("authorRank", authorRank);
        intentExtra.putExtra("clickedUserRipleCount", authorRipleCount);
        intentExtra.putExtra("userLastLocation", userLastLocation);
        intentExtra.putExtra("clickedUserInfo", authorInfo);

        intentExtra.putExtra("dropObjectId", dropObjectId);
        intentExtra.putExtra("dropDescription", dropDescription);
        intentExtra.putExtra("createdAt", createdAt);


//        int requestCode = ("someString" + System.currentTimeMillis()).hashCode();

        PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
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
                        .setVibrate(new long[] {0, 1000})
                        .setLights(Color.BLUE, 1000, 1000)
                        .setAutoCancel(true)
                        .setNumber(++sharedPrefUnreadCount);

        // Save the current unread count to shared prefs so that it can be displayed/reset
        SaveToSharedPrefs.saveIntPreferences(context, "unreadComments", sharedPrefUnreadCount);

        // Intent for the notification
        Intent intentExtra = new Intent(context, ViewDropActivity.class);
        intentExtra.putExtra("authorId", authorId);
        intentExtra.putExtra("commenterName", authorName);
        intentExtra.putExtra("authorRank", authorRank);
        intentExtra.putExtra("clickedUserRipleCount", authorRipleCount);
        intentExtra.putExtra("userLastLocation", userLastLocation);
        intentExtra.putExtra("clickedUserInfo", authorInfo);

        intentExtra.putExtra("dropObjectId", dropObjectId);
        intentExtra.putExtra("dropDescription", dropDescription);
        intentExtra.putExtra("createdAt", createdAt);

        PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intentExtra, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
