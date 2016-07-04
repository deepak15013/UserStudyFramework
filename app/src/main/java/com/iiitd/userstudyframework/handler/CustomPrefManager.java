package com.iiitd.userstudyframework.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by deepaksood619 on 18/6/16.
 */
public class CustomPrefManager {

    private SharedPreferences sharedPreferences;

    public static final String PREF_FIRST_START = "PREF_FIRST_START";
    public static final String PREF_S3_FOLDER_NAME = "PREF_S3_FOLDER_NAME";
    public static final String PREF_LOCAL_STORAGE_LOCATION = "PREF_LOCAL_STORAGE_LOCATION";

    private static CustomPrefManager sharedInstance;
    public static CustomPrefManager shared() {
        if(sharedInstance == null) {
            sharedInstance = new CustomPrefManager();
        }
        return sharedInstance;
    }

    public void init(Context context) {
        if(sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean setFirstStart(boolean firstStart) {
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putBoolean(PREF_FIRST_START, firstStart).commit();
    }

    public boolean isFirstStart() {
        return sharedPreferences.getBoolean(PREF_FIRST_START, true);
    }

    public boolean setS3FolderName(String folderName) {
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_S3_FOLDER_NAME,folderName).commit();
    }

    public String getS3FolderName(Context context) {
        if(sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences.getString(PREF_S3_FOLDER_NAME,"");
    }

    public boolean setLocalStorageLocation(String location) {
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_LOCAL_STORAGE_LOCATION,location).commit();
    }

    public String getLocalStorageLocation() {
        return sharedPreferences.getString(PREF_LOCAL_STORAGE_LOCATION,"");
    }

}
