package com.iiitd.userstudyframework;

import java.io.Serializable;

/**
 * Created by deepaksood619 on 15/6/16.
 */
public class UserInfoObject implements Serializable {
    private String name;
    private int age;
    private String gender;
    private String highestEducationLevel;
    private int numOfYearsUsingSP;
    private String typeOfLockUsed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHighestEducationLevel() {
        return highestEducationLevel;
    }

    public void setHighestEducationLevel(String highestEducationLevel) {
        this.highestEducationLevel = highestEducationLevel;
    }

    public int getNumOfYearsUsingSP() {
        return numOfYearsUsingSP;
    }

    public void setNumOfYearsUsingSP(int numOfYearsUsingSP) {
        this.numOfYearsUsingSP = numOfYearsUsingSP;
    }

    public String getTypeOfLockUsed() {
        return typeOfLockUsed;
    }

    public void setTypeOfLockUsed(String typeOfLockUsed) {
        this.typeOfLockUsed = typeOfLockUsed;
    }

    @Override
    public String toString() {
        return "Name - "+name +"\nAge - " + age +"\nGender - "+ gender +"\nHighest Education Level - "+highestEducationLevel +"\nNum of years using smartphone - "+numOfYearsUsingSP+"\nType of lock used - "+typeOfLockUsed;
    }
}
