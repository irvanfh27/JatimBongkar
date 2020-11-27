package com.jatim.bongkar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button btnUnloading, btnHistory;
    private String mName;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

//        GlobalClass globalClass = (GlobalClass) getApplication();
//        globalClass.setData(user.get(sessionManager.ID_USER));
//        globalClass.setRole(user.get(sessionManager.ROLE));
//        globalClass.setStockpile(user.get(sessionManager.STOCKPILE));
//        stockpile = globalClass.getStockpile();


        btnUnloading = findViewById(R.id.btnUnloading);
        btnHistory = findViewById(R.id.btnHistory);
//
        HashMap<String, String> user = sessionManager.getUserDetail();
//        Log.d("A", user.get(sessionManager.LOGIN));
        mName = user.get(sessionManager.USERNAME);

        btnUnloading.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_home_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        invalidateOptionsMenu();
        menu.findItem(R.id.uname).setTitle(mName);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mlogout:
                sessionManager.logout();
        }

        return super.onOptionsItemSelected(item);
    }


}