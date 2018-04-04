package com.screenshotcallback;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseObservableActivity extends AppCompatActivity implements MyScreenshotObserver.ScreenshotListener {

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 182;
    MyScreenshotObserver myScreenshotObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myScreenshotObserver = new MyScreenshotObserver(new Handler(), this, this);
        checkReadExternalStoragePermission();
    }

    @Override
    public void onScreenshotDetected(String name, String path) {
        new AlertDialog.Builder(this).setTitle(name).setMessage(path).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myScreenshotObserver.startObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myScreenshotObserver.pauseObserver();
    }

    private void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT < 23) return ;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myScreenshotObserver.startObserver();
            }
        }
    }
}
