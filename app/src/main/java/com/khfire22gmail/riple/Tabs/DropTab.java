package com.khfire22gmail.riple.Tabs;

import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.FriendAdapter;
import com.khfire22gmail.riple.model.IdeaAdapter;
import com.khfire22gmail.riple.model.StringAdapter;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by Kevin on 9/8/2015.
 */
public class DropTab extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter dropAdapter;
    private SimpleFacebook mSimpleFacebook;
    private RecyclerView dropView;
    private FragmentActivity context;
    private ProgressBar progressBar;
    private FriendAdapter friendAdapter;
    private IdeaAdapter ideaAdapter;
    private StringAdapter stringAdapter;
    private ImageView profilePic;

    public Picture mPicture;

    // public userID = 504322442;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_drop, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.drop_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dropAdapter = new DropAdapter();
        mRecyclerView.setAdapter(dropAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
    }
}
        /*int listImages[] = new int[]{R.drawable.ic_user_default, R.drawable.ic_user_default,
                R.drawable.ic_user_default, R.drawable.ic_user_default, R.drawable.ic_user_default};

        ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i<5; i++) {
            // Create a Card
            Card card = new Card(getActivity());
            // Create a CardHeader
            CardHeader header = new CardHeader(getActivity());
            // Add Header to card
            header.setTitle("User Name");
            card.setTitle("Title");
            card.addCardHeader(header);

            CardThumbnail thumb = new CardThumbnail(getActivity());
            thumb.setDrawableResource(listImages[i]);
            card.addCardThumbnail(thumb);

            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        CardListView listView = (CardListView) view.findViewById(R.id.myList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }*/


        /*ImageView imageView = (ImageView) view.findViewById(R.id.profilePic);

        Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);*/



//        ProfilePictureView profilePicture = (ProfilePictureView) view.findViewById(R.id.profilePic);
//        profilePicture.setProfileId(userID);

        /*ProfilePictureView profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePic);

        ImageView fbImage = ((ImageView) profilePictureView.getChildAt(0));

        Bitmap bitmap = ((BitmapDrawable) fbImage.getDrawable()).getBitmap();
*/
    /*    return view;
    }

    OnPhotosListener onPhotosListener = new OnPhotosListener() {
        @Override
        public void onComplete(List<Photo> photos) {
        }*/

//        mSimpleFacebook.getPhotos(onPhotosListener);
//
//        mSimpleFacebook.getPhotos(Profile, onPhotosListener);

    /*
     * You can override other methods here:
     * onThinking(), onFail(String reason), onException(Throwable throwable)
     */


        //TODO
        /*FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_add))
                .withButtonColor(Color.WHITE)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();*/

        /*context = getActivity();
        mSimpleFacebook = SimpleFacebook.getInstance(context);

        profilePic = (ImageView) v.findViewById(R.id.phonePic);

        recyclerView1 = (RecyclerView) v.findViewById(R.id.friend_list);
        recyclerView1.setLayoutManager(new LinearLayoutManager(conter);
        stringAdapter = new StringAdapter(context, getData());
        recyclerView1.setAdapter(stringAdapter);

        recyclerView2 = (RecyclerView) v.findViewById(R.id.idea_list);
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        ideaAdapter = new IdeaAdapter(context, getData2());
        recyclerView2.setAdapter(ideaAdate();
        return v;
        return view;
        }*/





    /*OnFriendsListener onFriendsListener = new OnFriendsListener() {
        @Override
        public void onComplete(List<Profile> friends) {
            Log.i("Kevin", "Number of friends = " + friends.size());
        }
        @Override
        public void onFail(String reason) {
            Log.i("Kevin", "Fail reason = " + rea  n)le)
     *//*
    };

    private voiriendsListener);
        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {
            ArrayList<String> allContacts = new ArrayList<String>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));


                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        allContacts.add(contactName);



                        *//*String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        allContacts.add(contactNumber);*//*

                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;

            Log.i("Kevin","Contacts: " + allContacts);
    rew)

    public List<Stri   .execuia"};*//*
        ArrayList<String> data = new ArrayList<>();
        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {
            ArrayList<String> allContacts = new ArrayList<>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));


                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsC.cts.DISPLAY_NAME));
                        allContacts.add(contactName);

                        Uri u = getPhotoUri(id);
                        if (u != null) {
                            profilePic.setImageURI(u);
                        } else {
                            profilePic.setImageResource(R.drawable.ic_user_default);
                        }

                        *//*String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        allContacts.add(contactNumber);*//*

                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;
            //Log.i("Kevin","Contacts: " + allContacts);
            for (int i = 0; i < allContacts.size(); i++) {
                data.add(allContacts.get(i));

            }
        }

        return//Keep

         data;

        *//*for (int i = 0; i < name.length && i < lastName.length; i++) {
            Friend currentFriend = new Friend();
            currentFriend.name = name[i];
            currentFriend.lastName = lastName[i];
            //currentFriend.profilePic = pictures[i];

            data.add(currentFriend);
        }
        return data;*//*
    }

    public static List<Idea> getData2() {

        //Idea Hardcode
        List<Idea> data = new ArrayList<>();
        String[] idea = {"Go fishing", "Buy ice cream", "Wash car"};

        for (int i = 0; i < idea.length; i++) {
            Idea currentIdea = new Idea();
            currentIdea.idea = idea[i];

            data.add(currentIdea);
        }
        return data;
    }

    public Uri getPhotoUri(String id) {
        try {
            Cursor cur = getActivity().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(id));
        //Log.i("Kevin", "Photo URI = " + Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

    }*/

