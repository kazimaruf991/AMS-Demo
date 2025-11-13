package com.kmmaruf.attendancemanagementsystem.nativeclasses.model;

public class DeviceStorageInfo {
    private int userCount;
    private int maxUser;
    private int fingersCount;
    private int maxFingers;
    private int attnRecordsCount;
    private int maxAttnRecords;
    private int facesCount;
    private int maxFaces;

    public DeviceStorageInfo(int userCount, int maxUser, int fingersCount, int maxFingers, int attnRecordsCount, int maxAttnRecords, int facesCount, int maxFaces) {
        this.userCount = userCount;
        this.maxUser = maxUser;
        this.fingersCount = fingersCount;
        this.maxFingers = maxFingers;
        this.attnRecordsCount = attnRecordsCount;
        this.maxAttnRecords = maxAttnRecords;
        this.facesCount = facesCount;
        this.maxFaces = maxFaces;
    }

    public int getUserCount() {
        return userCount;
    }

    public int getMaxUser() {
        return maxUser;
    }

    public int getFingersCount() {
        return fingersCount;
    }

    public int getMaxFingers() {
        return maxFingers;
    }

    public int getAttnRecordsCount() {
        return attnRecordsCount;
    }

    public int getMaxAttnRecords() {
        return maxAttnRecords;
    }

    public int getFacesCount() {
        return facesCount;
    }

    public int getMaxFaces() {
        return maxFaces;
    }
}
