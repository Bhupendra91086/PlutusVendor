package com.app.plutusvendorapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.adapter.ItemListAdapterwithSelector;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.Content;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.database.ItemRepository;
import com.app.plutusvendorapp.util.Global;
import com.app.plutusvendorapp.util.InputFilterMinMax;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CreateDeal extends AppCompatActivity implements ItemListAdapterwithSelector.ItemClickListener {

    ImageView priceimg, timer, camera;
    private LinearLayout titleLayout, descLayout;
    Button cancelButton, reviewButton;
    private ItemRepository itemRepository;
    private RecyclerView itemRecyclerView;
    private ItemListAdapterwithSelector itemListAdapterwithSelector;
    private List<Item> itemList;
    private SharedPreferences pref = null;
    private Item workingitem;
    private EditText dealText, dealDescription;
    private String discount, claimLimit, repeatDays, durationForDeal;
    private String jsonStartDateTime, jsonEndDateTime;
    private LinearLayout itemContainer;
    private String setKind = "auto-deal";
    private ArrayList<String> repeatEveryDate = new ArrayList<>();
    String timerForAutoDeal = null;
    Set<String> linkedHashSet = new LinkedHashSet<>();
    AlertDialog alertDialog;
    int dealIndicator = 0;
    private static int ActivityResult = 101;
    boolean MonFlag, TueFlag, WedFlag, ThrFlag, FriFlag, SatFlag, SunFlag, repeatFlag, nowFlag, goToReviewFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode

        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        itemRepository = new ItemRepository(this.getApplicationContext());
        itemRecyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        itemListAdapterwithSelector = new ItemListAdapterwithSelector(CreateDeal.this, CreateDeal.this);
        itemList = new ArrayList<>();
        linkedHashSet.add("Monday");
        linkedHashSet.add("Tuesday");
        linkedHashSet.add("Wednesday");
        linkedHashSet.add("Thursday");
        linkedHashSet.add("Friday");
        linkedHashSet.add("Saturday");
        linkedHashSet.add("Sunday");
        initView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityResult) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("result");
                //  actionEvent.setText(returnString);
                if (returnString.equalsIgnoreCase("cancel")) {

                } else {
                    CreateDeal.this.finish();
                }
            }

        }
    }

    private void initView() {


        dealText = (EditText) findViewById(R.id.deal);
        dealDescription = (EditText) findViewById(R.id.dealDesc);
        priceimg = (ImageView) findViewById(R.id.priceimg);
        timer = (ImageView) findViewById(R.id.timer);
        camera = (ImageView) findViewById(R.id.camera);
        cancelButton = (Button) findViewById(R.id.cancel);
        reviewButton = (Button) findViewById(R.id.Review);

        priceimg.setOnClickListener(priceimgListener);
        timer.setOnClickListener(timerListener);
        camera.setOnClickListener(cameraListener);

        cancelButton.setOnClickListener(cancelButtonListener);
        reviewButton.setOnClickListener(reviewButtonListener);

        titleLayout = (LinearLayout) findViewById(R.id.titleLayout);
        descLayout = (LinearLayout) findViewById(R.id.descLayout);

        dealText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0)
                {
                    titleLayout.setBackgroundResource(R.drawable.image_border);
                }
                else
                {
                    titleLayout.setBackgroundResource(R.drawable.image_border_not_selected);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dealDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0)
                {
                    descLayout.setBackgroundResource(R.drawable.image_border);
                }
                else
                {
                    descLayout.setBackgroundResource(R.drawable.image_border_not_selected);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    View.OnClickListener priceimgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            priceDialog();
        }
    };
    View.OnClickListener timerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            timeDialog();
        }
    };
    View.OnClickListener cameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dealIndicator = 0;
            Rect displayRectangle = new Rect();
            Window window = CreateDeal.this.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            final AlertDialog.Builder builder = new AlertDialog.Builder(CreateDeal.this, R.style.CustomAlertDialog);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.item_selector, viewGroup, false);
            dialogView.setMinimumWidth((int) (displayRectangle.width() * 1f));
            dialogView.setMinimumHeight((int) (displayRectangle.height() * 1f));
            builder.setView(dialogView);
            alertDialog = builder.create();
            final RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.listItemRC);
            Button cancel, select;
            cancel = (Button) dialogView.findViewById(R.id.cancel);
            select = (Button) dialogView.findViewById(R.id.Select);

            itemRepository.getAllItem().observe(CreateDeal.this, new Observer<List<Item>>() {
                @Override
                public void onChanged(@Nullable List<Item> items) {
                    try {
                        itemList.removeAll(itemList);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    for (Item item : items) {

                        itemList.add(item);
                        System.out.println("Ras >>>>>>>>>>>> " + item.getId());
                    }
                    // itemListAdapterwithSelector = new ItemListAdapterwithSelector(CreateDeal.this, CreateDeal.this);
                    itemListAdapterwithSelector.setItems(itemList);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(itemListAdapterwithSelector);


                }
            });


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();

                    try {
                        itemContainer.removeAllViews();
                    } catch (Exception e) {

                    }
                    View child = getLayoutInflater().inflate(R.layout.item_list_vertical, null);
                    Item item = itemList.get(Global.indexOfItem);
                    ImageView image = (ImageView) child.findViewById(R.id.image);

                    TextView textView = (TextView) child.findViewById(R.id.title);
                    textView.setText(item.getContent().getName());
                    TextView price = (TextView) child.findViewById(R.id.price);
                    price.setText(item.getContent().getPrice().toString());
                    TextView desc = (TextView) child.findViewById(R.id.desc);
                    desc.setText(item.getContent().getDescription());
                    ImageView edit = (ImageView) child.findViewById(R.id.edit);
                    edit.setVisibility(View.GONE);
                    itemContainer.setOnClickListener(cameraListener);
                    itemContainer.addView(child);
                    dealIndicator = 0;
                    try {
                        LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                                image);
                        loadImageFromServer.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            alertDialog.show();


        }
    };


    View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreateDeal.this.finish();
        }
    };
    View.OnClickListener reviewButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Deal deal = null;

            try {
                deal = new Deal();
                String vendorID = pref.getString(getString(R.string.vendor_id), "");
                String lat = pref.getString(getString(R.string.lat), "0");
                String llong = pref.getString(getString(R.string.llong), "0");
                //  System.out.println("Item Selected " + itemList.get(Global.indexOfItem).getContent().getName());


                deal.setKind(setKind);
                Content content = new Content();


                content.setLatitude(Double.parseDouble(lat));
                content.setLongitude(Double.parseDouble(llong));
                content.setVendorId(vendorID);

                if (setKind.equalsIgnoreCase("auto-deal")) {
                    content.setTime(timerForAutoDeal);
                    try {
                        content.setValidForHours(Integer.parseInt(durationForDeal));
                        System.out.println("durationForDeal >>>> " + durationForDeal);
                    } catch (Exception e) {
                        e.printStackTrace();
                        content.setValidForHours(0);
                    }
                    try {
                       /* repeatDays = "";
                        for (String i : linkedHashSet) {
                            repeatDays = repeatDays + i + ",";
                        }
                        repeatDays = repeatDays.substring(0, repeatDays.length() - 1);*/

                        content.setRepeatEvery(new ArrayList<>(linkedHashSet));
                        goToReviewFlag = true;
                    } catch (Exception e) {
                        goToReviewFlag = false;
                        Toast.makeText(CreateDeal.this, "Please select day/days", Toast.LENGTH_SHORT).show();
                    }
                    System.out.println("repeatDays>>>>>>>>>>>>>>>>>" + repeatDays);
                    System.out.println("repeatDays>>>>>>>>>>>>>>>>>" + repeatDays);
                    System.out.println("repeatDays>>>>>>>>>>>>>>>>>" + repeatDays);
                    System.out.println("repeatDays>>>>>>>>>>>>>>>>>" + repeatDays);


                } else {
                    System.out.println("jsonEndDateTime>>>>>>>>>>>>>>>>>");
                    System.out.println("jsonEndDateTime>>>>>>>>>>>>>>>>>");
                    System.out.println("jsonEndDateTime>>>>>>>>>>>>>>>>>" + jsonEndDateTime);
                    System.out.println("jsonStartDateTime>>>>>>>>>>>>>>>>>" + jsonStartDateTime);
                    System.out.println("durationForDeal>>>>>>>>>>>>>>>>>" + durationForDeal);


                    try {
                        String date = jsonStartDateTime;

                        SimpleDateFormat formatter6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date date6 = formatter6.parse(date);
                        System.out.println("Date >> " + date6);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date6);
                        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(durationForDeal));

                        Date date7 = calendar.getTime();
                        jsonEndDateTime = formatter6.format(date7);
                        System.out.println("Date >> " + date7);


                        System.out.println("jsonEndDateTime>>>>>>>>>>>>>>>>>" + jsonEndDateTime);
                        System.out.println("jsonEndDateTime>>>>>>>>>>>>>>>>>" + jsonEndDateTime);
                        System.out.println("jsonEndDateTime>>>>>>>>>>>>>>>>>" + jsonEndDateTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                try {
                    content.setItemId(itemList.get(Global.indexOfItem).getId());
                    goToReviewFlag = true;
                } catch (Exception e) {
                    goToReviewFlag = false;
                    Toast.makeText(CreateDeal.this, "Please select an Item", Toast.LENGTH_SHORT).show();
                }
                String textTitle = dealText.getText().toString();
                if (textTitle != null && !textTitle.equalsIgnoreCase("")) {
                    content.setTitle(dealText.getText().toString());
                } else {
                    dealText.setError("Please add Title");
                    goToReviewFlag = false;
                }

                if (!dealDescription.getText().toString().equalsIgnoreCase("")) {
                    content.setDescription(dealDescription.getText().toString());
                } else {
                    dealDescription.setError("Please add Description");
                    goToReviewFlag = false;
                }
                if (jsonStartDateTime != null && !jsonStartDateTime.equalsIgnoreCase("")) {
                    content.setValidFrom(jsonStartDateTime);
                } else {
                    Toast.makeText(CreateDeal.this, "Please provide deal start time.", Toast.LENGTH_SHORT).show();
                    goToReviewFlag = false;
                }

                if (jsonEndDateTime != null && !jsonEndDateTime.equalsIgnoreCase("")) {
                    content.setValidTo(jsonEndDateTime);
                } else {
                    Toast.makeText(CreateDeal.this, "Please provide deal Stop time.", Toast.LENGTH_SHORT).show();
                    goToReviewFlag = false;
                }
                if (claimLimit != null && !claimLimit.equalsIgnoreCase("")) {
                    content.setClaimLimit(Integer.parseInt(claimLimit));
                } else {
                    Toast.makeText(CreateDeal.this, "Please provide deal claim limit.", Toast.LENGTH_SHORT).show();
                    goToReviewFlag = false;
                }


                if (discount != null && !discount.equalsIgnoreCase("")) {
                    content.setDiscount(Integer.parseInt(discount));
                } else {
                    Toast.makeText(CreateDeal.this, "Please provide deal discount.", Toast.LENGTH_SHORT).show();
                    goToReviewFlag = false;
                }

                if (timerForAutoDeal != null && !timerForAutoDeal.equalsIgnoreCase("")) {
                    content.setTime(timerForAutoDeal);
                } else {
                    Toast.makeText(CreateDeal.this, "Please provide deal time.", Toast.LENGTH_SHORT).show();
                    goToReviewFlag = false;
                }


                deal.setContent(content);


            } catch (Exception e) {
                goToReviewFlag = false;
                e.printStackTrace();
                System.out.println(e.getMessage());


            }

            System.out.println("Flags " + goToReviewFlag);
            if (goToReviewFlag && deal != null) {
                Intent intent = new Intent(CreateDeal.this, DealPreview.class);
                startActivityForResult(intent, ActivityResult);
                MyApplication.deal = deal;
                // CreateDeal.this.finish();
            }
        }
    };


    private void priceDialog() {
        final Dialog dialog = new Dialog(CreateDeal.this);
        dialog.setContentView(R.layout.price_layout);
        dialog.setCanceledOnTouchOutside(false);
        final EditText discountEdit = (EditText) dialog.findViewById(R.id.discount);
        final EditText regular_price = (EditText) dialog.findViewById(R.id.regular_price);
        final EditText claim_limit_Edit = (EditText) dialog.findViewById(R.id.claim_limit);
        discountEdit.setFilters(new InputFilter[]{new InputFilterMinMax(0, 100)});
        claim_limit_Edit.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        claim_limit_Edit.setText(claimLimit);
        discountEdit.setText(discount);
        try {
            Item item = itemList.get(Global.indexOfItem);
            regular_price.setText(String.format("%.2f", item.getContent().getPrice()) + "");
            regular_price.setEnabled(false);

        } catch (Exception e) {
            Toast.makeText(this, "Please select menu ", Toast.LENGTH_SHORT).show();
        }
        Button save = (Button) dialog.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountEdit.getText().toString().length() > 0) {
                    discount = discountEdit.getText().toString();

                    if (claim_limit_Edit.getText().toString().length() > 0) {
                        claimLimit = claim_limit_Edit.getText().toString();
                        dialog.dismiss();
                        priceimg.setBackgroundResource(R.drawable.image_border);
                    } else {
                        claim_limit_Edit.setError("Enter Limit");
                        priceimg.setBackgroundResource(R.drawable.image_border_not_selected);
                    }


                } else {
                    discountEdit.setError("Enter discount");
                    priceimg.setBackgroundResource(R.drawable.image_border_not_selected);
                }


            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        // if button is clicked, close the custom dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void timeDialog() {

        final Calendar myCalendar = Calendar.getInstance();
        final Dialog dialog = new Dialog(CreateDeal.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.timer_layout);
        final TextView startDateTime, endDateTime;   // time
        final EditText duration = (EditText) dialog.findViewById(R.id.duration);
        duration.setFilters(new InputFilter[]{new InputFilterMinMax(0, 24)});
        if (dealIndicator != 0)
            duration.setText(durationForDeal);

        duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                durationForDeal = duration.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
      /*  duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateDeal.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                duration.setText(hourOfDay + ":" + minute);

                                //   timerForAutoDeal = time.getText().toString();
                                durationForDeal = duration.getText().toString();

                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });*/


       /* duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                durationForDeal = duration.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        Button cancel, save;
        final LinearLayout dateCotainer = (LinearLayout) dialog.findViewById(R.id.dateCotainer);
        CheckBox repeat = (CheckBox) dialog.findViewById(R.id.repeat);
        CheckBox now = (CheckBox) dialog.findViewById(R.id.now);
        startDateTime = (TextView) dialog.findViewById(R.id.startDateTime);
        endDateTime = (TextView) dialog.findViewById(R.id.endDateTime);
        // time = (TextView)dialog.findViewById(R.id.time);
        ToggleButton Mon, Tue, Wed, Thr, Fri, Sat, Sun;


        Mon = (ToggleButton) dialog.findViewById(R.id.monday);
        Tue = (ToggleButton) dialog.findViewById(R.id.tusday);
        Wed = (ToggleButton) dialog.findViewById(R.id.wedesday);
        Thr = (ToggleButton) dialog.findViewById(R.id.thurseday);
        Fri = (ToggleButton) dialog.findViewById(R.id.friday);
        Sat = (ToggleButton) dialog.findViewById(R.id.saturday);
        Sun = (ToggleButton) dialog.findViewById(R.id.sunday);


        if (dealIndicator == 0) {
            SatFlag = true;
            SunFlag = true;
            MonFlag = true;
            TueFlag = true;
            WedFlag = true;
            ThrFlag = true;
            FriFlag = true;
            repeatFlag = true;

        }

        Mon.setChecked(MonFlag);
        Tue.setChecked(TueFlag);
        Wed.setChecked(WedFlag);
        Thr.setChecked(ThrFlag);
        Fri.setChecked(FriFlag);
        Sat.setChecked(SatFlag);
        Sun.setChecked(SunFlag);
        repeat.setChecked(repeatFlag);


        now.setChecked(nowFlag);
        startDateTime.setEnabled(!nowFlag);

        if (jsonStartDateTime != null && jsonStartDateTime.length() > 0) {
            startDateTime.setText(jsonStartDateTime);
        } else {
            startDateTime.setText("Start Date \n & Time");
        }

        if (jsonEndDateTime != null && jsonEndDateTime.length() > 0) {
            endDateTime.setText(jsonEndDateTime);
        } else {
            endDateTime.setText("End On");
        }


        Mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Monday");
                    MonFlag = true;
                } else {
                    linkedHashSet.remove("Monday");
                    MonFlag = false;
                }
            }
        });
        Tue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Tuesday");
                    TueFlag = true;
                } else {
                    linkedHashSet.remove("Tuesday");
                    TueFlag = false;
                }
            }
        });
        Wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Wednesday");
                    WedFlag = true;
                } else {
                    linkedHashSet.remove("Wednesday");
                    WedFlag = false;
                }
            }
        });
        Thr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Thursday");
                    ThrFlag = true;
                } else {
                    linkedHashSet.remove("Thursday");
                    ThrFlag = false;
                }
            }
        });
        Fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Friday");
                    FriFlag = true;
                } else {
                    linkedHashSet.remove("Friday");
                    FriFlag = false;
                }
            }
        });
        Sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Saturday");
                    SatFlag = true;
                } else {
                    linkedHashSet.remove("Saturday");
                    SatFlag = false;
                }
            }
        });
        Sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkedHashSet.add("Sunday");
                    SunFlag = true;
                } else {
                    linkedHashSet.remove("Sunday");
                    SunFlag = false;
                }
            }
        });


        now.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    Date date = new Date();
                    jsonStartDateTime = simpleDateFormat.format(date);
                    int hours = date.getHours();
                    int minutes = date.getMinutes();
                    int seconds = date.getSeconds();

                    System.out.println(new DecimalFormat("00").format(hours));
                    System.out.println(new DecimalFormat("00").format(minutes));
                    System.out.println(new DecimalFormat("00").format(seconds));

                    timerForAutoDeal = new DecimalFormat("00").format(hours) + ":" +
                            new DecimalFormat("00").format(minutes) + ":" +
                            new DecimalFormat("00").format(seconds);

                    startDateTime.setText(jsonStartDateTime);
                    nowFlag = true;
                    startDateTime.setEnabled(!nowFlag);
                } else {
                    jsonStartDateTime = null;
                    startDateTime.setText("Start Date \n & Time");
                    nowFlag = false;
                    startDateTime.setEnabled(!nowFlag);
                }
            }
        });

        // final TextView time = (TextView) dialog.findViewById(R.id.time);

   /*     time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateDeal.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);

                                timerForAutoDeal = time.getText().toString();

                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });*/


        if (repeatFlag) {
            setKind = "auto-deal";

            dateCotainer.setVisibility(View.VISIBLE);
            //    time.setVisibility(View.VISIBLE);
            //  duration.setVisibility(View.VISIBLE);
            endDateTime.setVisibility(View.VISIBLE);


        } else {
            setKind = "deal";

            dateCotainer.setVisibility(View.GONE);
            //  time.setVisibility(View.GONE);
            //  duration.setVisibility(View.GONE);
            endDateTime.setVisibility(View.GONE);

        }

        //  repeat.setChecked(true);
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setKind = "auto-deal";
                    repeatFlag = true;
                    dateCotainer.setVisibility(View.VISIBLE);
                    //   time.setVisibility(View.VISIBLE);
                    //  duration.setVisibility(View.VISIBLE);
                    endDateTime.setVisibility(View.VISIBLE);


                } else {
                    setKind = "deal";
                    repeatFlag = false;
                    dateCotainer.setVisibility(View.GONE);
                    //  time.setVisibility(View.GONE);
                    //     duration.setVisibility(View.GONE);
                    endDateTime.setVisibility(View.GONE);

                }
            }
        });


        cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                timer.setBackgroundResource(R.drawable.image_border_not_selected);
            }
        });

        save = (Button) dialog.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealIndicator = dealIndicator + 1;
                timer.setBackgroundResource(R.drawable.image_border);


               /* if(repeatFlag && endDateTime.getText().toString().length() >0 )
                {
                    timer.setBackgroundResource(R.drawable.image_border);
                }
                else
                {
                    timer.setBackgroundResource(R.drawable.image_border_not_selected);
                }*/
                dialog.dismiss();

            }
        });
        // auto-deal

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                // "validFrom": "2019-09-14 14:00:00",
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                startDateTime.setText(sdf.format(myCalendar.getTime()));

                int hour = 0;
                int minute = 0;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateDeal.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                jsonStartDateTime = startDateTime.getText().toString() + " " +
                                        new DecimalFormat("00").format(hourOfDay) + ":"
                                        + new DecimalFormat("00").format(minute) + ":00";

                                timerForAutoDeal = new DecimalFormat("00").format(hourOfDay) + ":"
                                        + new DecimalFormat("00").format(minute) + ":00";

                                startDateTime.setText(startDateTime.getText().toString() + "\n" +
                                        new DecimalFormat("00").format(hourOfDay) + ":"
                                        + new DecimalFormat("00").format(minute) + ":00");


                            }
                        }, hour, minute, false);
                timePickerDialog.show();

            }

        };


        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                endDateTime.setText(sdf.format(myCalendar.getTime()));
                jsonEndDateTime = endDateTime.getText().toString() + " " + timerForAutoDeal;
                int hour = 0;
                int minute = 0;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateDeal.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                jsonEndDateTime = endDateTime.getText().toString() + " " + hourOfDay + ":" + minute + ":00";
                                endDateTime.setText(endDateTime.getText().toString() + " " + hourOfDay + ":" + minute + ":00");

                            }
                        }, hour, minute, false);
                //      timePickerDialog.show();

            }

        };


        startDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateDeal.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateDeal.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        dialog.show();

    }

    @Override
    public void onItemClick(int position) {
        System.out.println("Selected Item >>> " + position);

        Global.indexOfItem = position;

        if (alertDialog != null) {
            alertDialog.dismiss();

            try {
                itemContainer.removeAllViews();
            } catch (Exception e) {

            }
            View child = getLayoutInflater().inflate(R.layout.item_list_vertical, null);
            Item item = itemList.get(Global.indexOfItem);
            ImageView image = (ImageView) child.findViewById(R.id.image);

            TextView textView = (TextView) child.findViewById(R.id.title);
            textView.setText(item.getContent().getName());
            TextView price = (TextView) child.findViewById(R.id.price);
            price.setText(item.getContent().getPrice().toString());
            TextView desc = (TextView) child.findViewById(R.id.desc);
            desc.setText(item.getContent().getDescription());
            ImageView edit = (ImageView) child.findViewById(R.id.edit);
            edit.setVisibility(View.GONE);
            itemContainer.setOnClickListener(cameraListener);
            itemContainer.addView(child);
            try {
                LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                        image);
                loadImageFromServer.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }


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
