package com.app.plutusvendorapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.item.Content;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.reguserres.RegisterUserResponse;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.database.ItemRepository;
import com.app.plutusvendorapp.util.Config;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity {


    private EditText itemName, itemPrice, itemDescription;
    private ImageView camera;
    private boolean b;
    private Item item;
    private ItemRepository itemRepository;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 2;
    private String imagePathLocal;
    FirebaseUser firebaseUser;
    private SharedPreferences pref = null;
    StorageReference storageRef;
    String imageURL = null;
    ArrayList<String> imageList = new ArrayList<>();
    private String token;
    String filePath;
    private Uri file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getAuth(firebaseUser);


        b = getIntent().getExtras().getBoolean("update");
        try {


            item = (Item) getIntent().getSerializableExtra("data");
            System.out.println(">>>>>>>>>>>>>>>>> " + item.getContent().getName());
        } catch (Exception e) {

        }

        setContentView(R.layout.addupdate_item);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode

        itemRepository = new ItemRepository(this.getApplicationContext());

        itemName = (EditText) findViewById(R.id.item_name);
        itemPrice = (EditText) findViewById(R.id.price);
        itemDescription = (EditText) findViewById(R.id.itemDesc);
        camera = (ImageView) findViewById(R.id.camera);
        camera.setOnClickListener(addImageListener);
        Button save = (Button) findViewById(R.id.add);
        if (b) {
            save.setText("Update");
            itemName.setText(item.getContent().getName());

            itemDescription.setText(item.getContent().getDescription());
            itemPrice.setText(item.getContent().getPrice() + "");
            try {
                if (item.getContent().getImages().get(0) != null) {

                    LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                            camera);
                    loadImageFromServer.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            save.setText("Add");
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (b) {
                    Content itemcontent = item.getContent();
                    itemcontent.setName(itemName.getText().toString());
                    itemcontent.setDescription(itemDescription.getText().toString());
                    itemcontent.setPrice(Float.parseFloat(itemPrice.getText().toString()));
                    imagePathLocal = itemName.getText().toString();

                    if(imageURL!= null && imageURL.length()>0) {
                        try {
                            imageList.removeAll(imageList);
                        } catch (Exception e) {

                        }

                        imageList.add(imageURL);
                        itemcontent.setImages(imageList);
                    }
                    updateItemToServer(item);
                } else {

                    if(itemPrice.getText().length() > 0 ) {

                        if(itemName.getText().length() > 0)
                        {


                        Item itemNew = new Item();
                        String user = pref.getString(getString(R.string.reg_user), "");
                        itemNew.setKind("item");
                        Content itemcontent = new Content();
                        itemcontent.setName(itemName.getText().toString());
                        imagePathLocal = itemName.getText().toString();

                        itemcontent.setDescription(itemDescription.getText().toString());
                        itemcontent.setPrice(Float.parseFloat(itemPrice.getText().toString()));
                        itemcontent.setImages(imageList);
                        itemcontent.setVendorId(user);
                        itemNew.setContent(itemcontent);
                        uploadItemToServer(itemNew);
                        }
                        else
                        {
                            itemName.setError("Please provide Item Name");
                        }

                    }
                    else
                    {
                        itemPrice.setError("Please provide item price!!");
                    }
                }


                //  AddItem.this.finish();
            }
        });


        Button cancel = (Button) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AddItem.this.finish();
            }
        });
    }



    private void uploadItemToServer(Item itemNew) {

        AddItemToServer addItemToServer = new AddItemToServer(itemNew);
        addItemToServer.execute();

    }
    private void updateItemToServer(Item itemNew) {

        UpdateItemToServer updateItemToServer = new UpdateItemToServer(itemNew);
        updateItemToServer.execute();

    }

    class UpdateItemToServer extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url = Config.UPDATE_VENDOR;
        Item itemNew;

        public UpdateItemToServer(Item itemNew) {

            this.itemNew = itemNew;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(AddItem.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // registerUserOnServer(url , itemNew);
            System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> " + new Gson().toJson(itemNew));


            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new Gson().toJson(itemNew)).getAsJsonObject();


            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.remove("tt");
            String jsonReq = jsonObject.toString();
            System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> " + jsonReq);
            updateItemOnServer(url, itemNew, jsonReq);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (p != null) {
                if (p.isShowing()) {
                    p.dismiss();
                }

                /*Intent intent = new Intent(AddItem.this, MenuManagement.class);
                startActivity(intent);
                AddItem.this.finish();*/

            }

        }
    }

    class AddItemToServer extends AsyncTask<Void, Void, Void> {
        ProgressDialog p = null;
        String url = "https://prod-gimme.appspot.com/api/v1/add/item";
        Item itemNew;

        public AddItemToServer(Item itemNew) {

            this.itemNew = itemNew;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(AddItem.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // registerUserOnServer(url , itemNew);
            System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> " + new Gson().toJson(itemNew));


            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new Gson().toJson(itemNew)).getAsJsonObject();


            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.remove("tt");
            String jsonReq = jsonObject.toString();
            System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> " + jsonReq);
            registerUserOnServer(url, itemNew, jsonReq);
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

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void updateItemOnServer(String url, final Item itemNew, String jsonObj) {

        System.out.println("Auth  >>>>>>>>>>>>>>>>> " + token);
        // System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> "+ new Gson().toJson(itemNew));
        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    new JSONObject(jsonObj),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> " + response.toString());
                            Gson gson = new Gson();
                            RegisterUserResponse serverResponse = gson.fromJson(response.toString(), RegisterUserResponse.class);

                            System.out.println("serverResponse.getId() " + " >>>>>>> " + serverResponse.getData().getId());
                            // editor.putString(getString(R.string.reg_user),serverResponse.getData().getId());
                            // editor.commit();
                           // AddItem(itemNew, serverResponse.getData().getId());
                           /* Intent intent = new Intent(AddItem.this, HomeActivity.class);
                            startActivity(intent);
                            AddItem.this.finish();*/
                            //    nextActivity();

                            updateItem(itemNew);

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


    private void registerUserOnServer(String url, final Item itemNew, String jsonObj) {

        System.out.println("Auth  >>>>>>>>>>>>>>>>> " + token);
        // System.out.println("new Gson().toJson(user)  >>>>>>>>>>>>>>>>> "+ new Gson().toJson(itemNew));
        try {
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    new JSONObject(jsonObj),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //   verificationSuccess(response);
                            System.out.println("Success >>>> " + response.toString());
                            Gson gson = new Gson();
                            RegisterUserResponse serverResponse = gson.fromJson(response.toString(), RegisterUserResponse.class);

                            System.out.println("serverResponse.getId() " + " >>>>>>> " + serverResponse.getData().getId());
                            // editor.putString(getString(R.string.reg_user),serverResponse.getData().getId());
                            // editor.commit();
                            AddItem(itemNew, serverResponse.getData().getId());
                           /* Intent intent = new Intent(AddItem.this, HomeActivity.class);
                            startActivity(intent);
                            AddItem.this.finish();*/
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener addImageListener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            if (AddItem.this.checkSelfPermission(PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED
                    && AddItem.this.checkSelfPermission(PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED
                    && AddItem.this.checkSelfPermission(PERMISSIONS_STORAGE[2]) != PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(AddItem.this, "camera permission granted", Toast.LENGTH_LONG).show();
               /* Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                selectImage();
            } else {
                Toast.makeText(AddItem.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItem.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    file = Uri.fromFile(new File(AddItem.this.getFilesDir(), "timeStamp.jpg"));
                    file = FileProvider.getUriForFile(AddItem.this, AddItem.this.getApplicationContext().getPackageName() +
                            ".provider", new File("/storage/emulated/0/abc.jpg"));

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_REQUEST);

                 /*   Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);*/

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
              //  Bitmap photo = (Bitmap) data.getExtras().get("data");

            /*    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                WeakReference<Bitmap> result1 = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(thumbnail,
                        thumbnail.getWidth(), thumbnail.getHeight(), false).copy(
                        Bitmap.Config.RGB_565, true));
                Bitmap bm=result1.get();

                camera.setImageBitmap(bm);*/
                camera.setImageURI(file);
                //   saveToInternalStorage(photo , imagePathLocal);
                // uploadImageOnServer();
                AsyncTaskImageUpload asyncTaskImageUpload = new AsyncTaskImageUpload();
                asyncTaskImageUpload.execute();
            }

        } else if (requestCode == MY_CAMERA_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = AddItem.this.getContentResolver().query(selectedImage, filePath, null, null, null);
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
            p = new ProgressDialog(AddItem.this);
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


        storageRef = FirebaseStorage.getInstance().getReference("items/"+user + "/" + FileName);
        final String tempFileName = Config.FirebaseInitial + FirebaseStorage.getInstance().getReference().getBucket() + "/items/"
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

    private void AddItem(Item item, String id) {
        //
        try {
            item.setId(id);
            // make web service call
            itemRepository.insertTask(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(AddItem.this, MenuManagement.class);
        startActivity(intent);
       AddItem.this.finish();
    }


    private void updateItem(Item item) {
        //


        try {
            // make web service call
            System.out.println("Just before update db ?>>>>>> ");
            System.out.println("Error while updating in db ?>>>>>> " );
            itemRepository.updateTask(item);
            System.out.println("After update db ?>>>>>> " );
        } catch (Exception e) {
            System.out.println("Error while updating in db ?>>>>>> " + e.getMessage());
            System.out.println("Error while updating in db ?>>>>>> " + e.getMessage());
            System.out.println("Error while updating in db ?>>>>>> " + e.getMessage());

            e.printStackTrace();
        }
        Intent intent = new Intent(AddItem.this, MenuManagement.class);
        startActivity(intent);
        AddItem.this.finish();

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
