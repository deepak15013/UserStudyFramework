package com.iiitd.userstudyframework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by deepaksood619 on 29/6/16.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, MyAlarmService.class);
        context.startActivity(service);
    }

}
