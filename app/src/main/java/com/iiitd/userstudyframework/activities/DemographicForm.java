package com.iiitd.userstudyframework.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.iiitd.userstudyframework.R;
import com.iiitd.userstudyframework.handler.CustomPrefManager;
import com.iiitd.userstudyframework.objects.UserInfoObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DemographicForm extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private EditText etName;
    private EditText etAge;

    private RadioGroup rgGender;
    private RadioButton rbGender;

    private Spinner spnEducationLevel;

    private EditText etYears;

    private RadioGroup rgTypeOfLock;
    private RadioButton rbLock;
    private EditText etOtherLock;

    UserInfoObject userInfoObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_form);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },PERMISSION_REQUEST_CODE);
        }

        CustomPrefManager.shared().init(getApplicationContext());
        if(!CustomPrefManager.shared().isFirstStart()) {
            startNextActivity();
        }

        initObjects();
    }

    private void initObjects() {
        userInfoObject = new UserInfoObject();

        etName = (EditText) findViewById(R.id.et_name);
        etAge = (EditText) findViewById(R.id.et_age);

        spnEducationLevel = (Spinner) findViewById(R.id.spn_education_level);

        rgGender = (RadioGroup) findViewById(R.id.rg_gender);
        rgTypeOfLock = (RadioGroup) findViewById(R.id.rg_type_of_lock);

        etYears = (EditText) findViewById(R.id.et_years);
        etOtherLock = (EditText) findViewById(R.id.et_other_lock);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initObjects();
                } else {
                    finish();
                }
            }
        }
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void submitDemographicInfo(View view) {

        if(etName.getText().toString().trim().equalsIgnoreCase("")) {
            etName.setError("Please enter your name");
        } else if(etAge.getText().toString().trim().equalsIgnoreCase("")) {
            etAge.setError("Please enter your age");
        } else if(rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(DemographicForm.this, "Please select your gender", Toast.LENGTH_SHORT).show();
        } else if(etYears.getText().toString().trim().equalsIgnoreCase("")) {
            etYears.setError("Please enter num of years using smartphone");
        } else if(rgTypeOfLock.getCheckedRadioButtonId() == -1) {
            Toast.makeText(DemographicForm.this, "Please select type of lock you use", Toast.LENGTH_SHORT).show();
        } else if(rgTypeOfLock.getCheckedRadioButtonId() == R.id.rb_other_lock && etOtherLock.getText().toString().trim().equalsIgnoreCase("")) {
            etOtherLock.setError("Please enter which lock you use");
        } else {

            // form is filled completely

            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            rbGender = (RadioButton) findViewById(selectedGenderId);

            int selectedLockId = rgTypeOfLock.getCheckedRadioButtonId();
            rbLock = (RadioButton) findViewById(selectedLockId);

            userInfoObject.setGender(rbGender.getText().toString());

            userInfoObject.setName(etName.getText().toString());
            userInfoObject.setAge(Integer.parseInt(etAge.getText().toString()));
            userInfoObject.setNumOfYearsUsingSP(Integer.parseInt(etYears.getText().toString()));
            userInfoObject.setHighestEducationLevel(spnEducationLevel.getSelectedItem().toString());

            if (rbLock.getText().toString().equalsIgnoreCase("others")) {
                userInfoObject.setTypeOfLockUsed(etOtherLock.getText().toString());
            }
            else {
                userInfoObject.setTypeOfLockUsed(rbLock.getText().toString());
            }

            if(writeToExternalStorage()) {
                saveToFile();
            }
            else {
                Toast.makeText(DemographicForm.this, "No external storage found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean writeToExternalStorage() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void saveToFile() {
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/UserStudyFramework/");
        dir.mkdir();

        CustomPrefManager.shared().setLocalStorageLocation(dir.getAbsolutePath());

        String fileName = "UserDemographicInformation.txt";
        File newFile = new File(dir, fileName);

        try {
            FileWriter fileWriter = new FileWriter(newFile.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userInfoObject.toString());

            bufferedWriter.close();
            fileWriter.close();

            Toast.makeText(DemographicForm.this, "Information saved to sdcard", Toast.LENGTH_SHORT).show();

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
            String timeStamp = simpleDateFormat.format(new Date());

            String FOLDER_NAME = etName.getText().toString().toLowerCase().replaceAll("\\s", "")+timeStamp;

            CustomPrefManager.shared().setS3FolderName(FOLDER_NAME);
            CustomPrefManager.shared().setFirstStart(false);

            startNextActivity();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
