package com.iiitd.userstudyframework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by deepaksood619 on 29/6/16.
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = MyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG,"AlarmReceived");
        Intent service = new Intent(context, com.iiitd.userstudyframework.MyAlarmService.class);
        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(service);
    }

}
