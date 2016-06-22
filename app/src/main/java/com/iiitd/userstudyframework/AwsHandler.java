package com.iiitd.userstudyframework;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

/**
 * Created by deepaksood619 on 19/6/16.
 */
public class AwsHandler {

    private static AwsHandler sharedInstance;
    public static AwsHandler shared() {
        if(sharedInstance == null)
            sharedInstance = new AwsHandler();
        return sharedInstance;
    }

    private Context context;

    private static final String MY_BUCKET = "userstudyframeworkbucket";

    private TransferUtility transferUtility;

    public void init(Context context) {

        this.context = context;

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                this.context,
                "ap-northeast-1:4084ff1b-ae36-428a-8090-aa3108340958", // Identity Pool ID
                Regions.AP_NORTHEAST_1 // Region
        );

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

        transferUtility = new TransferUtility(s3, this.context);
    }

    public void storeAwsFile(File fileToUpload, String fileKey) {

        String fullFileKey = CustomPrefManager.shared().getS3FolderName() + "/" + fileKey;
        Log.v("dks","fullFileKey: "+fullFileKey);

        TransferObserver observer = transferUtility.upload(
                MY_BUCKET,      //The bucket to upload to
                fullFileKey,     //The key for the uploaded object
                fileToUpload         //The file where the data to upload exists
        );

        transferObserverListener(observer);
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