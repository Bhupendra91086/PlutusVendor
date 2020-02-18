package com.app.plutusvendorapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.account.AccountSetting;
import com.app.plutusvendorapp.bean.account.Content;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.loc.Res;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.util.Config;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PublicProfile extends AppCompatActivity implements OnMapReadyCallback {

    private SharedPreferences pref = null;
    String lat;
    String llong;
    String businessName;
    String mobile;
    String address, foodtype, description;
    FirebaseAuth mAuth;
    String token;
    TextView   BusinsesTypeText;
    private SharedPreferences.Editor editor= null;
    Button cancel, Update;
    private String responseJSON;
    private ImageView camera;
    String imageURL = null;
    ArrayList<String> imageList = new ArrayList<>();
    FirebaseUser firebaseUser;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 2;
    StorageReference storageRef;
    private String imagePathLocal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        editor = pref.edit();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getAuth(firebaseUser);
        setContentView(R.layout.activity_public_profile);
        getToken();
        camera  = (ImageView)findViewById(R.id.businessImage);
        camera.setOnClickListener(addImageListener);
        lat = pref.getString(getString(R.string.lat), "0");
        llong = pref.getString(getString(R.string.llong), "0");
        businessName = pref.getString(getString(R.string.sBusiness), "Please Edit Business Name");
        mobile = pref.getString(getString(R.string.sMobile), "Please add a mobile number");
        address = pref.getString(getString(R.string.sAddress), "Please add address");
        foodtype = pref.getString(getString(R.string.sFoodType), "");
        ;
        description = pref.getString(getString(R.string.sDescription), "Please add Business details");
        ;
        imageURL = pref.getString(getString(R.string.vendorImage) ,"");
        if(imageURL != null  && imageURL.length()>0)
        {
            LoadImageFromServer loadImageFromServer = new LoadImageFromServer(imageURL,camera);
            loadImageFromServer.execute();
        }
        else
        {
            System.out.println("Images >>>> Not Found..."+imageURL);
        }

        initView();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

    private void getAuth(FirebaseUser mUser) {

        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            System.out.println(" idToken >>>>>>> " + idToken);
                            // ...
                            token = idToken;
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    private View.OnClickListener addImageListener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            if (PublicProfile.this.checkSelfPermission(PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED
                    && PublicProfile.this.checkSelfPermission(PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED
                    && PublicProfile.this.checkSelfPermission(PERMISSIONS_STORAGE[2]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_STORAGE, MY_CAMERA_PERMISSION_CODE);
            } else {
                selectImage();

            }
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PublicProfile.this, "camera permission granted", Toast.LENGTH_LONG).show();
               /* Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                selectImage();
            } else {
                Toast.makeText(PublicProfile.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PublicProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("Response code", requestCode + " and " + resultCode);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            /*Log.w("RESULT_OK" , "Button Clicked");
            //  File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
            File f = new File(android.os.Environment.getExternalStorageDirectory().getPath()+"//gimme", "temp.jpg");
            Uri uri = Uri.fromFile(f);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // bitmap = crupAndScale(bitmap, 300); // if you mind scaling
                imageView.setImageBitmap(bitmap);
                uploadImageOnServer();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                camera.setImageBitmap(photo);
                //   saveToInternalStorage(photo , imagePathLocal);
                // uploadImageOnServer();
                AsyncTaskImageUpload asyncTaskImageUpload = new AsyncTaskImageUpload();
                asyncTaskImageUpload.execute();
            }

        } else if (requestCode == MY_CAMERA_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = PublicProfile.this.getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            Log.w("path of >>>", picturePath + "");
            imagePathLocal = picturePath;
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            Log.w("path of ", picturePath + "");
            //  uploadToServer(picturePath);
            camera.setImageBitmap(thumbnail);
            //  saveToInternalStorage(thumbnail , imagePathLocal);
            //   uploadImageOnServer();
            AsyncTaskImageUpload asyncTaskImageUpload = new AsyncTaskImageUpload();
            asyncTaskImageUpload.execute();
        }
    }

    private class AsyncTaskImageUpload extends AsyncTask<Void, Void, Void> {

        ProgressDialog p = null;
        Item item = new Item();

        public AsyncTaskImageUpload() {


        }

        public AsyncTaskImageUpload(Item d) {
            item = d;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(PublicProfile.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            getAuth(firebaseUser);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //  sendTopic(deallocal);
            uploadImageOnServer(p);
            //  gsonCallToServer(item);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


           /* AsyncTaskExample asyncTaskExample = new AsyncTaskExample(item);
            asyncTaskExample.execute();*/
        }
    }

    private void uploadImageOnServer(final ProgressDialog p) {

        //   StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
        //gs://evd-gimme.appspot.com/user

        Date date = new Date();
        String user = pref.getString(getString(R.string.reg_user), "");
        String FileName = user + date.getDay() + date.getHours() + date.getMinutes() + date.getSeconds() + ".jpg";


        storageRef = FirebaseStorage.getInstance().getReference("vendors/"+user + "/" + FileName);
        final String tempFileName = Config.FirebaseInitial + FirebaseStorage.getInstance().getReference().getBucket() + "/vendors/"
                + user + "/" + FileName;

        camera.setDrawingCacheEnabled(true);
        camera.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) camera.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
                if (p != null) {
                    if (p.isShowing()) {
                        p.dismiss();
                    }
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.w("File", taskSnapshot.getMetadata().getPath());
                imageURL = tempFileName;
                System.out.println("Image Path >>>> " + imageURL);
                System.out.println("Image Path >>>> " + imageURL);
                System.out.println("Image Path >>>> " + imageURL);
                try {
                    imageList.removeAll(imageList);
                } catch (Exception e) {

                }
                imageList.add(imageURL);
                if (p != null) {
                    if (p.isShowing()) {
                        p.dismiss();
                    }
                }
            }
        });
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
    private void initView() {
        cancel = (Button)findViewById(R.id.cancel);
        Update = (Button)findViewById(R.id.Update);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicProfile.this.finish();

            }
        });

        Update.setOnClickListener(updateListener);

        BusinsesTypeText = (TextView) findViewById(R.id.BusinsesTypeText);
        BusinsesTypeText.setText(foodtype);


        initBusinessName();
        initBusinessDesc();
        initMobile();
        initAddress();
    }

    private void initAddress() {
        final TextView addressText, editaddress;
        final EditText addressEdit;
        final Button editAddressCancel, editAddressUpdate;

        addressText = (TextView) findViewById(R.id.addressText);
        editaddress = (TextView) findViewById(R.id.editaddress);
        addressEdit = (EditText) findViewById(R.id.addressEdit);
        editAddressCancel = (Button) findViewById(R.id.editAddressCancel);
        editAddressUpdate = (Button) findViewById(R.id.editAddressUpdate);
        addressText.setText(address);
        editaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addressEdit.setText("");
                addressText.setVisibility(View.GONE);
                editaddress.setVisibility(View.GONE);
                addressEdit.setVisibility(View.VISIBLE);
                editAddressCancel.setVisibility(View.VISIBLE);
                editAddressUpdate.setVisibility(View.VISIBLE);
            }
        });

        editAddressCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressText.setVisibility(View.VISIBLE);
                editaddress.setVisibility(View.VISIBLE);
                addressEdit.setVisibility(View.GONE);
                editAddressCancel.setVisibility(View.GONE);
                editAddressUpdate.setVisibility(View.GONE);
            }
        });
        editAddressUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressText.setVisibility(View.VISIBLE);
                editaddress.setVisibility(View.VISIBLE);
                addressEdit.setVisibility(View.GONE);
                editAddressCancel.setVisibility(View.GONE);
                editAddressUpdate.setVisibility(View.GONE);
                addressText.setText(addressEdit.getText().toString());
                editor.putString(getString(R.string.sAddress), addressEdit.getText().toString());
                editor.commit();
                // Call the adress update on map and lat long
                getLocation(addressEdit.getText().toString(),token);

            }
        });
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
                           // registerUser(address, FCMToken);
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
    private void initMobile() {
        final TextView mobileText, editmobile;
        final EditText mobileEdit;
        final Button editmobileDescCancel, editmobileDescUpdate;

        mobileText = (TextView) findViewById(R.id.mobileText);
        editmobile = (TextView) findViewById(R.id.editmobile);
        mobileEdit = (EditText) findViewById(R.id.mobileEdit);
        editmobileDescCancel = (Button) findViewById(R.id.editmobileDescCancel);
        editmobileDescUpdate = (Button) findViewById(R.id.editmobileDescUpdate);
        mobileText.setText(mobile);
        editmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileEdit.setText("");
                mobileText.setVisibility(View.GONE);
                editmobile.setVisibility(View.GONE);
                mobileEdit.setVisibility(View.VISIBLE);
                editmobileDescCancel.setVisibility(View.VISIBLE);
                editmobileDescUpdate.setVisibility(View.VISIBLE);
            }
        });

        editmobileDescCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileText.setVisibility(View.VISIBLE);
                editmobile.setVisibility(View.VISIBLE);
                mobileEdit.setVisibility(View.GONE);
                editmobileDescCancel.setVisibility(View.GONE);
                editmobileDescUpdate.setVisibility(View.GONE);
            }
        });
        editmobileDescUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileText.setVisibility(View.VISIBLE);
                editmobile.setVisibility(View.VISIBLE);
                mobileEdit.setVisibility(View.GONE);
                editmobileDescCancel.setVisibility(View.GONE);
                editmobileDescUpdate.setVisibility(View.GONE);
                mobileText.setText(mobileEdit.getText().toString());
                editor.putString(getString(R.string.sMobile), mobileEdit.getText().toString());
                editor.commit();

            }
        });
    }
    private void initBusinessDesc() {
        final TextView BusinsesDescText, editBusinessDesc;
        final EditText BusinsesDescEdit;
        final Button editBusinessDescCancel, editBusinessDescUpdate;

        BusinsesDescText = (TextView) findViewById(R.id.BusinsesDescText);
        editBusinessDesc = (TextView) findViewById(R.id.editBusinessDesc);
        BusinsesDescEdit = (EditText) findViewById(R.id.BusinsesDescEdit);
        editBusinessDescCancel = (Button) findViewById(R.id.editBusinessDescCancel);
        editBusinessDescUpdate = (Button) findViewById(R.id.editBusinessDescUpdate);
        BusinsesDescText.setText(description);
        editBusinessDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BusinsesDescEdit.setText("");
                BusinsesDescText.setVisibility(View.GONE);
                editBusinessDesc.setVisibility(View.GONE);
                BusinsesDescEdit.setVisibility(View.VISIBLE);
                editBusinessDescCancel.setVisibility(View.VISIBLE);
                editBusinessDescUpdate.setVisibility(View.VISIBLE);
            }
        });

        editBusinessDescCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinsesDescText.setVisibility(View.VISIBLE);
                editBusinessDesc.setVisibility(View.VISIBLE);
                BusinsesDescEdit.setVisibility(View.GONE);
                editBusinessDescCancel.setVisibility(View.GONE);
                editBusinessDescUpdate.setVisibility(View.GONE);
            }
        });
        editBusinessDescUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinsesDescText.setVisibility(View.VISIBLE);
                editBusinessDesc.setVisibility(View.VISIBLE);
                BusinsesDescEdit.setVisibility(View.GONE);
                editBusinessDescCancel.setVisibility(View.GONE);
                editBusinessDescUpdate.setVisibility(View.GONE);
                BusinsesDescText.setText(BusinsesDescEdit.getText().toString());
                editor.putString(getString(R.string.sDescription), BusinsesDescEdit.getText().toString());
                editor.commit();

            }
        });
    }
    private void initBusinessName()
    {
        final TextView BusinsesNameText,editBusinessName;
        final EditText BusinsesNameEdit;
        final Button editBusinsesNameCancel,editBusinsesNameUpdate;
        BusinsesNameText = (TextView)findViewById(R.id.BusinsesNameText);
        editBusinessName = (TextView)findViewById(R.id.editBusinessName);
        BusinsesNameEdit = (EditText)findViewById(R.id.BusinsesNameEdit);
        editBusinsesNameCancel = (Button)findViewById(R.id.editBusinsesNameCancel);
        editBusinsesNameUpdate = (Button)findViewById(R.id.editBusinsesNameUpdate);
        BusinsesNameText.setText(businessName);
        editBusinessName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BusinsesNameEdit.setText("");
                BusinsesNameText.setVisibility(View.GONE);
                editBusinessName.setVisibility(View.GONE);
                BusinsesNameEdit.setVisibility(View.VISIBLE);
                editBusinsesNameCancel.setVisibility(View.VISIBLE);
                editBusinsesNameUpdate.setVisibility(View.VISIBLE);
            }
        });

        editBusinsesNameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinsesNameText.setVisibility(View.VISIBLE);
                editBusinessName.setVisibility(View.VISIBLE);
                BusinsesNameEdit.setVisibility(View.GONE);
                editBusinsesNameCancel.setVisibility(View.GONE);
                editBusinsesNameUpdate.setVisibility(View.GONE);
            }
        });
        editBusinsesNameUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinsesNameText.setVisibility(View.VISIBLE);
                editBusinessName.setVisibility(View.VISIBLE);
                BusinsesNameEdit.setVisibility(View.GONE);
                editBusinsesNameCancel.setVisibility(View.GONE);
                editBusinsesNameUpdate.setVisibility(View.GONE);
                BusinsesNameText.setText(BusinsesNameEdit.getText().toString());
                editor.putString(getString(R.string.sBusiness),BusinsesNameEdit.getText().toString());
                editor.commit();
            }
        });



    }
    View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //
            AccountSetting accountSetting =  new AccountSetting();
            Content content  = new Content();

            accountSetting.setKind("vendor");
            accountSetting.setId(pref.getString(getString(R.string.vendor_id),""));

            businessName = pref.getString(getString(R.string.sBusiness), "");
            content.setBusinessName(businessName);
            description = pref.getString(getString(R.string.sDescription), "");
            content.setDescription(description);
            mobile = pref.getString(getString(R.string.sMobile), "");
            content.setMobile(mobile);

            lat = pref.getString(getString(R.string.lat), "0");
            llong = pref.getString(getString(R.string.llong), "0");
            content.setLatitude(Double.parseDouble(lat));
            content.setLatitude(Double.parseDouble(llong));
            address =  pref.getString(getString(R.string.sAddress), "");

            content.setImages(imageList);
            content.setAddress(address);
            accountSetting.setContent(content);
            UpdateWebService updateWebService =
                    new UpdateWebService("https://prod-gimme.appspot.com/api/v1/update/vendor",accountSetting);
            updateWebService.execute();
        }
    };
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

            // imageURL


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

                            Toast.makeText(PublicProfile.this, "Data Updated Sucessfully...", Toast.LENGTH_SHORT).show();
                            //accountSetting.setId(addDealResponse.getData().getId());
                            editor.putString(getString(R.string.vendorImage) ,imageURL);
                            editor.commit();
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