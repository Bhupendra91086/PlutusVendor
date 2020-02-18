package com.app.plutusvendorapp.adapter;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.DealActive;
import com.app.plutusvendorapp.communicator.UpdateActiveIndicator;
import com.app.plutusvendorapp.database.ItemRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HorizontalListAdapter extends RecyclerView.Adapter<HorizontalListAdapter.DealHolder> {

    private Context context;
    Lifecycle lifecycle;
    private List<DealActive> dealList = new ArrayList<>();
    private UpdateActiveIndicator updateActiveIndicator;
    private ItemRepository itemRepository;
    public HorizontalListAdapter(Activity activity ,Context context, Lifecycle lifecycle) {
        this.context = context;
        this.lifecycle = lifecycle;
        updateActiveIndicator = (UpdateActiveIndicator) activity;
        itemRepository = new ItemRepository(context.getApplicationContext());
    }

    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new DealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DealHolder holder, int position) {

       final TextView textViewPrice = holder.price;
       final ImageView imageIMG  = holder.imageView;
        DealActive deal = dealList.get(position);

        holder.title.setText(deal.getContent().getTitle());
        holder.percentage_off.setText(deal.getContent().getDiscount()+"%");
        holder.subitem.setText(deal.getContent().getDescription());

        try {
            updateTimer(deal, holder.time);
        }
        catch (Exception e)
        {

        }
        if(deal!= null)
        {
            try
            {
                itemRepository.getTask(deal.getContent().getItemId())
                        .observe(new LifecycleOwner() {
                            @NonNull
                            @Override
                            public Lifecycle getLifecycle() {
                                return lifecycle;
                            }
                        }, new Observer<Item>() {
                            @Override
                            public void onChanged(@Nullable Item item) {
                                try
                                {

                                    textViewPrice.setText("$" + String.format("%.2f",item.getContent().getPrice()));
                                    LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                                            holder.imageView);
                                    loadImageFromServer.execute();
                                }
                                catch (Exception e)
                                {

                                }
                            }
                        });
            }
            catch (Exception e)
            {

            }


        }


    }


    private void updateTimer(final DealActive deal, final TextView textView) {

        String date1 = deal.getContent().getValidTo().replaceAll("-", "/");
        Date date = new Date(date1);
        //  System.out.println(date);

        Date d = new Date();

        long timeDiff = date.getTime() - d.getTime();
            if(timeDiff > 0)
            {
        //     System.out.println(timeDiff);

        long seconds = timeDiff / 1000;
        long min = seconds / 60;
        long hour = min / 60;
        // long days = hour / 24;



            final DecimalFormat formatter = new DecimalFormat("00");

            //  String timeInDays = "D : " + days + " H : " + hour % 24 + " M : " + min % 60 + " S : " + seconds % 60 + " ";
            String timeInDays = formatter.format(hour % 24) + ":" + formatter.format(min % 60) + ":" + formatter.format(seconds % 60) + " ";
            textView.setText(timeInDays);
            new CountDownTimer(timeDiff, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    try {
                        String date1 = deal.getContent().getValidTo().replaceAll("-", "/");
                        Date date = new Date(date1);
                        //  System.out.println(date);

                        Date d = new Date();

                        long timeDiff = date.getTime() - d.getTime();


                        //     System.out.println(timeDiff);

                        long seconds = timeDiff / 1000;
                        long min = seconds / 60;
                        long hour = min / 60;
                        //    long days = hour / 24;

                        //      String timeInDays = " D : " + days + " H : " + hour % 24 + " M : " + min % 60 + " S : " + seconds % 60 + " ";
                        String timeInDays = formatter.format(hour % 24) + ":" + formatter.format(min % 60) + ":"
                                + formatter.format(seconds % 60) + " ";



                        textView.setText(timeInDays);
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFinish() {
                    dealList.remove(deal);

                }
            }.start();

        }
        else
        {
            dealList.remove(deal);
        }
    }
    @Override
    public int getItemCount() {
        System.out.println("getItemCount >>> "+dealList.size());
        updateActiveIndicator.setIndicator(dealList.size());
        return dealList.size();
    }

    public void setDeals(List<DealActive> deals) {
        dealList = deals;
        System.out.println("setDeals >>> "+dealList.size());
       // notifyItemInserted(deals.size()-1);

        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        dealList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dealList.size());
    }

    public void restoreItem(DealActive deal, int position) {
        dealList.add(position, deal);
        // notify item added by position
        notifyItemInserted(position);
    }

    class DealHolder extends RecyclerView.ViewHolder {
        TextView price, time, title, subitem, percentage_off;
        ImageView imageView;

        public DealHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            subitem = itemView.findViewById(R.id.subitem);
            percentage_off = itemView.findViewById(R.id.percentage_off);

            imageView = (ImageView) itemView.findViewById(R.id.imageIMG);
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
