package com.app.plutusvendorapp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.adapter.HorizontalListAdapter;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.database.ItemRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DealLandingInfo extends AppCompatActivity  implements OnMapReadyCallback{
    private SharedPreferences pref = null;
    String lat;
    String llong;
    String businessName;
    Deal deal;
    Button Update;
    ImageView businessImage;
    ItemRepository itemRepository;
    private static int ActivityResult = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_landing_info);
        deal = MyApplication.deal;
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        itemRepository = new ItemRepository(this);
        lat = pref.getString(getString(R.string.lat), "0");
        llong = pref.getString(getString(R.string.llong), "0");
        System.out.println("Lat >>>>> "+lat);
        System.out.println("llong >>>>> "+llong);
        businessImage= (ImageView) findViewById(R.id.businessImage);

        Update = (Button)findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.deal = deal;
                Intent intent = new Intent(DealLandingInfo.this,DealPreview.class);
                startActivityForResult(intent, ActivityResult);

            }
        });
        initLoadImage();
        initTitle();
        initDealPrice();
        initDealDiscount();
        initDealTime();
        initDealLimit();
        initDesc();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityResult) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("result");
                //  actionEvent.setText(returnString);
                if(returnString.equalsIgnoreCase("cancel"))
                {

                }
                else {
                    DealLandingInfo.this.finish();
                }
            }

        }
    }

    private void initLoadImage()
    {
        try
        {
            itemRepository.getTask(deal.getContent().getItemId())
                    .observe(this, new Observer<Item>() {
                        @Override
                        public void onChanged(@Nullable Item item) {

                            // Set image from the server
                         LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                                 businessImage);
                            loadImageFromServer.execute();


                        }
                    });
        }
        catch (Exception e)
        {

        }

    }

    private void initDealPrice()
    {
        final TextView BusinsesTypeText,editBusinesstype;
        final EditText BusinsesType;
        final Button editPriceCancel,editPriceUpdate;

        BusinsesTypeText = (TextView)findViewById(R.id.BusinsesTypeText);

            try
            {
                itemRepository.getTask(deal.getContent().getItemId())
                        .observe(this, new Observer<Item>() {
                            @Override
                            public void onChanged(@Nullable Item item) {
                                BusinsesTypeText.setText("$"+String.format("%.2f",item.getContent().getPrice()));
                            }
                        });
            }
            catch (Exception e)
            {

            }






    }
    private void initDealDiscount()
    {
        final TextView DiscountText,editDiscount;
        final EditText DiscountEdit;
        final Button dealDiscountCancel,dealDiscountUpdate;

        DiscountText = (TextView)findViewById(R.id.DiscountText);
        editDiscount = (TextView)findViewById(R.id.editDiscount);
        DiscountEdit = (EditText)findViewById(R.id.DiscountEdit);
        dealDiscountCancel = (Button)findViewById(R.id.dealDiscountCancel);
        dealDiscountUpdate = (Button)findViewById(R.id.dealDiscountUpdate);
        DiscountText.setText(deal.getContent().getDiscount() + "%");
        editDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DiscountEdit.setText("");
                DiscountText.setVisibility(View.GONE);
                editDiscount.setVisibility(View.GONE);
                DiscountEdit.setVisibility(View.VISIBLE);
                dealDiscountCancel.setVisibility(View.VISIBLE);
                dealDiscountUpdate.setVisibility(View.VISIBLE);
            }
        });

        dealDiscountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscountText.setVisibility(View.VISIBLE);
                editDiscount.setVisibility(View.VISIBLE);
                DiscountEdit.setVisibility(View.GONE);
                dealDiscountCancel.setVisibility(View.GONE);
                dealDiscountUpdate.setVisibility(View.GONE);
            }
        });
        dealDiscountUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscountText.setVisibility(View.VISIBLE);
                editDiscount.setVisibility(View.VISIBLE);
                DiscountEdit.setVisibility(View.GONE);
                dealDiscountCancel.setVisibility(View.GONE);
                dealDiscountUpdate.setVisibility(View.GONE);
                deal.getContent().setDiscount(Integer.parseInt(DiscountEdit.getText().toString()));
                DiscountText.setText(DiscountEdit.getText().toString()+ "%");
            }
        });



    }
    private void initDealTime()
    {

        final TextView timeText,editTime;
        final EditText timeEdit;
        final Button dealtimeCancel,dealtimeUpdate;
        final Calendar myCalendar = Calendar.getInstance();

        timeText = (TextView)findViewById(R.id.timeText);
        editTime = (TextView)findViewById(R.id.editTime);
        timeEdit = (EditText)findViewById(R.id.timeEdit);
        dealtimeCancel = (Button)findViewById(R.id.dealtimeCancel);
        dealtimeUpdate = (Button)findViewById(R.id.dealtimeUpdate);
        timeText.setText(deal.getContent().getValidTo() );
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeEdit.setText("");
                timeText.setVisibility(View.GONE);
                editTime.setVisibility(View.GONE);
                timeEdit.setVisibility(View.VISIBLE);
                dealtimeCancel.setVisibility(View.VISIBLE);
                dealtimeUpdate.setVisibility(View.VISIBLE);
            }
        });


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

               final String jsonStartDateTime = null;

                // TODO Auto-generated method stub
                // "validFrom": "2019-09-14 14:00:00",
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                timeEdit.setText(sdf.format(myCalendar.getTime()));

                int hour = 0;
                int minute = 0;
                TimePickerDialog timePickerDialog = new TimePickerDialog(DealLandingInfo.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                              /*  jsonStartDateTime = timeEdit.getText().toString() + " " +
                                        new DecimalFormat("00").format(hourOfDay) + ":"
                                        + new DecimalFormat("00").format(minute) + ":00";
*/


                                timeEdit.setText(timeEdit.getText().toString() + "\n"+
                                        new DecimalFormat("00").format(hourOfDay) + ":"
                                        + new DecimalFormat("00").format(minute) + ":00");


                            }
                        }, hour, minute, false);
                timePickerDialog.show();

            }

        };
        editTime .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timeText.setVisibility(View.GONE);
            editTime.setVisibility(View.GONE);
            timeEdit.setVisibility(View.VISIBLE);
            dealtimeCancel.setVisibility(View.VISIBLE);
            dealtimeUpdate.setVisibility(View.VISIBLE);
            new DatePickerDialog(DealLandingInfo.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    });

        dealtimeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeText.setVisibility(View.VISIBLE);
                editTime.setVisibility(View.VISIBLE);
                timeEdit.setVisibility(View.GONE);
                dealtimeCancel.setVisibility(View.GONE);
                dealtimeUpdate.setVisibility(View.GONE);
            }
        });
        dealtimeUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeText.setVisibility(View.VISIBLE);
                editTime.setVisibility(View.VISIBLE);
                timeEdit.setVisibility(View.GONE);
                dealtimeCancel.setVisibility(View.GONE);
                dealtimeUpdate.setVisibility(View.GONE);
                deal.getContent().setValidTo(timeEdit.getText().toString());
                timeText.setText(timeEdit.getText().toString());
            }
        });

    }

    private void initDealLimit()
    {
        final TextView dealLimit,editButtondealLimit;
        final EditText dealLimitEdit;
        final Button dealLimitCancel,dealLimitUpdate;

        dealLimit = (TextView)findViewById(R.id.dealLimit);
        editButtondealLimit = (TextView)findViewById(R.id.editButtondealLimit);
        dealLimitEdit = (EditText)findViewById(R.id.dealLimitEdit);
        dealLimitCancel = (Button)findViewById(R.id.dealLimitCancel);
        dealLimitUpdate = (Button)findViewById(R.id.dealLimitUpdate);

        try {
            dealLimit.setText(deal.getContent().getClaimLimit() +" Offers Left");
            System.out.println("Claim Limit of the deal >>>> " +deal.getContent().getClaimLimit());
        }
        catch (Exception e)
        {
            System.out.println("Claim Limit of the deal >>>> " +e.getMessage());
        }
        editButtondealLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dealLimitEdit.setText("");
                editButtondealLimit.setVisibility(View.GONE);
                dealLimit.setVisibility(View.GONE);
                dealLimitEdit.setVisibility(View.VISIBLE);
                dealLimitCancel.setVisibility(View.VISIBLE);
                dealLimitUpdate.setVisibility(View.VISIBLE);
            }
        });
        dealLimitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtondealLimit.setVisibility(View.VISIBLE);
                dealLimit.setVisibility(View.VISIBLE);
                dealLimitEdit.setVisibility(View.GONE);
                dealLimitCancel.setVisibility(View.GONE);
                dealLimitUpdate.setVisibility(View.GONE);
            }
        });
        dealLimitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtondealLimit.setVisibility(View.VISIBLE);
                dealLimit.setVisibility(View.VISIBLE);
                dealLimitEdit.setVisibility(View.GONE);
                dealLimitCancel.setVisibility(View.GONE);
                dealLimitUpdate.setVisibility(View.GONE);
                dealLimit.setText(dealLimitEdit.getText().toString());
                deal.getContent().setClaimLimit(Integer.parseInt(dealLimitEdit.getText().toString()));
                dealLimit.setText(deal.getContent().getClaimLimit() +" Offers Left");
            }
        });





    }

    private void initTitle()
    {
        final TextView DealTitle,editDealTitle;
        final EditText DealTitleEdit;
        final Button editDealCancel,editDealUpdate;

        DealTitle = (TextView)findViewById(R.id.DealTitle);
        editDealTitle = (TextView)findViewById(R.id.editDealTitle);
        DealTitleEdit = (EditText)findViewById(R.id.DealTitleEdit);
        editDealCancel = (Button)findViewById(R.id.editDealCancel);
        editDealUpdate = (Button)findViewById(R.id.editDealUpdate);
        DealTitle.setText(deal.getContent().getTitle());
        editDealTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DealTitleEdit.setText("");
                DealTitle.setVisibility(View.GONE);
                editDealTitle.setVisibility(View.GONE);
                DealTitleEdit.setVisibility(View.VISIBLE);
                editDealCancel.setVisibility(View.VISIBLE);
                editDealUpdate.setVisibility(View.VISIBLE);
            }
        });

        editDealCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealTitle.setVisibility(View.VISIBLE);
                editDealTitle.setVisibility(View.VISIBLE);
                DealTitleEdit.setVisibility(View.GONE);
                editDealCancel.setVisibility(View.GONE);
                editDealUpdate.setVisibility(View.GONE);
            }
        });
        editDealUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealTitle.setVisibility(View.VISIBLE);
                editDealTitle.setVisibility(View.VISIBLE);
                DealTitleEdit.setVisibility(View.GONE);
                editDealCancel.setVisibility(View.GONE);
                editDealUpdate.setVisibility(View.GONE);
                deal.getContent().setTitle(DealTitleEdit.getText().toString());
                DealTitle.setText(DealTitleEdit.getText().toString());
            }
        });




    }

    private void initDesc()
    {
        final TextView dealDesc,editDealDesc;
        final EditText dealDescEdit;
        final Button editDealDescCancel , editDealDescUpdate;

        dealDesc = (TextView)findViewById(R.id.dealDesc);
        editDealDesc = (TextView)findViewById(R.id.editDealDesc);
        dealDescEdit = (EditText)findViewById(R.id.dealDescEdit);
        editDealDescCancel = (Button)findViewById(R.id.editDealDescCancel);
        editDealDescUpdate = (Button)findViewById(R.id.editDealDescUpdate);

        dealDesc.setText(deal.getContent().getDescription());


        editDealDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editDealDesc.setVisibility(View.GONE);
                dealDesc.setVisibility(View.GONE);
                dealDescEdit.setVisibility(View.VISIBLE);
                editDealDescCancel.setVisibility(View.VISIBLE);
                editDealDescUpdate.setVisibility(View.VISIBLE);
            }
        });
        editDealDescCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDealDesc.setVisibility(View.VISIBLE);
                dealDesc.setVisibility(View.VISIBLE);
                dealDescEdit.setVisibility(View.GONE);
                editDealDescCancel.setVisibility(View.GONE);
                editDealDescUpdate.setVisibility(View.GONE);

            }
        });
        editDealDescUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDealDesc.setVisibility(View.VISIBLE);
                dealDesc.setVisibility(View.VISIBLE);
                dealDescEdit.setVisibility(View.GONE);
                editDealDescCancel.setVisibility(View.GONE);
                editDealDescUpdate.setVisibility(View.GONE);
                dealDesc.setText(dealDescEdit.getText().toString());
                deal.getContent().setDescription(dealDescEdit.getText().toString());

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(llong));
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title(businessName)

        );

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


 private  class LoadImageFromServer extends AsyncTask<Void, Void, Void> {

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

         //  VendorId = "467440b5-960b-4aa1-a19d-e09aade75f52";
         //  FirebaseStorage firebaseStorage =    FirebaseStorage.getInstance("gs://evd-gimme.appspot.com");

         // StorageReference gsReference = firebaseStorage.getReference("user/map-marker-2-xxl.png");

         try {
             FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


             // StorageReference gsReference = firebaseStorage.getReference("user/map-marker-2-xxl.png");


             StorageReference gsReference = firebaseStorage.getReferenceFromUrl(imageURL);


             //  .getReferenceFromUrl("gs://evd-gimme.appspot.com/user/467440b5-960b-4aa1-a19d-e09aade75f52.jpg");
// gs://evd-gimme.appspot.com/user/467440b5-960b-4aa1-a19d-e09aade75f52.jpg
             System.out.println(" Image  >>> " + gsReference.getRoot().getBucket());



             /* Glide.with(MainActivity.this *//* context *//*)
                    .load(gsReference)
                    .into(imgProfile);*/

             final long ONE_MEGABYTE = 1024 * 1024 * 5;
             gsReference
                     .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                 @Override
                 public void onSuccess(byte[] bytes) {
                     // Data for "images/island.jpg" is returns, use this as needed
                     Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                     imageView.setImageBitmap(bitmap);

                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception exception) {
                     // Handle any errors
                 }
             });

         } catch (Exception e) {

         }
         return null;
     }
 }


}

