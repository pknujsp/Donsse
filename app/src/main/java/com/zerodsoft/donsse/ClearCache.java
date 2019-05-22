package com.zerodsoft.donsse;

import android.content.Context;



import java.io.File;

public class ClearCache
{
    void cleanCache(Context context, File file)
    {
        File cache;

        if (file == null)
        {
            cache = context.getCacheDir();
        } else
        {
            cache = file;
        }

        if (cache == null)
            return;


        File[] cachefiles = cache.listFiles();

        try
        {
            for (int i = 0; i < cachefiles.length; i++)
            {
                if (cachefiles[i].isDirectory())
                    cleanCache(context, cachefiles[i]);
                else
                    cachefiles[i].delete();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}

