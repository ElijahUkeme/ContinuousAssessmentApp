package com.elijahukeme.assessmentapp.model;

public class StudentModel {

    private String regNumber;
    private String name;
    private String gender;
    private String profileImage;
    private int testScore;
    private String testStatus;
    public StudentModel() {
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getTestScore() {
        return testScore;
    }

    public void setTestScore(int testScore) {
        this.testScore = testScore;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }
}
