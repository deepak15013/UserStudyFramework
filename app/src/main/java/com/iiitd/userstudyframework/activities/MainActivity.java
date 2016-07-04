package com.iiitd.userstudyframework.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iiitd.userstudyframework.R;
import com.iiitd.userstudyframework.handler.AwsHandler;
import com.iiitd.userstudyframework.handler.CustomPrefManager;
import com.iiitd.userstudyframework.handler.MyReceiver;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE,30);

        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    public void uploadCSVFiles(View view) {
        Toast.makeText(MainActivity.this, "uploading files", Toast.LENGTH_SHORT).show();

        AwsHandler.shared().init(getApplicationContext());

        File fileDir = Environment.getExternalStorageDirectory();
        String dirLocation = fileDir.getAbsolutePath() + "/UserStudyFramework";

        File folder = new File(dirLocation);
        if(folder.exists()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    AwsHandler.shared().storeAwsFile(file, file.getName());
                    Log.v("dks", "file uploaded " + file.getName());
                }
            }
        }
        else {
            Log.v("dks","folder not found");
        }
    }

    public void editDemographicInformation(View view) {
        CustomPrefManager.shared().init(getApplicationContext());
        CustomPrefManager.shared().setFirstStart(true);

        Intent intent = new Intent(MainActivity.this, DemographicForm.class);
        finish();
        startActivity(intent);
    }
    
}
