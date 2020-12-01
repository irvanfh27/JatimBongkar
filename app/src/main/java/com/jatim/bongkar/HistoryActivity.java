package com.jatim.bongkar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.HistoryData;
import adapter.AdapterLoading;
import util.AppController;
import util.ServerAPI;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView rHistory;
    List<HistoryData> mItems;
    private final String URL_API = ServerAPI.URL_API + "api/v1/security/unloading/history/";
    RecyclerView.LayoutManager mManager;
    RecyclerView.Adapter mAdapter;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rHistory = findViewById(R.id.recyclerviewhome);

        mItems = new ArrayList<>();
        sessionManager = new SessionManager(this);

        mManager = new LinearLayoutManager(HistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        rHistory.setLayoutManager(mManager);
        loadHistoryData(sessionManager.ID_USER);
        mAdapter = new AdapterLoading(HistoryActivity.this, mItems);
        rHistory.setAdapter(mAdapter);
    }

    private void loadHistoryData(final String userId) {
        StringRequest reqData = new StringRequest(Request.Method.GET, URL_API + sessionManager.ID_USER,
                (Response.Listener<String>) response -> {
                    Log.d("Volley", "response :" + response.toString());
                    try {
                        JSONObject data = new JSONObject(response);
                        JSONArray array = data.getJSONArray("getst");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject nllist = array.getJSONObject(i);
                            HistoryData md = new HistoryData();
                            md.setDriver(nllist.getString("driver"));
                            md.setNoPolisi(nllist.getString("no_polisi"));
                            md.setSendWeight(nllist.getString("send_weight"));
                            md.getNoUrut(nllist.getString("no_urut"));
                            mItems.add(md);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mAdapter.notifyDataSetChanged();

                },
                (Response.ErrorListener) error -> Log.d("Volley", "response :" + error.toString())) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("stockpile", Stockpile);
                //params.put("password",password);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(reqData);
    }
}