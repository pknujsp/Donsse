package com.zerodsoft.donsse;


import android.content.Context;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{

    private RecyclerViewClickListener mListener;
    public RecyclerView view;
    private List<CardItem> DATALIST;
    private Context CONTEXT;
    Bitmap bitmap;
    cache_bitmap caching = new cache_bitmap();


    public RecyclerViewAdapter(List<CardItem> datalist, Context context, RecyclerView rcw)
    {
        caching.readycaching();
        DATALIST = datalist;
        CONTEXT = context;
        view = rcw;
    }

    void requested_removecache(int position)
    {
        caching.removecache(position + "");
    }

    public void setOnClickListener(RecyclerViewClickListener listener)
    {
        mListener = listener;
    }

    public interface RecyclerViewClickListener
    {
        void onDeleteButtonClicked(int position);

        void onLearnMoreButton(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carditem, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        CardItem item = DATALIST.get(position);
        bitmap = downsizebitmap(item.getImageaddress());


        holder.tenwon.setText(String.valueOf(item.getWon_10s() + item.getWon_10b()));
        holder.fiftywon.setText(String.valueOf(item.getWon_50()));
        holder.onehundred.setText(String.valueOf(item.getWon_100()));
        holder.fivehundred.setText(String.valueOf(item.getWon_500()));
        holder.total.setText(GetTotal(item));
        caching.addBitmapToMemoryCache(position + "", bitmap);
        caching.getBitmapFromMemCache(position + "");
        caching.loadBitmap(position + "", bitmap);

        Glide.with(CONTEXT).load(caching.bitmap).into(holder.image);
        if (mListener != null)
        {
            final int pos = position;

            holder.delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.onDeleteButtonClicked(pos);
                }
            });

            holder.more.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.onLearnMoreButton(pos);
                }
            });
        }
    }

    public String ReturnDateData(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        String date_data = ITEM.getDate();
        return date_data;
    }

    public String ReturnImageData(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        String image_data = ITEM.getImageaddress();
        return image_data;
    }

    public long ReturnIdData(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        long id_data = ITEM.getID();
        return id_data;
    }

    public int ReturnWon10(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        int won10 = ITEM.getWon_10s() + ITEM.getWon_10b();
        return won10;
    }

    public int ReturnWon50(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        int won50 = ITEM.getWon_50();
        return won50;
    }

    public int ReturnWon100(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        int won100 = ITEM.getWon_100();
        return won100;
    }

    public int ReturnWon500(int position)
    {
        CardItem ITEM = DATALIST.get(position);
        int won500 = ITEM.getWon_500();
        return won500;
    }

    public int ReturnTotal(int position)
    {
        CardItem item = DATALIST.get(position);
        int total = item.getWon_10b() * 10 + item.getWon_10s() * 10
                + item.getWon_50() * 50 + item.getWon_100() * 100 + item.getWon_500() * 500;
        return total;
    }

    @Override
    public int getItemCount()
    {
        return DATALIST.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView total, tenwon, fiftywon, onehundred, fivehundred;
        ImageView image;
        Button delete, more;

        public ViewHolder(View viewitem)
        {
            super(viewitem);
            total = (TextView) viewitem.findViewById(R.id.total_text);
            tenwon = (TextView) viewitem.findViewById(R.id.ten_won_text);
            fiftywon = (TextView) viewitem.findViewById(R.id.fifty_won_text);
            onehundred = (TextView) viewitem.findViewById(R.id.onehundred_won_text);
            fivehundred = (TextView) viewitem.findViewById(R.id.fivehundred_won_text);
            image = (ImageView) viewitem.findViewById(R.id.coin_image);
            delete = (Button) viewitem.findViewById(R.id.delete_button);
            more = (Button) viewitem.findViewById(R.id.more_button);
        }
    }

    Bitmap downsizebitmap(String path)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(path, option);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return resizedbitmap;
    }

    private String GetTotal(CardItem item)
    {
        int total = item.getWon_10b() * 10 + item.getWon_10s() * 10
                + item.getWon_50() * 50 + item.getWon_100() * 100 + item.getWon_500() * 500;
        return String.valueOf(total);
    }
}
