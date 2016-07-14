package com.iiitd.userstudyframework.handler;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.iiitd.userstudyframework.R;

import java.io.File;

/**
 * Created by deepaksood619 on 29/6/16.
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = MyReceiver.class.getSimpleName();
    private NotificationManager mManager;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        Notification.Builder builder = new Notification.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentTitle("User Study Framework");
        builder.setContentText("Sync completed");
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        notification = builder.build();

        AwsHandler.shared().init(context);

        File fileDir = Environment.getExternalStorageDirectory();
        String dirLocation = fileDir.getAbsolutePath() + "/UserStudyFramework";

        File folder = new File(dirLocation);
        if(folder.exists()) {
            File[] files = folder.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isFile()) {
                    AwsHandler.shared().storeAwsFile(files[i], files[i].getName());
                    Log.v(TAG,"file uploaded "+ files[i].getName());
                }
            }
        }
        else {
            Log.v(TAG,"folder not found");
        }

        mManager.notify(0, notification);

    }

}
