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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.loc.Res;
import com.app.plutusvendorapp.bean.reguserres.RegisterUserResponse;
import com.app.plutusvendorapp.bean.regvendor.Content;
import com.app.plutusvendorapp.bean.regvendor.RegisterVendor;
import com.app.plutusvendorapp.bean.survey.SurveyJson;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.util.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button create_account;
    private Spinner resturent_type, food_type;
    private String action = null;
    private LinearLayout header, userprofile;
    private boolean isEmailLogin;
    private FirebaseAuth mAuth;
    private EditText business_email, passeword, repassword, BusinsesName,address,number,desc,city,zip;
    private String token;
    private String responseJSON;
    private List<String> itemList, typeList;
    private SharedPreferences.Editor editor = null;
    private SharedPreferences pref = null;
    private String lat, llong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        editor = pref.edit();
        itemList = new ArrayList<String>();
        typeList = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        try {
            action = getIntent().getStringExtra("action");
        } catch (Exception e) {

        }
        header = (LinearLayout) findViewById(R.id.header);
        userprofile = (LinearLayout) findViewById(R.id.userprofile);
        BusinsesName = (EditText)findViewById(R.id.BusinsesName);
        address = (EditText)findViewById(R.id.address);
        number = (EditText)findViewById(R.id.number);
        business_email = (EditText) findViewById(R.id.business_email);
        passeword = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        desc = (EditText)findViewById(R.id.desc);
        city = (EditText)findViewById(R.id.city);
        zip = (EditText)findViewById(R.id.zip) ;


        business_email.addTextChangedListener(new TextWatcher() {
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

        create_account = (Button) findViewById(R.id.createaccount);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("Register".equalsIgnoreCase(action)) {

                    //Register User
                    if (isEmailLogin == true) {

                        regUser(business_email, passeword);
                        System.out.println(" >>>>>>> Reached here " + business_email);


                    }


                } else {
                    String FCMToken = pref.getString(getString(R.string.FCMToken), "");
                    System.out.println(" >>>>>>> FCMToken FCMToken " + FCMToken);
                    System.out.println(" >>>>>>> FCMToken FCMToken " + FCMToken);
                    System.out.println(" >>>>>>> FCMToken FCMToken " + FCMToken);



                    String complete_address = address.getText().toString() + " " + city.getText().toString() + " "+zip.getText().toString();


                    getLocation(complete_address, FCMToken);

                }


            }
        });

        if ("Register".equalsIgnoreCase(action)) {
            header.setVisibility(View.VISIBLE);
            userprofile.setVisibility(View.GONE);

        } else {
            header.setVisibility(View.GONE);
            userprofile.setVisibility(View.VISIBLE);
            create_account.setText("Update");
            getToken();
        }

        resturent_type = (Spinner) findViewById(R.id.resturent_type);
        food_type = (Spinner) findViewById(R.id.foodtype);





    }

    private void registerUser(String address , String FCMToken) {



        String email  = pref.getString(getString(R.string.user_email_to_reg),"");
        RegisterVendor registerVendor = new RegisterVendor();
        registerVendor.setKind("vendor");
        Content content = new Content();
        registerVendor.setContent(content);
        registerVendor.getContent().setFcmToken(FCMToken);
        registerVendor.getContent().setBusinessName(BusinsesName.getText().toString());
        registerVendor.getContent().setMobile(number.getText().toString());
        registerVendor.getContent().setEmail(email);
        registerVendor.getContent().setAddress(address);
        registerVendor.getContent().setType("restaurant");
        registerVendor.getContent().setDescription(desc.getText().toString());
        registerVendor.getContent().setItem(itemList);
        registerVendor.getContent().setBusinessType(typeList);
        registerVendor.getContent().setLatitude(Double.parseDouble(lat));
        registerVendor.getContent().setLongitude(Double.parseDouble(llong));

        editor.putString(getString(R.string.sAddress) , address);
        editor.putString(getString(R.string.sMobile) , number.getText().toString());
        editor.putString(getString(R.string.sBusiness) , BusinsesName.getText().toString());
        editor.putString(getString(R.string.sDescription) , desc.getText().toString());
        editor.putString(getString(R.string.sFoodType) , itemList.get(0));
        editor.commit();
        RegUser RegUser = new RegUser(registerVendor);
        RegUser.execute();
    }
    class RegUser extends AsyncTask<Void,Void,Void> {
        ProgressDialog p = null;
        String url="https://prod-gimme.appspot.com/api/v1/add/vendor";
        RegisterVendor registerUser;

        public  RegUser(RegisterVendor registerUser) {

            this.registerUser =registerUser;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MainActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            registerUserOnServer(url , registerUser);
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

    private void registerUserOnServer(String url, RegisterVendor registerVendor) {

        System.out.println("Auth  >>>>>>>>>>>>>>>>> "+ token);
        System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> "+ new Gson().toJson(registerVendor));
        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    new JSONObject(new Gson().toJson(registerVendor)),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> "+response.toString());
                            Gson gson = new Gson();
                            RegisterUserResponse serverResponse= gson.fromJson(response.toString(), RegisterUserResponse.class);

                            System.out.println("serverResponse.getId() "+ " >>>>>>> " + serverResponse.getData().getId());
                            editor.putString(getString(R.string.reg_user),serverResponse.getData().getId());
                            editor.putString(getString(R.string.vendor_id),serverResponse.getData().getId());

                            editor.commit();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                            //    nextActivity();
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
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void getLocation(final String address, final String FCMToken) {


        String tag_json_obj = "results";
        String thirdObj = null;
        String locAddress = address.replaceAll(" ", "+");
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                locAddress +
                "&key=" + getString(R.string.google_map_key);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, thirdObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());
                        System.out.println("Response >>>>>>> " + response.toString());
                        responseJSON = response.toString();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();

                        Res res = gson.fromJson(responseJSON, Res.class);
                        //result = res;
                        //  updateUser();

                        try {
                            Log.i("Text >> ", res.getResults().get(0).getGeometry().getLocation().getLat() + " and" +
                                    res.getResults().get(0).getGeometry().getLocation().getLng());
                            lat = res.getResults().get(0).getGeometry().getLocation().getLat() + "";
                            llong = res.getResults().get(0).getGeometry().getLocation().getLng() + "";
                            editor.putString(getString(R.string.lat),lat );
                            editor.putString(getString(R.string.llong),llong );
                            editor.commit();
                            registerUser(address, FCMToken);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

// Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


    private void getToken() {
        FirebaseAuth mAuth;
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
                            if(token!= null)
                            {
                                new callWebService("https://prod-gimme.appspot.com/api/v1/survey/food").execute();
                            }
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    class callWebService extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog p = null;
        String url;

        public callWebService(String url)
        {
            this.url = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MainActivity.this);
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
            if (p!=null)
            {
                if (p.isShowing())
                {
                    p.dismiss();
                }
            }
        }
    }


    private void gsonCallToServer(String  URL)
    {
        String inputObj = null;
        System.out.println("Token >>>>>>>>>>>>>>>>>> "+token);
        System.out.println("URL  >>>>>>>>>>>>>>>>> "+ URL);
        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL,
                    inputObj,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> "+response.toString());
                            Gson gson = new Gson();
                            SurveyJson surveyJson= gson.fromJson(response.toString(), SurveyJson.class);

                            loadUI(surveyJson);

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
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadUI(SurveyJson surveyJson) {

        // Spinner click listener


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, surveyJson.getType());
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, surveyJson.getItem());
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        resturent_type.setAdapter(dataAdapter);
        food_type.setAdapter(dataAdapter1);

        resturent_type.setOnItemSelectedListener(resturent_type_listner);
        food_type.setOnItemSelectedListener(food_type_listner);

    }

    private void regUser(TextView user_email, TextView password) {
        if (Validator.EmailValidator(user_email.getText().toString())
                && (password.getText().toString().equals(password.getText().toString()))
                && password.getText().toString().length() > 3) {

            mAuth.createUserWithEmailAndPassword(user_email.getText().toString(),
                    password.getText().toString())
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                    /*user.sendEmailVerification(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SingupActivity.this, "kindly Verify Email",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SingupActivity.this, task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });*/

                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Please Verifiy Email Address",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {


            // Toast.makeText(LoginActivity.this, "Wrong....",Toast.LENGTH_SHORT).show();
        }
    }

    AdapterView.OnItemSelectedListener resturent_type_listner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();

            try {
                typeList.removeAll(typeList);
            }
            catch (Exception e)
            {
            }
            typeList.add(item);

            // Showing selected spinner item
           // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener food_type_listner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();

            try {
                itemList.removeAll(itemList);
            }
            catch (Exception e)
            {
            }
            itemList.add(item);

            // Showing selected spinner item
           // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


}
