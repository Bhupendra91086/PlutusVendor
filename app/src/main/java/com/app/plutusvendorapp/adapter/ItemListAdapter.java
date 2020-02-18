package com.app.plutusvendorapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.activity.AddItem;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.DealHolder> {

    Activity activity;

    public ItemListAdapter(Activity activity) {
        this.activity = activity;
    }

    private List<Item> items = new ArrayList<>();

    @NonNull
    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_vertical, parent, false);
        return new DealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealHolder holder, int position) {
        final Item item = items.get(position);
        MyApplication.tempItem = item;

        holder.price.setText("$" + String.format("%.2f",item.getContent().getPrice()));
        holder.title.setText(item.getContent().getName());
        holder.desc.setText(item.getContent().getDescription());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("button Clicked...");
                MyApplication.tempItem = item;
                Intent intent = new Intent(activity, AddItem.class);
                intent.putExtra("update", true);
                intent.putExtra("data", item);
                activity.startActivity(intent);
            }
        });

        try {
            if (null != item.getContent().getImages().get(0) ) {
                LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                        holder.imageView);
                loadImageFromServer.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> itemList) {
        items = itemList;
        notifyDataSetChanged();
    }
/*
    public void removeItem(int position) {
        dealList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dealList.size());
    }

    public void restoreItem(Deal deal, int position) {
        dealList.add(position, deal);
        // notify item added by position
        notifyItemInserted(position);
    }*/

    class DealHolder extends RecyclerView.ViewHolder {
        TextView price, title, desc;
        ImageView imageView, editButton;

        public DealHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            imageView = itemView.findViewById(R.id.image);
            editButton = itemView.findViewById(R.id.edit);


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
