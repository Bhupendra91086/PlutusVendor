package com.app.plutusvendorapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.dealRes.AddDealResponse;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.database.DealDatabase;
import com.app.plutusvendorapp.database.ItemRepository;
import com.app.plutusvendorapp.util.Config;
import com.app.plutusvendorapp.util.CustomTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DealPreview extends AppCompatActivity {

    private Button cancelDeal, postDeal, saveLater;
    private Deal deal;
    private String token, postURL;
    private ImageView itemImage;
    private DealDatabase dealDatabase;
    private ItemRepository itemRepository;
    private CustomTextView time, CompanyName, cuisin_type, dealLeft, Price, OFF, lowered_price, deals_claimed, deals_left;
    float dealPrice, dealDiscount;
    private SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_preview);
        deal = MyApplication.deal;
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        dealDatabase = DealDatabase.getInstance(DealPreview.this);
        itemRepository = new ItemRepository(this.getApplicationContext());
        itemImage = (ImageView) findViewById(R.id.itemImage);
        cuisin_type = (CustomTextView) findViewById(R.id.cuisin_type);
        deals_claimed = (CustomTextView) findViewById(R.id.deals_claimed);
        Price = (CustomTextView) findViewById(R.id.original_price);
        OFF = (CustomTextView) findViewById(R.id.percentage_off);
        lowered_price = (CustomTextView) findViewById(R.id.lowered_price);
        deals_left = (CustomTextView) findViewById(R.id.deals_left);
        try {
            itemRepository.getTask(deal.getContent().getItemId()).observe(DealPreview.this, new Observer<Item>() {
                @Override
                public void onChanged(@Nullable Item item) {
                    LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0), itemImage);
                    loadImageFromServer.execute();
                    cuisin_type.setText(deal.getContent().getDescription());
                    Price.setText("$" + String.format("%.2f",item.getContent().getPrice()));
                    Price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    deals_claimed.setText(deal.getContent().getClaimLimit() + " Deals Remaining!");
                    // deals_left.setText(item.getContent().getClaimLimit() + " Deals Remaining !");
                    try {
                        dealPrice = Float.parseFloat(item.getContent().getPrice().toString());
                        OFF.setText(deal.getContent().getDiscount() + "% discount");
                        dealDiscount = (deal.getContent().getDiscount());
                        float tempResult = dealPrice - ((dealPrice * dealDiscount) / 100);
                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(2);
                        System.out.println(">>>>dealDiscount>>>>>>>>>>>>>> " + dealDiscount);
                        System.out.println(">>>>>>>>>dealPrice>>>>>>>>> " + dealPrice);
                        System.out.println(">>>>>>>>>>>>>>>>>> " + df.format(tempResult));
                        System.out.println(">>>>>>>>>>>>>>>>>> " + df.format(tempResult));

                        lowered_price.setText("$" + String.format("%.2f",tempResult));
                    } catch (Exception e) {
                        dealPrice = 0.0f;
                    }
                }
            });

        } catch (Exception e) {

        }

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        getToken();

        cancelDeal = (Button) findViewById(R.id.cancelDeal);
        postDeal = (Button) findViewById(R.id.postDeal);
        saveLater = (Button) findViewById(R.id.saveLater);

        cancelDeal.setOnClickListener(cancelDealListener);
        postDeal.setOnClickListener(postDealListener);
        saveLater.setOnClickListener(saveLaterListener);
        time = (CustomTextView) findViewById(R.id.timer);
        CompanyName = (CustomTextView) findViewById(R.id.company_name);
        String businessName = pref.getString(getString(R.string.sBusiness), "");


        CompanyName.setText(deal.getContent().getTitle());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Date d1 = null;
        Date d2 = null;

        try {

            if (deal.getKind().equalsIgnoreCase("auto-deal")) {
                d1 = format.parse(deal.getContent().getValidFrom());
                d2 = format.parse(deal.getContent().getValidTo());
                int timeToAdd  = deal.getContent().getValidForHours();
                String date = deal.getContent().getValidFrom();

                SimpleDateFormat formatter6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date6 = formatter6.parse(date);
                System.out.println("Date >> " + date6);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date6);
                calendar.add(Calendar.HOUR_OF_DAY, timeToAdd);

                Date date7 = calendar.getTime();
                long diff = date7.getTime() - d1.getTime();

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);

                System.out.print(diffDays + " days, ");
                System.out.print(diffHours + " hours, ");
                System.out.print(diffMinutes + " minutes, ");
                System.out.print(diffSeconds + " seconds.");
                if (diffDays > 1) {
                    time.setText("24:00:00");
                } else {
                    time.setText(new DecimalFormat("00").format(diffHours )+ ":"
                            + new DecimalFormat("00").format(diffMinutes) + ":"
                            + new DecimalFormat("00").format(diffSeconds));
                }



            } else {
                d1 = format.parse(deal.getContent().getValidFrom());
                d2 = format.parse(deal.getContent().getValidTo());


                //in milliseconds
                long diff = d2.getTime() - d1.getTime();

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);

                System.out.print(diffDays + " days, ");
                System.out.print(diffHours + " hours, ");
                System.out.print(diffMinutes + " minutes, ");
                System.out.print(diffSeconds + " seconds.");
                if (diffDays > 1) {
                    time.setText("24:00:00");
                } else {
                    time.setText(new DecimalFormat("00").format(diffHours )+ ":"
                            + new DecimalFormat("00").format(diffMinutes) + ":"
                            + new DecimalFormat("00").format(diffSeconds));
                }
            }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR >>>>>>>>>>>>> " + e.getMessage());
                time.setText("24:00:00");
            }

      //}
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
                            }
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    View.OnClickListener cancelDealListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","cancel");
            setResult(Activity.RESULT_OK,returnIntent);
            DealPreview.this.finish();
        }
    };

    View.OnClickListener postDealListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (token != null) {

                Gson g = new Gson();
                String jsonString = g.toJson(deal);

                System.out.println("Server json = " + jsonString);

                if (deal.getId() != null) {
                    // Delete deal ....                    https://prod-gimme.appspot.com/api/v1/deal
                    DeleteDealWebService deleteDealWebService =
                            new DeleteDealWebService( deal,"https://prod-gimme.appspot.com/api/v1/deal/");
                    deleteDealWebService.execute();
                }


                if (deal.getKind().equalsIgnoreCase("auto-deal")) {
                    postURL = Config.POST_AUTO_DEAL;
                    callWebService callWebService = new callWebService(postURL, deal, true);
                    callWebService.execute();
                    System.out.println("postURL Auto-deal " + postURL);
                    System.out.println("deal Auto-deal " + deal);


                } else {
                    postURL = Config.POST_DEAL;
                    deal.getContent().setTime(null);
                    callWebService callWebService = new callWebService(postURL, deal, true);
                    callWebService.execute();
                }


            }

        }
    };
    View.OnClickListener saveLaterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dealDatabase.daoDeal().save(deal);
        }
    };

    private String dateCalc(String date, String durationForDeal) {
        String enddate = date;
        try {
            System.out.println("Date >> " + date +"\n and Duration "+ durationForDeal);
            SimpleDateFormat formatter6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date6 = formatter6.parse(date);
            System.out.println("Date >> " + date6);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date6);
            calendar.add(Calendar.HOUR, Integer.parseInt(durationForDeal));


            Date date7 = calendar.getTime();
            System.out.println("Date >> " + date7);

            enddate = formatter6.format(date7);
            System.out.println("enddate >> " + enddate);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return enddate;
    }

    class DeleteDealWebService extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;
        Deal deal;

        public DeleteDealWebService(Deal deal, String url) {
            this.deal = deal;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(DealPreview.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DeactivateDealFromServer(deal.getId(), url);

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

    private void DeactivateDealFromServer(String id, String url) {

        try {

            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.DELETE,
                    url+id,
                    (String) null,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> " + response.toString());
                            Gson gson = new Gson();

                         //   AddDealResponse addDealResponse = gson.fromJson(response.toString(), AddDealResponse.class);
                            System.out.println("Delete Id() " + " >>>>>>> " + response.toString());
                            System.out.println("Delete Id() " + " >>>>>>> " + response.toString());
                            System.out.println("Delete Id() " + " >>>>>>> " + response.toString());
                            // addDealResponse.getData().getId();



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

        }

    }

    class callWebService extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;
        Deal deal;
        boolean isToAdd;

        public callWebService(String url, Deal deal, boolean b) {
            this.url = url;
            this.deal = deal;
            isToAdd = b;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(DealPreview.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            gsonCallToServer(url, deal, isToAdd);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (p != null) {
                if (p.isShowing()) {
                    p.dismiss();
                }

                if (deal.getKind().equalsIgnoreCase("auto-deal")) {
                    //  DealPreview.this.finish();
                    Deal dealTest = deal;
                    postURL = Config.POST_DEAL;
                    dealTest.setKind("deal");

                    System.out.println(dealTest.getContent().getValidFrom()+" AND TIME TO ADD "+ dealTest.getContent().getValidForHours());
                    dealTest.getContent().setValidTo(dateCalc(dealTest.getContent().getValidFrom(), dealTest.getContent().getValidForHours().toString()));
                    dealTest.getContent().setRepeatEvery(null);
                    dealTest.getContent().setTime(null);
                    dealTest.getContent().setValidForHours(null);
                    callWebServiceDeal callWebService_deal = new callWebServiceDeal(postURL, dealTest, true);


                    System.out.println("postURL deal " + postURL);
                    System.out.println("deal deal " + deal);
                    callWebService_deal.execute();
                }
                else
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","postDeal");
                    setResult(Activity.RESULT_OK,returnIntent);
                    DealPreview.this.finish();
                }
            }
        }
    }

    class callWebServiceDeal extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url;
        Deal deal;
        boolean isToAdd;

        public callWebServiceDeal(String url, Deal deal, boolean b) {
            this.url = url;
            this.deal = deal;
            isToAdd = b;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(DealPreview.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            gsonCallToServer(url, deal, isToAdd);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (p != null) {
                if (p.isShowing()) {
                    p.dismiss();
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","postDeal");
                setResult(Activity.RESULT_OK,returnIntent);
                DealPreview.this.finish();
            }
        }
    }


    private void gsonCallToServer(String url, final Deal deal, final boolean isToAdd) {

        System.out.println("Auth  >>>>>>>>>>>>>>>>> " + token);

        // System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> "+ new Gson().toJson(itemNew));
        // Deal Json Changes

        if (deal.getKind().equalsIgnoreCase("auto-deal")) {

        } else {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new Gson().toJson(deal)).getAsJsonObject();


            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.remove("content.repeatEvery");
            String jsonReq = jsonObject.toString();
            System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> " + jsonReq);
        }


        //

        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    new JSONObject(new Gson().toJson(deal)),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> " + response.toString());
                            Gson gson = new Gson();

                            AddDealResponse addDealResponse = gson.fromJson(response.toString(), AddDealResponse.class);
                            System.out.println("serverResponse.getId() " + " >>>>>>> " + addDealResponse.getData().getId());
                            // addDealResponse.getData().getId();

                            deal.setId(addDealResponse.getData().getId());
                            if (isToAdd) {
                                dealDatabase.daoDeal().save(deal);
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


    private class LoadImageFromServer extends AsyncTask<Void, Void, Void> {

        String imageURL;
        ImageView imageView;

        public LoadImageFromServer(String imageURL, ImageView imageView) {
            this.imageURL = imageURL;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference gsReference = firebaseStorage.getReferenceFromUrl(imageURL);
                System.out.println(" Image  >>> " + gsReference.getRoot().getBucket());

                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                gsReference
                        .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });

            } catch (Exception e) {

            }
            return null;
        }
    }


}
