package com.jatim.bongkar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import util.ServerAPI;


public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button _loginButton;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static String URL_LOGIN = ServerAPI.URL_API + "api/login/";
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);

        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);

        _loginButton.setOnClickListener(v -> {
            String mUsername = username.getText().toString().trim();
            String mPass = password.getText().toString().trim();
            if (!mUsername.isEmpty() && !mPass.isEmpty()) {
                login(mUsername, mPass);
            } else {
                username.setError("Please Insert Email");
                password.setError("Please Insert Password");
            }
        });
    }

    public void login(final String username, final String password) {

//        Log.d(TAG, "Login");
//
//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }

//        _loginButton.setEnabled(false);
//
//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.putExtra("name", email);
//        intent.putExtra("email", email);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        final String email = _emailText.getText().toString();
//        final String password = _passwordText.getText().toString();

//         TODO: Implement your own authentication logic here.

//        loading.setVisibility(View.VISIBLE);
//        btn_login.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
//                        JSONArray jsonArray = jsonObject.getJSONArray("login");
                        if (success) {
                            String name = jsonObject.getString("username").trim();
                            String token = jsonObject.getString("access_token").trim();
                            String id_user = jsonObject.getString("user_id").trim();

//                            GlobalClass globalClass = (GlobalClass) getApplication();
//                            globalClass.setUserId(id_user);
//                            globalClass.setUsername(name);
//                            globalClass.setAccessToken(token);

                            sessionManager.createSession(name, token, id_user);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            //loading.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(LoginActivity.this, "Username Or Password Is Wrong", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        Log.d("TEST", response);
                        e.printStackTrace();
                        //loading.setVisibility(View.GONE);
                        //btn_login.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Username Or Password Is Wrong", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR AUTH", e.toString());
                    }

                }, error -> {
            Log.d("ERROR AUTH", error.toString());
            Toast.makeText(LoginActivity.this, "Username Or Password Is Wrong", Toast.LENGTH_SHORT).show();
            //loading.setVisibility(View.GONE);
            //btn_login.setVisibility(View.VISIBLE);

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("freight_username", username);
                params.put("password", password);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                        // onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = username.getText().toString();
        String pass = password.getText().toString();

        if (email.isEmpty()) {
            username.setError("Masukan Username");
            valid = false;
        } else {
            username.setError(null);
        }

        if (pass.isEmpty()) {
            password.setError("Masukan Password");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }
}