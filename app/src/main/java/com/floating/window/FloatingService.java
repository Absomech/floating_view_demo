package com.floating.window;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class FloatingService extends Service {
    private WindowManager windowManager;
    private View floatingView;


    @Override
    public void onCreate() {
        super.onCreate();

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_view, null);

        // Set up a click listener for the button in the floating view
        Button button = floatingView.findViewById(R.id.floating_view_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(floatingView);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(Service.STOP_FOREGROUND_REMOVE);
                }
            }
        });

        // Set up the layout parameters for the floating view
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Add the floating view to the window manager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Save the initial touch position and view position
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Calculate the new position of the view based on the touch delta
                        int deltaX = (int) (event.getRawX() - initialTouchX);
                        int deltaY = (int) (event.getRawY() - initialTouchY);
                        params.x = initialX + deltaX;
                        params.y = initialY + deltaY;
                        // Update the view position
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                windowManager.addView(floatingView, params);
            }
        },8000);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, "AADIL")
                .setContentTitle("My Foreground Service")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }
}
