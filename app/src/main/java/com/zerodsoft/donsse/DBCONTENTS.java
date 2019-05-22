package com.zerodsoft.donsse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;


public class DBCONTENTS extends AppCompatActivity
{
    PhotoView image_view;
    TextView WON10, WON50, WON100, WON500, TOTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbcontents);
        image_view = (PhotoView) findViewById(R.id.imageview);
        WON10 = (TextView) findViewById(R.id.ten_won_text_dbcontents);
        WON50 = (TextView) findViewById(R.id.fifty_won_text_dbcontents);
        WON100 = (TextView) findViewById(R.id.onehundred_won_text_dbcontents);
        WON500 = (TextView) findViewById(R.id.fivehundred_won_text_dbcontents);
        TOTAL = (TextView) findViewById(R.id.total_text_dbcontents);

        Intent it = getIntent();

        String content = it.getStringExtra("contents");
        int won10 = it.getIntExtra("won10", 0);
        int won50 = it.getIntExtra("won50", 0);
        int won100 = it.getIntExtra("won100", 0);
        int won500 = it.getIntExtra("won500", 0);
        int total = it.getIntExtra("total", 0);

        WON10.setText(String.valueOf(won10));
        WON50.setText(String.valueOf(won50));
        WON100.setText(String.valueOf(won100));
        WON500.setText(String.valueOf(won500));
        TOTAL.setText(String.valueOf(total));
        Bitmap bitmap = BitmapFactory.decodeFile(content);

        Glide.with(this).load(bitmap).into(image_view);
    }

    void backtomain(View v)
    {
        DBCONTENTS.this.finish();
    }
}
