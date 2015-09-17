package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kevin on 9/10/2015.
 */
public class FriendAdapter extends RecyclerView.Adapter <FriendAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<Friend> data = Collections.emptyList();

    public static interface FriendAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<FriendAdapterDelegate> delegate;

    public FriendAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(FriendAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }



    public FriendAdapter(Context context, List<Friend> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_drop, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.update(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView name;
        TextView lastName;
        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            //number = (TextView) itemView.findViewById(R.id.itemListTextView);
            name = (TextView) itemView.findViewById(R.id.dropDescription);
            //lastName = (TextView) itemView.findViewById(R.id.nameView2);
            //profilePic = (ImageView) itemView.findViewById(R.id.profilePic);
            itemView.setOnClickListener(this);
        }


        public void update(int position){
            Friend current = data.get(position);
            //number.setText(current.number);
            name.setText(current.name);
            //lastName.setText(current.lastName);
            //profilePic.setImage(current.profilePic);
        }

        @Override
        public void onClick(View v) {
            //getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}
