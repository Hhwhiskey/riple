package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kevin on 9/16/2015.
 */
public class UserAdapter extends RecyclerView.Adapter <UserAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<User> data = Collections.emptyList();

    public static interface UserAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<UserAdapterDelegate> delegate;

    public UserAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(UserAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }

    public UserAdapter(Context context, List<User> data){
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userId;
        TextView firstName;
        TextView lastName;
        ImageView profilePic;

        public MyViewHolder(View itemView) {

            super(itemView);
            //userId = (TextView) itemView.findViewById(R.id.userId);
            firstName = (TextView) itemView.findViewById(R.id.firstName);
            lastName = (TextView) itemView.findViewById(R.id.lastName);
            profilePic = (ImageView) itemView.findViewById(R.id.profilePic);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            User current = data.get(position);
            //userId.setText(current.userId);
            firstName.setText(current.firstName);
            lastName.setText(current.lastName);
            profilePic.setImage(current.profilePic);
        }

        @Override
        public void onClick(View v) {

            getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}
