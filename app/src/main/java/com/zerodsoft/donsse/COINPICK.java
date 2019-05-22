package com.zerodsoft.donsse;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;


import android.media.MediaScannerConnection;

import android.net.Uri;


import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;


import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import org.opencv.core.Mat;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Date;
import java.text.SimpleDateFormat;

public class COINPICK extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    PhotoView imageview;
    boolean CHECK_SAVE_CALCU = false;
    Uri captured_image_uri;
    Button convertbtn;
    boolean imagecheck = false;
    boolean checkcalcfail = true;
    Bitmap imagebitmap;
    String path;
    String timeStamp;
    File IMAGEFILE1;
    int original_width, original_height;
    CountCoin countCoin = null;
    OnBackPressed onBackPressed = new OnBackPressed(COINPICK.this, "뒤로가기시 저장되지 않습니다, 한번 더 누르면 메인으로 이동합니다");

    private void start()
    {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagefile;
        try
        {
            imagefile = createImageFile();
            captured_image_uri = FileProvider.getUriForFile(COINPICK.this, "com.zerodsoft.donsse.provider", imagefile);
            IMAGEFILE1 = imagefile;
            it.putExtra(MediaStore.EXTRA_OUTPUT, captured_image_uri);
            startActivityForResult(it, REQUEST_IMAGE_CAPTURE);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        start();
        setContentView(R.layout.activity_coinpick);

        imageview = (PhotoView) findViewById(R.id.CoinImage);
        convertbtn = (Button) findViewById(R.id.calculate);
        OpenCVLoader.initDebug();

    }


    protected File createImageFile() throws IOException
    {
        timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        String imageFileName = "coinimage" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/com.zerodsoft.donsse/");
        if (!storageDir.exists())
        {
            storageDir.mkdirs();

        }
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );
        File nomedia_file = new File(storageDir, ".nomedia");
        if (!nomedia_file.exists())
        {
            nomedia_file.createNewFile();
        }
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            try
            {
                if (IMAGEFILE1.length() == 0)
                {
                    image_delete delete_image = new image_delete(IMAGEFILE1);
                    delete_image.deleteImageFile();
                    COINPICK.this.finish();
                }
                OrientImage orientimage = new OrientImage();
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), captured_image_uri);
                Bitmap bitmap2 = orientimage.ProcessImage(IMAGEFILE1.getAbsolutePath(), bitmap1);
                original_width = bitmap2.getWidth();
                original_height = bitmap2.getHeight();


                imagebitmap = bitmap2;

                Glide.with(this).load(bitmap2).into(imageview);
                imagecheck = true;


            } catch (Exception e)
            {

            }

            MediaScannerConnection.scanFile(COINPICK.this, new String[]{captured_image_uri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener()
                    {
                        @Override
                        public void onScanCompleted(String path, Uri uri)
                        {
                        }
                    });
        }
    }


    public void calculate(View v)
    {
        if (imagecheck == false)
        {
            Toast.makeText(getApplicationContext(), "사진이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else if (CHECK_SAVE_CALCU)
        {
            save_after_convert();
            setResult(RESULT_OK);
            Intent intent = new Intent(COINPICK.this, DBCONTENTS.class);

            int won10 = countCoin.GetCoin10s() + countCoin.GetCoin10b();
            int won50 = countCoin.GetCoin50();
            int won100 = countCoin.GetCoin100();
            int won500 = countCoin.GetCoin500();
            int total = won10 * 10 + won50 * 50 + won100 * 100 + won500 * 500;
            intent.putExtra("contents", path);
            intent.putExtra("won10", won10);
            intent.putExtra("won50", won50);
            intent.putExtra("won100", won100);
            intent.putExtra("won500", won500);
            intent.putExtra("total", total);

            COINPICK.this.finish();
            startActivity(intent);
        } else if (!checkcalcfail)
        {
            setResult(RESULT_CANCELED);
            COINPICK.this.finish();
        } else
        {
            CountTask counttask = new CountTask();
            counttask.width = original_width;
            counttask.height = original_height;
            counttask.execute(IMAGEFILE1);
        }
    }

    @Override
    public void onBackPressed()
    {
        boolean checkback = onBackPressed.BackPressed();
        if (checkback)
        {
            if (IMAGEFILE1.exists())
            {
                image_delete delete_image = new image_delete();
                delete_image.delete_image(IMAGEFILE1.getAbsolutePath());
            }
        }
    }

    public void save_after_convert()
    {
        SQLiteDatabase db = COIN_DB_HELPER.getInstance(this).getWritableDatabase();

        String date = timeStamp;

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("DATE", date);
        contentvalues.put("IMAGE_ADDRESS", path);
        contentvalues.put("TEN_SMALL", countCoin.GetCoin10s());
        contentvalues.put("TEN_BIG", countCoin.GetCoin10b());
        contentvalues.put("FIFTY", countCoin.GetCoin50());
        contentvalues.put("ONEHUNDRED", countCoin.GetCoin100());
        contentvalues.put("FIVEHUNDRED", countCoin.GetCoin500());
        db.insert("coins", null, contentvalues);
    }


    public class CountTask extends AsyncTask<File, Void, Bitmap>
    {
        int width, height;
        ProgressDialog progressdialog = new ProgressDialog(COINPICK.this);

        @Override
        protected void onPreExecute()
        {
            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressdialog.setMessage(getResources().getString(R.string.calculating));
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();
        }

        @Override
        protected Bitmap doInBackground(File... files)
        {
            imageprocessing processing = new imageprocessing();

            Mat mat = processing.process_(files[0]);
            if (mat == null)
            {
                image_delete deletenull = new image_delete();
                deletenull.delete_image(processing.get_image_path());
                return null; //검출되지 않음
            } else
            {
                path = processing.get_image_path();
                Bitmap calculated_bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, calculated_bitmap);
                File fileCacheItem = new File(path);
                try
                {
                    FileOutputStream out = new FileOutputStream(fileCacheItem);
                    calculated_bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                countCoin = processing.GetCountClass();
                return calculated_bitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap data)
        {
            if (data == null)
            {
                progressdialog.dismiss();
                convertbtn.setText(R.string.failed_calculation);
                checkcalcfail = false;
                CHECK_SAVE_CALCU = false;
                MainActivity.check_del = true;
            } else
            {
                Glide.with(getApplicationContext()).load(data).into(imageview);
                progressdialog.dismiss();
                convertbtn.setText(R.string.completed_calculation);
                CHECK_SAVE_CALCU = true;
                MainActivity.check_del = false;
            }
        }
    }
}

