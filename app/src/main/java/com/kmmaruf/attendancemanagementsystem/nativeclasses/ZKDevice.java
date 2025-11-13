package com.kmmaruf.attendancemanagementsystem.nativeclasses;

import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKException;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Attendance;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.DeviceStorageInfo;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Finger;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.User;

import java.util.Calendar;
import java.util.List;

public class ZKDevice {
    static {
        System.loadLibrary("zkdevice");
    }

    public native boolean connect(String ip, int port, int password) throws ZKException;

    public native boolean reconnect() throws ZKException;

    public native boolean enrollUser(int uid, int tempId, String userId) throws ZKException;

    public native boolean verifyUser() throws ZKException;

    public native void cancelCapture() throws ZKException;

    public native boolean clearData() throws ZKException;

    public native boolean restart() throws ZKException;

    public native boolean poweroff() throws ZKException;

    public native String getDeviceName() throws ZKException;

    public native String getFirmwareVersion() throws ZKException;

    public native int getFingerVersion() throws ZKException;

    public native int getFaceVersion() throws ZKException;

    public native String getSerialNumber() throws ZKException;

    public native String getPlatform() throws ZKException;

    public native String getMACAddress() throws ZKException;

    public native int getPinWidth() throws ZKException;

    public native DeviceStorageInfo getDeviceStorageInfo() throws ZKException;

    public native List<User> getUsers() throws ZKException;

    public native boolean enableDevice() throws ZKException;

    public native boolean disableDevice() throws ZKException;

    public native boolean disconnect() throws ZKException;

    public native boolean refreshData() throws ZKException;

    public native boolean deleteUserTemplate(int uid, int fingerIndex, String userId) throws ZKException;

    public native boolean deleteUser(int uid, String userId) throws ZKException;

    public native List<Attendance> getAttendance() throws ZKException;

    public native boolean setUser(User user) throws ZKException;

    public native int getNextUid() throws ZKException;

    public native String getNetworkParams() throws ZKException;

    public native boolean setTime(int year, int month, int day, int hour, int minute, int second) throws ZKException;

    public native Calendar getTime() throws ZKException;

    public native List<Finger> getTemplates() throws ZKException;

    public native void startLiveCapture(CaptureCallback callback, int timeout) throws ZKException;

    public native boolean isConnected() throws ZKException;

    public native void endLiveCapture() throws ZKException;

    public native void nativeDestroy();
}