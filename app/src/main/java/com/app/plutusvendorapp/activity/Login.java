package com.app.plutusvendorapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.vendor.VendorDate;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.util.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText email, password;
    TextView forgetPassword;
    Button login, createAccount;
    private SharedPreferences.Editor editor = null;
    private boolean isEmailLogin;
    private SharedPreferences pref = null;
    private FirebaseAuth mAuth;
    private String checkUSER = "https://prod-gimme.appspot.com/api/v1/vendor?email=";
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        editor = pref.edit();
        initView();
    }

    private void initView() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        forgetPassword = (TextView) findViewById(R.id.forgetPassword);

        login = (Button) findViewById(R.id.login);
        createAccount = (Button) findViewById(R.id.createAccount);


        forgetPassword.setOnClickListener(forgetPasswordListener);
        login.setOnClickListener(loginListener);
        createAccount.setOnClickListener(createAccountListener);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (Validator.EmailValidator(s.toString())) {
                    isEmailLogin = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    View.OnClickListener forgetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isEmailLogin && password.getText().toString().length() > 0) {
                checkLoginEmail(email.getText().toString(), password.getText().toString());
            }

        }
    };

    View.OnClickListener createAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("action", "Register");
            startActivity(intent);
            ///  Login.this.finish();
        }
    };

    private boolean doLogin(String email, String password) {
        boolean result;

        result = true;
        return result;
    }

    private void checkLoginEmail(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                Toast.makeText(Login.this, "User Verified",
                                        Toast.LENGTH_SHORT).show();

                               /* if (MyApplication.getInstance().isProfileRegistred()) {
                                    Toast.makeText(Login.this, "GO to Location",
                                            Toast.LENGTH_SHORT).show();
                                    goToNextActivity();

                                } else {
                                    Toast.makeText(Login.this, user.getProviderId(),
                                            Toast.LENGTH_SHORT).show();
                                    goToNextActivity();
                                }*/

                             /*   Toast.makeText(Login.this, user.getUid(),
                                        Toast.LENGTH_SHORT).show();*/
                                //  goToNextActivity();
                                checkUSER = checkUSER + email;
                                getAuth(user);

                            } else {
                                Toast.makeText(Login.this, "Please Verifiy Email Address",
                                        Toast.LENGTH_SHORT).show();
                            }
                            //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void goToNextActivity() {
        editor.putString(getString(R.string.user_email_to_reg), email.getText().toString());
        editor.commit();
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("action", "Update");
        startActivity(intent);
        this.finish();

    }

    private void getAuth(FirebaseUser mUser) {

        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            System.out.println(" idToken >>>>>>> " + idToken);
                            // ...
                            token = idToken;

                            new callWebService(checkUSER).execute();

                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    class callWebService extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;

        public callWebService(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(Login.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            gsonCallToServer(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (p != null) {
                if (p.isShowing()) {
                    p.dismiss();
                }
            }
        }
    }

    private void gsonCallToServer(String URL) {
        String inputObj = null;
        System.out.println("Token >>>>>>>>>>>>>>>>>> " + token);
        System.out.println("URL  >>>>>>>>>>>>>>>>> " + URL);
        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL,
                    inputObj,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> " + response.toString());
                            Gson gson = new Gson();
                            VendorDate vendorDate = gson.fromJson(response.toString(), VendorDate.class);


                            loadUI(vendorDate);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //verificationFailed(error);

                            System.err.println("Error >>> +" + error.toString());
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", token);
                    return headers;
                }
            };
            MyApplication.getInstance().addToRequestQueue(myRequest, "tag");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUI(VendorDate vendorDate) {

        System.out.println("Total Existing user " + vendorDate.getTotal());
        if (vendorDate.getTotal() > 0) {// Load data

            clearExistingData();
            updateFCMToken();
            setSharedPreferenceData(vendorDate);


        } else {
            goToNextActivity();

        }

    }

    void clearExistingData() {
        pref.edit().clear();
    }

    void setSharedPreferenceData(VendorDate vendorDate) {


        editor.putString(getString(R.string.user_email_to_reg),vendorDate.getData().get(0).getContent().getEmail());
        editor.putString(getString(R.string.sAddress), vendorDate.getData().get(0).getContent().getAddress());
        editor.putString(getString(R.string.sMobile), vendorDate.getData().get(0).getContent().getMobile());
        editor.putString(getString(R.string.sBusiness), vendorDate.getData().get(0).getContent().getBusinessName());
        editor.putString(getString(R.string.sDescription), vendorDate.getData().get(0).getContent().getDescription());
        try {
            editor.putString(getString(R.string.sFoodType), vendorDate.getData().get(0).getContent().getItem().get(0));
        }
        catch (Exception e)
        {

        }
        editor.putString(getString(R.string.reg_user),vendorDate.getData().get(0).getId());
        editor.putString(getString(R.string.vendor_id),vendorDate.getData().get(0).getId());
        editor.putString(getString(R.string.lat),vendorDate.getData().get(0).getContent().getLatitude() );
        editor.putString(getString(R.string.llong),vendorDate.getData().get(0).getContent().getLongitude() );
        editor.commit();

        Intent intent = new Intent(Login.this,HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    void updateFCMToken() {

    }

}
