package com.kmmaruf.attendancemanagementsystem.nativeclasses.model;

public class Template {
    public final Finger finger;
    public final User user;

    public Template(Finger finger, User user) {
        this.finger = finger;
        this.user = user;
    }

    public String getDisplayUserId() {
        return (user != null) ? user.userId : "UID: " + finger.uid;
    }
}
