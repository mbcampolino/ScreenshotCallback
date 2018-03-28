package com.screenshotcallback;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseObservableActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 182;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkReadExternalStoragePermission();
    }

    ContentObserver observer  = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange, Uri uri) {

            if (uri.toString().matches(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/[0-9]+")) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(uri, new String[] {
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATA
                    }, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        /*final String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));*/
                        new AlertDialog.Builder(BaseObservableActivity.this).setMessage("SCREENSHOT!").show(); }
                } finally {
                    if (cursor != null)  {
                        cursor.close();
                    }
                }
            }
            super.onChange(selfChange, uri);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, observer
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(observer);
    }

    private void checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalStoragePermission();
        }
    }

    private void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
