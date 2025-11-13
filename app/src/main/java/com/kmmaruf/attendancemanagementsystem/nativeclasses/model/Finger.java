package com.kmmaruf.attendancemanagementsystem.nativeclasses.model;

public class Finger {
    public int uid;
    public int fid;
    public int valid;
    public byte[] templateData;

    public Finger(int uid, int fid, int valid, byte[] templateData) {
        this.uid = uid;
        this.fid = fid;
        this.valid = valid;
        this.templateData = templateData;
    }
}

