package com.example.mathias.helloworld;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Creates a single RequestQueue for the entire app to use.
 */
public class NetworkSingleton {

    private static NetworkSingleton mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx; //ApplicationContext

    private NetworkSingleton(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    //Should be called with Application context to create singleton
    public static NetworkSingleton getInstance(Context context) {
        if (mInstance == null)
            mInstance = new NetworkSingleton(context);
        return mInstance;
    }

    //Returns the request queue for the entire application
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mCtx); //Creates RequestQueue with ApplicationContext
        return mRequestQueue;
    }

    //Adds a given request to the request queue
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    //Cancel all requests with a given tag.
    public void cancelAllRequests(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }
}
