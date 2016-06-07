package com.iiitd.userstudyframework;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void retrieveDetails(View view) {
        // Retrieve student records
        String URL = "content://com.example.numericalpass.NumPin/users";

        Uri students = Uri.parse(URL);
        Cursor c = managedQuery(students, null, null, null, "");

        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex("_id")) +
                                ", " +  c.getString(c.getColumnIndex("username")) +
                                ", " + c.getString(c.getColumnIndex("number_of_successful_login")),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
}
