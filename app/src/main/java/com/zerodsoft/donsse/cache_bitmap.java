package com.zerodsoft.donsse;


import android.graphics.Bitmap;

import android.os.AsyncTask;

import android.util.LruCache;

public class cache_bitmap
{
    private LruCache<String, Bitmap> memcache;
    backg_bitmap Background;

    void readycaching()
    {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 6;
        memcache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getByteCount() / 1024;
            }
        };

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {

        if (getBitmapFromMemCache(key) == null)
        {

            memcache.put(key, bitmap);

        }

    }

    public Bitmap getBitmapFromMemCache(String key)
    {
        return memcache.get(key);
    }

    Bitmap bitmap;

    public void loadBitmap(String resId, Bitmap givenbit)
    {

        final Bitmap bitmap = getBitmapFromMemCache(resId);
        if (bitmap != null)
        {
            this.bitmap = bitmap;
        } else
        {
            Background = new backg_bitmap();
            this.bitmap = givenbit;
            Background.ID = resId;
            Background.execute(givenbit);
        }
    }

    void removecache(String key)
    {
        memcache.remove(key);
    }

    public class backg_bitmap extends AsyncTask<Bitmap, Void, Bitmap>
    {
        String ID;

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps)
        {
            addBitmapToMemoryCache(ID, bitmaps[0]);
            return bitmaps[0];
        }
    }


}