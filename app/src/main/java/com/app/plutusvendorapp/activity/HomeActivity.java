package com.app.plutusvendorapp.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.adapter.ActiveDealViewModel;
import com.app.plutusvendorapp.adapter.HorizontalListAdapter;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.bean.serverdeal.DealActive;
import com.app.plutusvendorapp.bean.serverdeal.DealActiveResponse;
import com.app.plutusvendorapp.bean.serverdeal.DealResponse;
import com.app.plutusvendorapp.bean.serveritem.ItemResponse;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.communicator.UpdateActiveIndicator;
import com.app.plutusvendorapp.database.ActiveDealDatabase;
import com.app.plutusvendorapp.database.DealDatabase;
import com.app.plutusvendorapp.database.ItemRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements UpdateActiveIndicator {

    Button CreateDeal;
    TextView manageDeals, MenuManagement, managePublicProfile, accoutsetting, mapList, dealCount;

    private List<Item> itemList;
    private RecyclerView recyclerView;
    private List<Deal> dealList = new ArrayList<>();
    private List<DealActive> activeDealList = new ArrayList<>();
    private SharedPreferences pref = null;
    String token;
    private ItemRepository itemRepository;
    private DealDatabase dealDatabase;
    private ActiveDealDatabase activeDealDatabase;
    private SharedPreferences.Editor editor = null;
    private ActiveDealViewModel dealViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        itemRepository = new ItemRepository(this.getApplicationContext());
        dealDatabase = DealDatabase.getInstance(HomeActivity.this);
        activeDealDatabase = ActiveDealDatabase.getInstance(HomeActivity.this);
        dealCount = (TextView) findViewById(R.id.dealCount);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        System.out.println("Fauth Token should be display over here ");
        getToken();
        System.out.println("Fauth Token should be display over here ");

        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        CreateDeal = (Button) findViewById(R.id.CreateDeal);

        CreateDeal.setOnClickListener(createDealListener);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();

        // loadItemFromServrer();
        initView();

        final HorizontalListAdapter horizontalListAdapter = new HorizontalListAdapter(this, this, getLifecycle());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        dealViewModel = ViewModelProviders.of(this).get(ActiveDealViewModel.class);
        // horizontalListAdapter.setDeals(dealList);
        dealViewModel.getAllPosts().observe(this, new Observer<List<com.app.plutusvendorapp.bean.serverdeal.DealActive>>() {
            @Override
            public void onChanged(@Nullable List<com.app.plutusvendorapp.bean.serverdeal.DealActive> deals) {
                activeDealList = deals;

                System.out.println("dealList Size >>>> " + activeDealList.size());
                if (activeDealList.size() > 0) {
                    horizontalListAdapter.setDeals(activeDealList);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(horizontalListAdapter);
                    horizontalListAdapter.notifyDataSetChanged();


                }
                //    PagerSnapHelper snapHelper = new PagerSnapHelper();
                //    snapHelper.attachToRecyclerView(recyclerView);
                // recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());


            }

        });
       /* for (int i = 0; i < 5; i++) {
            Deal d = new Deal();

            d.setName("Deal >> " + i);
            dealList.add(d);
        }*/
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


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
                            if (token != null) {
                                loadItemFromServrer();
                                loadDealsFromServer();
                               loadAllDealsFromServer();
                               loadAutoDealsFromServer();
                            }
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    private void loadItemFromServrer() {


        String itemURL = "https://prod-gimme.appspot.com/api/v1/item/vendor/" + pref.getString(getString(R.string.reg_user), "");
        callWebService callWebService = new callWebService(itemURL);
        callWebService.execute();
    }

    private void loadDealsFromServer() {

        try {
            activeDealDatabase.daoActiveDeal().deleteAll();
        } catch (Exception e) {

        }

        String itemURL = "https://prod-gimme.appspot.com/api/v1/deal/vendor/" + pref.getString(getString(R.string.reg_user), "");
        callDealWebService callDealWebService = new callDealWebService(itemURL);
        callDealWebService.execute();


    }


    private void loadAutoDealsFromServer() {

      /*  try
        {
            dealDatabase.daoDeal().deleteAll();
        }
        catch (Exception e)
        {

        }*/
          String itemURL = "https://prod-gimme.appspot.com/api/v1/auto-deal/vendor/" + pref.getString(getString(R.string.reg_user),"");
        //String itemURL = "https://prod-gimme.appspot.com/api/v1/deal/vendor/" + pref.getString(getString(R.string.reg_user), "") + "?history=true";
        callAutoDealWebService callAutoDealWebService = new callAutoDealWebService(itemURL);
        callAutoDealWebService.execute();

    }

    private void loadAllDealsFromServer() {

      /*  try
        {
            dealDatabase.daoDeal().deleteAll();
        }
        catch (Exception e)
        {

        }*/
        //  String itemURL = "https://prod-gimme.appspot.com/api/v1/auto-deal/vendor/" + pref.getString(getString(R.string.reg_user),"")+"?history=true";
        String itemURL = "https://prod-gimme.appspot.com/api/v1/deal/vendor/" + pref.getString(getString(R.string.reg_user), "") + "?history=true";
        callAllDealWebService callAllDealWebService = new callAllDealWebService(itemURL);
        callAllDealWebService.execute();

    }

    @Override
    public void setIndicator(int ind) {
        dealCount.setText("Current Deals: " + ind);
    }



    class callAutoDealWebService extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;

        public callAutoDealWebService(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(HomeActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            gsonAutoDealCallToServer(url);
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


    class callAllDealWebService extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;

        public callAllDealWebService(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(HomeActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            gsonALLDealCallToServer(url);
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

    class callDealWebService extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;

        public callDealWebService(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(HomeActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            gsonDealCallToServer(url);
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


    private void gsonALLDealCallToServer(String URL) {
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
                            try {
                                System.out.println("Success >>>> " + response.toString());
                                Gson gson = new Gson();
                                DealResponse dealResponse = gson.fromJson(response.toString(), DealResponse.class);

                                UpdateAllDealToDB(dealResponse);
                            } catch (Exception e) {
                                System.out.println("Error  >>>>> " + e.getMessage());
                                e.printStackTrace();
                            }
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

    private void gsonAutoDealCallToServer(String URL) {
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
                            try {
                                System.out.println("Success >>>> " + response.toString());
                                Gson gson = new Gson();
                                DealResponse dealResponse = gson.fromJson(response.toString(), DealResponse.class);

                                UpdateAllDealToDB(dealResponse);
                            } catch (Exception e) {
                                System.out.println("Error  >>>>> " + e.getMessage());
                                e.printStackTrace();
                            }
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


    private void gsonDealCallToServer(String URL) {
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
                            try {
                                System.out.println("Success active deals >>>> " + response.toString());
                                Gson gson = new Gson();
                                DealActiveResponse dealResponse = gson.fromJson(response.toString(), DealActiveResponse.class);
                                System.out.println("DealActiveResponse active deals >>>> " + dealResponse.toString());
                                UpdateDealToDB(dealResponse);
                            } catch (Exception e) {
                                System.out.println("Error  >>>>> " + e.getMessage());
                                e.printStackTrace();
                            }
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

    private void UpdateDealToDB(DealActiveResponse dealResponse) {


        for (com.app.plutusvendorapp.bean.serverdeal.DealActive deal : dealResponse.getData()) {
            System.out.println("Deal >>>>>>>>>. " + deal.getContent().getTitle());

        }
        activeDealDatabase.daoActiveDeal().saveAll(dealResponse.getData());
    }

    private void UpdateAllDealToDB(DealResponse dealResponse) {

        for (com.app.plutusvendorapp.bean.serverdeal.Deal deal : dealResponse.getData()) {
            System.out.println("Deal >>>>>>>>>. " + deal.getContent().getTitle());

        }
        dealDatabase.daoDeal().saveAll(dealResponse.getData());
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
            p = new ProgressDialog(HomeActivity.this);
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
                            try {
                                System.out.println("Success >>>> " + response.toString());
                                Gson gson = new Gson();
                                ItemResponse itemResponse = gson.fromJson(response.toString(), ItemResponse.class);

                                UpdateListToDB(itemResponse);
                            } catch (Exception e) {
                                System.out.println("Error  >>>>> " + e.getMessage());
                                e.printStackTrace();
                            }
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

    private void UpdateListToDB(ItemResponse itemResponse) {


        for (Item item : itemResponse.getData()) {
            System.out.println("Item >>>>>>>>>. " + item.getContent().getName());
            itemRepository.insertTask(item);
        }

    }

    private void UpdateListToDBActive(ItemResponse itemResponse) {


        for (Item item : itemResponse.getData()) {
            System.out.println("Item >>>>>>>>>. " + item.getContent().getName());
            itemRepository.insertTask(item);
        }

    }

    private void loadItemFromServer(final List<Item> items) {
        try {
            itemList.removeAll(itemList);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (Item item : items) {

            itemList.add(item);
            System.out.println("Ras >>>>>>>>>>>> " + item.getId());
        }


    }

    private void initView() {
        manageDeals = (TextView) findViewById(R.id.manageDeals);
        MenuManagement = (TextView) findViewById(R.id.MenuManagement);
        managePublicProfile = (TextView) findViewById(R.id.managePublicProfile);
        accoutsetting = (TextView) findViewById(R.id.accoutsetting);
        mapList = (TextView) findViewById(R.id.mapList);

        manageDeals.setOnClickListener(manageDealsListener);
        MenuManagement.setOnClickListener(manageDealsListenerListener);
        managePublicProfile.setOnClickListener(managePublicProfileListner);
        mapList.setOnClickListener(mapListListener);
        accoutsetting.setOnClickListener(accoutsettingListener);
    }

    View.OnClickListener accoutsettingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, AccountSettings.class);
            startActivity(intent);
        }
    };
    View.OnClickListener mapListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener managePublicProfileListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, PublicProfile.class);
            startActivity(intent);

        }
    };

    View.OnClickListener manageDealsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, DealManagement.class);
            startActivity(intent);


        }
    };
    View.OnClickListener manageDealsListenerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, MenuManagement.class);
            startActivity(intent);

        }
    };


    View.OnClickListener createDealListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            Intent intent = new Intent(HomeActivity.this, CreateDeal.class);
            startActivity(intent);


        }
    };
}
