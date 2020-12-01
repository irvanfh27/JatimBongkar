package util;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jatim.bongkar.GlobalClass;

public class AppController extends GlobalClass {
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController instance;
    RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public static synchronized AppController getInstance(){
        return instance;
    }

    private RequestQueue getRequestqueue(){
        if(mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestqueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestqueue().add(req);
    }

    public void cancelAllRequest(Object req){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(req);
        }
    }
}
