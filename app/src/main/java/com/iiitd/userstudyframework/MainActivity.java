package com.iiitd.userstudyframework;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String COL_1 = "_id";
    private static final String COL_2 = "username";
    private static final String COL_3 = "sign_up_time";
    private static final String COL_4 = "sign_up_video_location";
    private static final String COL_5 = "num_of_successful_login";
    private static final String COL_6 = "mean_login_time";
    private static final String COL_7 = "video_location_of_every_login";
    private static final String COL_8 = "num_of_failed_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void retrieveDetails(View view) {
        // Retrieve student records
        String URL = "content://com.example.numericalpass.provider/users";

        Uri students = Uri.parse(URL);
        //Cursor c = managedQuery(students, null, null, null, "");
        // query(Uri,          The content uri
        // mProjection,        The columns to return for each row
        // mSelectionClause,   Selection Criteria
        // mSelectionArgs,     Selection Criteria
        // mSortOrder);        The sort order for the returned rows

        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(students, null, null, null, "");

            if(cursor.moveToFirst()) {
                do {
                    Toast.makeText(this,
                            cursor.getString(cursor.getColumnIndex(COL_1)) +
                                    ", " +  cursor.getString(cursor.getColumnIndex(COL_2)) +
                                    ", " + cursor.getString(cursor.getColumnIndex(COL_3)) +
                                    ", " + cursor.getString(cursor.getColumnIndex(COL_4)) +
                                    ", "+ cursor.getString(cursor.getColumnIndex(COL_5)) +
                                    ", "+ cursor.getString(cursor.getColumnIndex(COL_6)) +
                                    ", "+cursor.getString(cursor.getColumnIndex(COL_7)) +
                                    ", "+cursor.getString(cursor.getColumnIndex(COL_8)),
                            Toast.LENGTH_LONG).show();
                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
