package com.heads.android.budgetapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;
/**
 * Created by Jill on 12/28/2018.
 */

public class BudgetLoader extends AsyncTaskLoader<String>{

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link BudgetLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public BudgetLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    /**
     * This is on a background thread.
     */
    @Override
    public String loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request
        return "success";
    }
}
