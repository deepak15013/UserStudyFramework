package com.iiitd.userstudyframework;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

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
        Log.v("dks","initObjects");

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

    private boolean writeToExternalStorage() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void saveToFile() {

        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath()+"/UserStudyFramework/");
        dir.mkdir();

        Log.v("dks",dir.getAbsolutePath());
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

            storeAwsFiles();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeAwsFiles() {

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-1:4084ff1b-ae36-428a-8090-aa3108340958", // Identity Pool ID
                Regions.AP_NORTHEAST_1 // Region
        );

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

        File file = Environment.getExternalStorageDirectory();
        File fileToUpload = new File(file.getAbsolutePath()+"/UserStudyFramework/UserDemographicInformation.txt");

        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        String MY_BUCKET = "userstudyframeworkbucket";
        String FOLDER_NAME = etName.getText().toString().toLowerCase().replaceAll("\\s","");
        String FILE_KEY = FOLDER_NAME + "/userdemographicinfo.txt";

        TransferObserver observer = transferUtility.upload(
                MY_BUCKET,     /* The bucket to upload to */
                FILE_KEY,    /* The key for the uploaded object */
                fileToUpload        /* The file where the data to upload exists */
        );

        transferObserverListener(observer);

        CustomPrefManager.shared().setFirstStart(false);
        CustomPrefManager.shared().setS3FolderName(FOLDER_NAME);

        Toast.makeText(DemographicForm.this, "user information uploaded", Toast.LENGTH_SHORT).show();
        startNextActivity();
    }

    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state+"");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                Log.e("percentage",percentage +"");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }


}
