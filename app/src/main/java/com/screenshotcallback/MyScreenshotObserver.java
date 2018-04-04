package com.screenshotcallback;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

public class MyScreenshotObserver extends ContentObserver {

    ScreenshotListener listener;
    Context context;

    public MyScreenshotObserver(Handler handler, ScreenshotListener listener, Context context) {
        super(handler);
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (uri.toString().matches(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/[0-9]+")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, new String[] {
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATA
                }, null, null, null);

                if (listener != null && cursor != null && cursor.moveToFirst()) {
                    final String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    listener.onScreenshotDetected(name, path);
                }
            } finally {
                if (cursor != null)  {
                    cursor.close();
                }
            }
        }

        super.onChange(selfChange, uri);
    }

    public void startObserver() {
        context.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, this
        );
    }

    public void pauseObserver() {
        context.getContentResolver().unregisterContentObserver(this);
    }

    public interface ScreenshotListener {
        void onScreenshotDetected(String name, String path);
    }
}
