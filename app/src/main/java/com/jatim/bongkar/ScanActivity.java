package com.jatim.bongkar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.ServerAPI;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonScan, buttonPhoto, buttonSubmit;
    private TextView platNOTextView, driverTextView, sendWeightTextView;
    private static String URL = ServerAPI.URL_API + "api/v1/security/";
    public static ImageView imageView, mPhoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String imgBase64, userId, scanId;
    Uri image_uri;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        userId = user.get(sessionManager.ID_USER);

        setContentView(R.layout.activity_scan);
        imageView = findViewById(R.id.QRscanST);
        imageView.setVisibility(View.GONE);
        mPhoto = findViewById(R.id.photo);

        // initialize object
        platNOTextView = findViewById(R.id.platNo);
        driverTextView = findViewById(R.id.driver);
        sendWeightTextView = findViewById(R.id.send_weight);
        buttonPhoto = findViewById(R.id.btnPhoto);
        buttonScan = findViewById(R.id.btn_scan);
        buttonSubmit = findViewById(R.id.btnSubmit);

        platNOTextView.setVisibility(View.GONE);
        driverTextView.setVisibility(View.GONE);
        sendWeightTextView.setVisibility(View.GONE);
        buttonPhoto.setVisibility(View.GONE);
        buttonSubmit.setVisibility(View.GONE);

        // attaching onclickListener
        buttonScan.setOnClickListener(this);

        buttonPhoto.setOnClickListener(v -> {
            dispatchTakePictureIntent();

        });
        buttonSubmit.setOnClickListener(v -> {
            submitData();
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final Uri imageUri = image_uri;
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imgBase64 = encodeImage(selectedImage);
            buttonPhoto.setVisibility(View.GONE);
            mPhoto.setVisibility(View.VISIBLE);
            mPhoto.setImageURI(image_uri);
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null && result.getContents().isEmpty()) {
                    Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        scanId = result.getContents();
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + result.getContents() + '/',
                                response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String platNO = jsonObject.getString("no_polisi");
                                        String driver = jsonObject.getString("driver");
                                        String sendWeight = jsonObject.getString("send_weight");
                                        if (!platNO.isEmpty() && !driver.isEmpty() && !sendWeight.isEmpty()) {

                                            buttonScan.setVisibility(View.GONE);
                                            platNOTextView.setVisibility(View.VISIBLE);
                                            driverTextView.setVisibility(View.VISIBLE);
                                            sendWeightTextView.setVisibility(View.VISIBLE);
                                            buttonPhoto.setVisibility(View.VISIBLE);
                                            buttonSubmit.setVisibility(View.VISIBLE);

                                            platNOTextView.setText(platNO);
                                            driverTextView.setText(driver);
                                            sendWeightTextView.setText(sendWeight);
                                        } else {
                                            Toast.makeText(ScanActivity.this, "Data Tidak Ditemukan!", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("Volley", e.toString());
                                    }

                                }, error -> {
                            Log.d("Volley", error.toString());
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(stringRequest);
                        // converting the data json
                        JSONObject object = new JSONObject(result.getContents());
                        // atur nilai ke textviews
//                    textViewNama.setText(object.getString("nama"));
//                    textViewTinggi.setText(object.getString("tinggi"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // jika format encoded tidak sesuai maka hasil
                        // ditampilkan ke toast
                        Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        // inisialisasi IntentIntegrator(scanQR)
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    private void dispatchTakePictureIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_back, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mback:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private void submitData() {
        buttonSubmit.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL + "unloading/" + scanId,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        Log.d("TEST", response);
                        buttonSubmit.setVisibility(View.VISIBLE);
                        if (success) {
                            //loading.setVisibility(View.GONE);
                            Toast.makeText(ScanActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ScanActivity.this, "Data Is Wrong", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Log.d("TEST", response);
                        e.printStackTrace();
                    }

                }, error -> {
            Log.d("TEST", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("file", imgBase64);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

}