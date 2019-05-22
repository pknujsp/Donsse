
package com.zerodsoft.donsse;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.RecyclerViewClickListener, SwipeRefreshLayout.OnRefreshListener, ActivityCompat.OnRequestPermissionsResultCallback
{
    private static final int REQUESTCODEADD = 1000;
    private static final int THROWDATA = 2000;
    static public boolean check_del = false;
    ImageView imageview_for;
    SwipeRefreshLayout swipe_refresh;
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    COIN_DB_HELPER dbHelper;
    boolean checkPER;
    OnBackPressed onBackPressed_main = new OnBackPressed(MainActivity.this,"뒤로가기를 한번 더 누르면 앱이 종료됩니다");
    private static final int REQUEST_CODE_STORAGE = 111;
    private static final int REQUEST_CODE_CAMERA = 222;
    String[] REQUIRED_PERMISSIONS_STORAGE =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
    String[] REQUIRED_PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetAppBar();
        imageview_for = (ImageView) findViewById(R.id.inform_imageview);
        checkPER = Check_STORAGE_PERMISSION();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            START_APP();
        } else if (checkPER)
        {
            START_APP();
        } else
        {
            imageview_for.setVisibility(View.VISIBLE);
            imageview_for.setImageDrawable(getDrawable(R.drawable.nostoragepermission));
        }
    }

    public void START_APP()
    {
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipe_refresh.setColorSchemeColors(Color.BLUE);
        swipe_refresh.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ADDBTN);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File checkdir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.zerodsoft.donsse");
                if (checkdir.exists())
                {
                    if (Check_CAMERA_PERMISSION())
                        startActivityForResult(new Intent(MainActivity.this, COINPICK.class), REQUESTCODEADD);
                } else
                {
                    reStart(getApplicationContext());
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        GetSetAdapter();
    }

    void GetSetAdapter()
    {
        dbHelper = COIN_DB_HELPER.getInstance(this);
        adapter = new RecyclerViewAdapter(dbHelper.datalist(), this, recyclerView);
        adapter.setOnClickListener(MainActivity.this);
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0)
        {
            imageview_for.setVisibility(View.VISIBLE);
            imageview_for.setImageDrawable(getDrawable(R.drawable.nodata));
        } else
        {
            imageview_for.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed()
    {
        boolean checkback_main = onBackPressed_main.BackPressed();

    }

    void ClearAdapter()
    {
        dbHelper = null;
        adapter = null;
    }

    @Override
    public void onDeleteButtonClicked(final int position)
    {
        final String PATH = adapter.ReturnImageData(position);
        final long deleteID = adapter.ReturnIdData(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.appicon);
        builder.setTitle(R.string.cleardata);
        builder.setMessage(R.string.question_cleardata);
        builder.setPositiveButton(R.string.okcleardata, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SQLiteDatabase db = COIN_DB_HELPER.getInstance(MainActivity.this).getWritableDatabase();
                ClearCache cce = new ClearCache();
                cce.cleanCache(getApplicationContext(), null);
                adapter.requested_removecache(position);
                image_delete ide = new image_delete();
                ide.delete_image(PATH);
                int deletedCount = db.delete("coins", "_ID" + " = " + deleteID, null);
                if (deletedCount == 0)
                {
                    Toast.makeText(MainActivity.this, R.string.failed_cleardata, Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(MainActivity.this, R.string.completed_cleardata, Toast.LENGTH_SHORT).show();
                    onRefresh();
                }
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (checkPER)
            START_APP();
    }

    @Override
    public void onLearnMoreButton(int position)
    {
        Intent intent = new Intent(MainActivity.this, DBCONTENTS.class);

        intent.putExtra("contents", adapter.ReturnImageData(position));
        intent.putExtra("won10", adapter.ReturnWon10(position));
        intent.putExtra("won50", adapter.ReturnWon50(position));
        intent.putExtra("won100", adapter.ReturnWon100(position));
        intent.putExtra("won500", adapter.ReturnWon500(position));
        intent.putExtra("total", adapter.ReturnTotal(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh()
    {

        swipe_refresh.setRefreshing(true);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ClearAdapter();
                GetSetAdapter();
                swipe_refresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, R.string.completed_refresh, Toast.LENGTH_SHORT).show();
            }
        }, 700);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODEADD && resultCode == RESULT_OK)
        {
            GetSetAdapter();
        } else if (requestCode == REQUESTCODEADD && resultCode == RESULT_CANCELED)
        {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_menu2:
                Toast.makeText(this, R.string.clearalldata, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void deleteAllData(MenuItem item)
    {
        File checkdir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.zerodsoft.donsse");

        if (checkdir.exists())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.mipmap.appicon);
            builder.setTitle(R.string.clearalldata);
            builder.setMessage(R.string.question_cleardata);
            builder.setPositiveButton(R.string.okcleardata, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (check_del == false)
                    {
                        SQLiteDatabase db = COIN_DB_HELPER.getInstance(MainActivity.this).getWritableDatabase();
                        COIN_DB_HELPER coindb = new COIN_DB_HELPER(MainActivity.this);
                        ClearCache cce = new ClearCache();
                        cce.cleanCache(getApplicationContext(), null);
                        coindb.deleteAll(db);

                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.zerodsoft.donsse";
                        File dir = new File(path);

                        if (dir.exists())
                        {
                            if (dir.isDirectory())
                            {
                                File[] files = dir.listFiles();

                                for (int i = 0; i < files.length; i++)
                                {

                                    String ext = files[i].getName().substring(files[i].getName().length() - 3, files[i].getName().length());
                                    if (ext.equals("png"))
                                        files[i].delete();
                                }
                            }
                        }

                        Toast.makeText(MainActivity.this, R.string.completed_clearalldata, Toast.LENGTH_SHORT).show();
                        onRefresh();
                        check_del = true;
                    } else
                    {
                        Toast.makeText(MainActivity.this, R.string.failed_clearalldata, Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        } else
        {
            reStart(getApplicationContext());
        }
    }

    public void reStart(Context context)
    {
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void SetAppBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("돈쎄");
    }

    public boolean Check_STORAGE_PERMISSION()
    {
        if (
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS_STORAGE[0]) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS_STORAGE[1]))
            {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.mipmap.appicon);
                builder.setTitle("권한 허용 요청");
                builder.setMessage("앱을 이용하기 위해서는 권한 허용이 필요합니다.");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                });
                builder.show();
            } else
            {

                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE);

            }
            return false;
        } else
        {
            return true;
        }
    }


    public boolean Check_CAMERA_PERMISSION()
    {
        if (
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS_CAMERA[0]))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.mipmap.appicon);
                builder.setTitle("권한 허용 요청");
                builder.setMessage("앱을 이용하기 위해서는 권한 허용이 필요합니다.");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA);

                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                });
                builder.show();
            } else
            {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA);
            }
            return false;
        } else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        switch (requestCode)
        {
            case REQUEST_CODE_STORAGE:

                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(R.mipmap.appicon);
                    builder.setTitle("권한 거부됨");
                    builder.setMessage("저장소 이용 권한을 거부하셨습니다. 스마트폰 내 애플리케이션 설정으로 이동하여 직접 권한을 허용해주십시오.");
                    builder.setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            it.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(it);
                            ActivityCompat.finishAffinity(MainActivity.this);
                            System.runFinalization();
                            System.exit(0);
                        }
                    });
                    builder.show();
                } else
                {
                    imageview_for.setVisibility(View.GONE);
                    START_APP();

                }
                break;
            case REQUEST_CODE_CAMERA:
                boolean FORCHECK = true;
                for (int i = 0; i < grantResults.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        FORCHECK = false;
                    }
                }
                if (grantResults.length > 0 && FORCHECK)
                {

                } else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(R.mipmap.appicon);
                    builder.setTitle("권한 거부됨");
                    builder.setMessage("카메라 이용 권한을 거부하셨습니다. 스마트폰 내 애플리케이션 설정으로 이동하여 직접 권한을 허용해주십시오.");
                    builder.setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            it.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(it);
                            ActivityCompat.finishAffinity(MainActivity.this);
                            System.runFinalization();
                            System.exit(0);
                        }
                    });
                    builder.show();
                }
                break;
        }
    }
}

