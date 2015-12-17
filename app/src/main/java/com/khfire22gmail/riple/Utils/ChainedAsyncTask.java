package com.khfire22gmail.riple.utils;

import android.os.AsyncTask;

/**
 * Created by Kevin on 12/16/2015.
 */
public abstract class ChainedAsyncTask<T extends AsyncTask> extends AsyncTask {

    private T nextTask;

    public ChainedAsyncTask(T nextTask) {
        this.nextTask = nextTask;
    }



}
