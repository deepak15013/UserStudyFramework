package com.iiitd.userstudyframework;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

/**
 * Created by deepaksood619 on 29/6/16.
 */
public class MyAlarmService extends Service {

    private static final String TAG = MyAlarmService.class.getSimpleName();
    private NotificationManager mManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG,"AlarmService created");

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        Notification notification;

        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentTitle("User Study Framework");
        builder.setContentText("Sync completed");
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        notification = builder.build();

        AwsHandler.shared().init(this.getApplicationContext());

        File fileDir = Environment.getExternalStorageDirectory();
        String dirLocation = fileDir.getAbsolutePath() + "/UserStudyFramework";

        File folder = new File(dirLocation);
        if(folder.exists()) {
            File[] files = folder.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isFile()) {
                    AwsHandler.shared().storeAwsFile(files[i], files[i].getName());
                    Log.v("dks","file uploaded "+ files[i].getName());
                }
            }
        }
        else {
            Log.v("dks","folder not found");
        }

        mManager.notify(0, notification);

    }
}
