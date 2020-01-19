package com.codelabs.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final String ACTION_UPDATE_NOTIFICATION = "com.codelabs.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final int NOTIFICATION_ID = 0;
    private Button button_notify;
    private Button button_update;
    private Button button_cancel;
    private NotificationManager mNotificationManager;
    private NotificationReceiver mReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new NotificationReceiver();
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        button_notify = findViewById(R.id.notify);
        button_update = findViewById(R.id.update);
        button_cancel = findViewById(R.id.cancel);

        button_notify.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        button_update.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                cancelNotification();            }
        });

        buttonInitialiser(true, false, false);

        createNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                updateIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = getNotificationBuilder();
        builder.addAction(R.drawable.ic_update, "Update", updatePendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        buttonInitialiser(false, true,true);
    }

    private void createNotificationChannel(){
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID).
                setContentText("This is your notification text.")
                .setContentTitle("You've been notified!")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        return notifyBuilder;

    }

    private void updateNotification(){
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);

        NotificationCompat.Builder builder = getNotificationBuilder();
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage)
        .setBigContentTitle("Notification Updated!"));

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

        buttonInitialiser(false, false, true);

    }

    private void cancelNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
        buttonInitialiser(true, false, false);
    }

    private void buttonInitialiser(boolean notify, boolean update, boolean cancel){
        button_notify.setEnabled(notify);
        button_update.setEnabled(update);
        button_cancel.setEnabled(cancel);
    }

    public class NotificationReceiver extends BroadcastReceiver{

        public NotificationReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
