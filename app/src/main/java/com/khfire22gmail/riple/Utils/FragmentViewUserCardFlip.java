package com.khfire22gmail.riple.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khfire22gmail.riple.R;

/**
 * Created by Kevin on 11/26/2015.
 */
public class FragmentViewUserCardFlip extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    Handler mHandler = new Handler();
    private boolean mShowingBack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_user_card_flip);

        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.view_user_fragment_container, new CardFrontFragment())
                    .commit();
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {
        public CardFrontFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_view_user_picture, container, false);
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
        public CardBackFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_view_user_text, container, false);


        }
    }

    @Override
    public void onBackStackChanged() {

    }

    private void flipCard() {
        // TODO: 11/26/2015 Add OCL to heroImage that flips the ImageView
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }



        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.view_user_fragment_container, new CardBackFragment())

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();
        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }


}
