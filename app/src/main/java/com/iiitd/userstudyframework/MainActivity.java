package com.iiitd.userstudyframework;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);

        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
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
    }

    public void editDemographicInformation(View view) {
        CustomPrefManager.shared().init(getApplicationContext());
        CustomPrefManager.shared().setFirstStart(true);

        Intent intent = new Intent(MainActivity.this, DemographicForm.class);
        finish();
        startActivity(intent);
    }
    
}
