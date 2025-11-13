package com.kmmaruf.attendancemanagementsystem.nativeclasses;

public class ZKDeviceManager {
    private static ZKDevice instance;

    private ZKDeviceManager() {
    }

    public static ZKDevice getInstance() {
        if (instance == null) {
            instance = new ZKDevice();
        }
        return instance;
    }
}

