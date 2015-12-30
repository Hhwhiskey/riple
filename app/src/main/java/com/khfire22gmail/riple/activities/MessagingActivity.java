package com.khfire22gmail.riple.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.MessageAdapter;
import com.khfire22gmail.riple.utils.Constants;
import com.khfire22gmail.riple.utils.MessageService;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MessagingActivity extends AppCompatActivity {

    private String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MyMessageClientListener messageClientListener = new MyMessageClientListener();
    private MessageAdapter messageAdapter;
    private ListView messagesList;
    private ParseUser mCurrentUser;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        mCurrentUser = ParseUser.getCurrentUser();

        bindService(new Intent(this, MessageService.class), serviceConnection, BIND_AUTO_CREATE);

        // Unsub from message notifications
        ParsePush.unsubscribeInBackground("message", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("MyApp", "successfully unsubscribed to the broadcast channel.");
                } else {
                    Log.e("MyApp", "failed to unsubscribe for push" + e);
                }
            }
        });


        //get recipientId from the intent
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        checkForRelation();
        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        String[] userIds = {currentUserId, recipientId};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereContainedIn("senderId", Arrays.asList(userIds));
        query.whereContainedIn("recipientId", Arrays.asList(userIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        WritableMessage message = new WritableMessage(messageList.get(i).get("recipientId").toString(), messageList.get(i).get("messageText").toString());
                        if (messageList.get(i).get("senderId").toString().equals(currentUserId)) {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                        } else {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                        }
                    }
                }
            }
        });


        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        //listen for a click on the send button
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Random random = new Random();

                if (recipientId.equals(currentUserId)) {
                    String[] toastMessages = new String[]{
                            getString(R.string.message_self_1),
                            getString(R.string.message_self_2),
                            getString(R.string.message_self_3),
                            getString(R.string.message_self_4),
                            getString(R.string.message_self_5),
                            getString(R.string.message_self_6),
                            getString(R.string.message_self_7),
                            getString(R.string.message_self_8),
                            getString(R.string.message_self_9),
                            getString(R.string.message_self_10)};

                    int randomMsgIndex = random.nextInt(toastMessages.length - 1);
                    Toast.makeText(MessagingActivity.this, toastMessages[randomMsgIndex], Toast.LENGTH_LONG).show();

                } else {

                    messageBody = messageBodyField.getText().toString();

                    if (messageBody.isEmpty()) {
                        Toast.makeText(MessagingActivity.this, "Please enter a message first", Toast.LENGTH_LONG).show();

                    } else {

                        messageService.sendMessage(recipientId, messageBody);
                        messageBodyField.setText("");
                    }
                }
            }
        });
    }

    public void checkForRelation() {
        ParseQuery<ParseUser> addToFriendsQuery = ParseUser.getQuery();
        addToFriendsQuery.getInBackground(recipientId, new GetCallback<ParseUser>() {
            public void done(ParseUser recipient, ParseException e) {
                if (e == null) {
                    addFriendsRelation(recipient);
                }
            }
        });
    }


    public void addFriendsRelation(final ParseUser recipient) {

        final ArrayList mRecipientsList = new ArrayList<>();

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(Constants.FRIENDS);
        query1.whereEqualTo(Constants.USER1, mCurrentUser);
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery(Constants.FRIENDS);
        query2.whereEqualTo(Constants.USER2, mCurrentUser);


        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByDescending("createdAt");
        mainQuery.include("user1");
        mainQuery.include("user2");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {


                    mRecipientsList.clear();

                    for (int i = 0; i < list.size(); i++) {
                        Log.d("MyApp", "current relation objects = " + list.size());
                        ParseUser user1 = (ParseUser) list.get(i).get(Constants.USER1);
                        String userId = user1.getObjectId();
                        ParseUser recipient;

                        if (userId.equals(mCurrentUser.getObjectId())) {
                            recipient = (ParseUser) list.get(i).get(Constants.USER2);
                        } else {
                            recipient = (ParseUser) list.get(i).get(Constants.USER1);
                        }

                        mRecipientsList.add(recipient.getObjectId());
                        Log.e("MyApp", "mCurrentRelations size = " + mRecipientsList.size());
                        //chatRecipient.put("recipient", markerUser);
                    }
                }
                chatAdditionRequest(mRecipientsList, recipient);
            }
        });
    }

    private void chatAdditionRequest(List<String> mRecipientList, ParseUser recipient) {
        Boolean exists = false;

        for (int i = 0; i < mRecipientList.size(); i++) {
            String user = mRecipientList.get(i);
            if (user.equals(recipient.getObjectId())) {
                exists = true;
                Log.d("MyApp", "Chat relation already exists, not creating again");
            }
        }
        if (!exists) {
            ParseObject relation = new ParseObject(Constants.FRIENDS);
            relation.put(Constants.USER1, mCurrentUser);
            relation.put(Constants.USER2, recipient);
            relation.saveInBackground();
        }
    }
        /*ParseQuery<ParseUser> viewUserQuery = ParseQuery.getQuery("_User");
        viewUserQuery.getInBackground(recipientId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser messagedUser, ParseException e) {
                if (e == null) {
                    addYouToTheirFriends(messagedUser);
                }
            }
        });
    }*/

    /* public static void addYouToTheirFriends(ParseObject messagedUser) {
        ParseUser clickedUser = ParseUser.getString("_User");
         ParseQuery query = ParseQuery.getQuery("_User");
         query.whereEqualTo("objectId", recipientId);
     }*/
    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        messageService.removeMessageClientListener(messageClientListener);
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = (MessageService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {

        //Notify the user if their message failed to send
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            //Log.d("Kevin" , "send error messages" + message + failureInfo);
            Toast.makeText(MessagingActivity.this, "Message failed to send.", Toast.LENGTH_LONG).show();
        }


        @Override
        public void onIncomingMessage(MessageClient client, Message message) {
            if (message.getSenderId().equals(recipientId)) {
                WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
                messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
            }
//Display an incoming message

        }

        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {
            //Display the message that was just sent
            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
            query.whereEqualTo("sinchId", message.getMessageId());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                    if (e == null) {
                        if (messageList.size() == 0) {
                            ParseObject parseMessage = new ParseObject("ParseMessage");
                            parseMessage.put("senderId", currentUserId);
                            parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                            parseMessage.put("messageText", writableMessage.getTextBody());
                            parseMessage.put("sinchId", writableMessage.getMessageId());
                            parseMessage.saveInBackground();

                            //messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);

                            try {
                                sendPushNotification();
                            } catch (JSONException error) {
                                error.printStackTrace();
                            }
                        }
                    }
                }
            });

        }

        //Do you want to notify your user when the message is delivered?
        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        }

        //Don't worry about this right now
        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        }
    }

    private void sendPushNotification() throws JSONException {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(Constants.USER_ID, recipientId);
        query.whereEqualTo(Constants.CHANNELS, Constants.MESSAGE_PUSH);

        JSONObject data = new JSONObject();
        data.put(Constants.PUSH_ALERT, "You have a message from " +
                ParseUser.getCurrentUser().get(Constants.NAME) + "!");
        data.put(Constants.PUSH_ID, ParseUser.getCurrentUser().getObjectId());
        data.put(Constants.PUSH_NAME, ParseUser.getCurrentUser().get(Constants.NAME));

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setData(data);
        push.sendInBackground();
    }


}