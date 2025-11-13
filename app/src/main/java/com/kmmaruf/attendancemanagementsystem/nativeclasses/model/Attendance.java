package com.kmmaruf.attendancemanagementsystem.nativeclasses.model;

public class Attendance {
    public int uid;
    public String userId;
    public String timestamp;
    public int status;
    public int punch;

    public Attendance(int uid, String userId, String timestamp, int status, int punch) {
        this.uid = uid;
        this.userId = userId;
        this.timestamp = timestamp;
        this.status = status;
        this.punch = punch;
    }
}

