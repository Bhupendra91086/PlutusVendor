package com.app.plutusvendorapp.adapter;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.activity.DealManagement;
import com.app.plutusvendorapp.bean.DateBean;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.database.ItemRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {


    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(Deal deal);
    }

    private List<Deal> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;
    OnListItemClickListener onListItemClickListener;
    private ItemRepository itemRepository;
    Lifecycle lifecycle;

    public DealAdapter(Context context, OnDeleteButtonClickListener listener, OnListItemClickListener onListItemClickListener,
                       ItemRepository itemRepository, Lifecycle lifecycle) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onDeleteButtonClickListener = listener;
        this.onListItemClickListener = onListItemClickListener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemRepository = itemRepository;
        this.lifecycle = lifecycle;
    }


    @Override
    public DealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.dea_item, parent, false);
        return new DealViewHolder(itemView, onListItemClickListener);
    }

    @Override
    public void onBindViewHolder(DealViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Deal> newData) {
        if (data != null) {
            DealDiffCallback dealDiffCallback = new DealDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(dealDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle, subitem, price,dealStatus;
        private Button btnDelete;
        private ImageView imageIMG;
        OnListItemClickListener onListItemClickListener;

        DealViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView);
            this.onListItemClickListener = onListItemClickListener;
            tvTitle = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            subitem = itemView.findViewById(R.id.subitem);
            imageIMG = (ImageView)itemView.findViewById(R.id.imageIMG);
            dealStatus = (TextView)itemView.findViewById(R.id.dealStatus);
          /*  tvContent = itemView.findViewById(R.id.tvContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);*/
            itemView.setOnClickListener(this);

        }

        void bind(final Deal deal) {
            if (deal != null) {
                try {
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
                                   tvTitle.setText(deal.getContent().getTitle());
                                   price.setText("$" + String.format("%.2f",item.getContent().getPrice()));
                                   subitem.setText(deal.getContent().getDescription());

                                       LoadImageFromServer loadImageFromServer = new LoadImageFromServer(item.getContent().getImages().get(0),
                                               imageIMG);
                                       loadImageFromServer.execute();
                                   }
                                   catch (Exception e)
                                   {

                                   }
                               }
                           });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvTitle.setText(deal.getContent().getTitle());

                subitem.setText(deal.getContent().getDescription());

                setDealStatus(dealStatus,deal);


            }
        }

        @Override
        public void onClick(View v) {
            onListItemClickListener.onItemClick(getAdapterPosition());
        }
    }
    private void setDealStatus(TextView textView, Deal deal)
    {
        try {

        String startDateAndTime = deal.getContent().getValidFrom();
        String endDateAndTime = deal.getContent().getValidTo();
            System.err.println("Our ERrrroooorrrr >>"+startDateAndTime);
            System.err.println("Our ERrrroooorrrr >>"+endDateAndTime);


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println(formatter.format(date));

            String currentDateTime = formatter.format(date);
            System.err.println("Our ERrrroooorrrr >>"+currentDateTime);




            DateBean checkCurrentWithDealStart = dateDifference(currentDateTime,startDateAndTime);
            DateBean checkCurrentWithDealEnd = dateDifference(currentDateTime,endDateAndTime);

            DateBean DealTimeDiff = dateDifference(startDateAndTime,endDateAndTime);
            if(checkCurrentWithDealStart.getMinute() > 0)
            {
                textView.setText("Deal Start \n on \n "+ startDateAndTime );
            }
            else if(checkCurrentWithDealEnd.getMinute()>0)
            {
                textView.setText("Deal Alive \n Finish on \n"+endDateAndTime);

            }
            else
            {
                textView.setText("Deal Completed !!!");
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Our ERrrroooorrrr >>"+e.getMessage());


        }



    }

    private DateBean dateDifference(String date1, String date2)
    {
        DateBean dateBean = new DateBean();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        System.out.println("Time in seconds: " + diffSeconds + " seconds.");
        System.out.println("Time in minutes: " + diffMinutes + " minutes.");
        System.out.println("Time in hours: " + diffHours + " hours.");

        dateBean.setHours(diffHours);
        dateBean.setMinute(diffMinutes);
        dateBean.setSec(diffSeconds);
        return  dateBean;
    }


    class DealDiffCallback extends DiffUtil.Callback {

        private final List<Deal> oldDeals, newDeals;

        public DealDiffCallback(List<Deal> oldDeals, List<Deal> newDeals) {
            this.oldDeals = oldDeals;
            this.newDeals = newDeals;
        }

        @Override
        public int getOldListSize() {
            return oldDeals.size();
        }

        @Override
        public int getNewListSize() {
            return newDeals.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldDeals.get(oldItemPosition).getId() == newDeals.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldDeals.get(oldItemPosition).equals(newDeals.get(newItemPosition));
        }
    }

    public interface OnListItemClickListener {
        void onItemClick(int position);
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
