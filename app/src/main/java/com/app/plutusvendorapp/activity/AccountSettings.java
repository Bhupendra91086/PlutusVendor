package com.app.plutusvendorapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.account.AccountSetting;
import com.app.plutusvendorapp.bean.account.Content;
import com.app.plutusvendorapp.bean.dealRes.AddDealResponse;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AccountSettings extends AppCompatActivity {

    private EditText firstName,LastName,mobile;
    Button  cancel, update , Logout;
    FirebaseAuth mAuth;
    String token;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        editor = pref.edit();
        getToken();
        firstName = (EditText)findViewById(R.id.firstName);
        LastName = (EditText)findViewById(R.id.LastName);
        mobile = (EditText)findViewById(R.id.mobile);

        cancel = (Button)findViewById(R.id.cancel);
        update = (Button)findViewById(R.id.update);
        Logout = (Button)findViewById(R.id.Logout);


        cancel.setOnClickListener(cancelListener);
        update.setOnClickListener(updateListener);
        Logout.setOnClickListener(LogoutListener);


    }
    private void getToken() {

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            System.out.println(" idToken >>>>>>> " + idToken);
                            // ...
                            token = idToken;
                            if (token != null) {
                             // do some code here
                            }
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AccountSettings.this.finish();
        }
    };

    View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Call service
            AccountSetting accountSetting = new AccountSetting();
            Content content = new Content();
            accountSetting.setKind("vendor");
            accountSetting.setId(pref.getString(getString(R.string.vendor_id),""));

            content.setFirstName(firstName.getText().toString());
            content.setLastName(LastName.getText().toString());
            content.setMobile(mobile.getText().toString());
            accountSetting.setContent(content);

            UpdateWebService updateWebService =
                    new UpdateWebService("https://prod-gimme.appspot.com/api/v1/update/vendor",accountSetting);
            updateWebService.execute();

        }
    };

    View.OnClickListener LogoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getToken();
           mAuth.signOut();
            editor.putString(getString(R.string.user_email_to_reg), "");
            editor.commit();

            editor.putString(getString(R.string.reg_user), "");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            AccountSettings.this.finish();
        }
    };

    class UpdateWebService extends AsyncTask<Void,Void,Void>
    {
        String url;
        AccountSetting accountSetting;

        public  UpdateWebService(String url,AccountSetting accountSetting)
        {
            this.url = url;
            this.accountSetting = accountSetting;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            gsonCallToServer(url, accountSetting);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void gsonCallToServer(String url, final AccountSetting accountSetting) {

        System.out.println("Auth  >>>>>>>>>>>>>>>>> " + token);

        // System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> "+ new Gson().toJson(itemNew));
        // Deal Json Changes



        //

        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    new JSONObject(new Gson().toJson(accountSetting)),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> " + response.toString());
                            Gson gson = new Gson();

                          //  AddDealResponse addDealResponse = gson.fromJson(response.toString(), AddDealResponse.class);
                            System.out.println("serverResponse.getId() " + " >>>>>>> " + response.toString());
                            // addDealResponse.getData().getId();

                            Toast.makeText(AccountSettings.this, "Data Updated Sucessfully...", Toast.LENGTH_SHORT).show();
                            //accountSetting.setId(addDealResponse.getData().getId());

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


}
