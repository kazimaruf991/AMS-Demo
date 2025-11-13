package com.kmmaruf.attendancemanagementsystem.activities;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDeviceManager;
import com.kmmaruf.attendancemanagementsystem.databinding.ActivityDeviceInfoBinding;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDevice;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKConnectionException;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.DeviceStorageInfo;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceInfoActivity extends AppCompatActivity {

    private ActivityDeviceInfoBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        readMachineInfo();
    }

    private void readMachineInfo() {
        PleaseWaitDialog dialog = new PleaseWaitDialog(this);
        dialog.setMessage("Connecting to device...");
        dialog.setCancelable(false);
        dialog.show();

        executor.execute(() -> {
            try {
                ZKDevice zkDevice = ZKDeviceManager.getInstance();

                mainHandler.post(() -> dialog.setMessage("Checking device connection..."));
                if (!zkDevice.isConnected()) {
                    mainHandler.post(() -> dialog.setMessage("Re-connecting..."));
                    if (!zkDevice.reconnect()) {
                        throw new ZKConnectionException("Failed to connect");
                    }
                }

                mainHandler.post(() -> dialog.setMessage("Reading device info..."));
                String deviceName = zkDevice.getDeviceName();
                String firmwareVer = zkDevice.getFirmwareVersion();
                String faceVer = Integer.toString(zkDevice.getFaceVersion());
                String fingerVer = Integer.toString(zkDevice.getFingerVersion());
                String platform = zkDevice.getPlatform();
                String serialNum = zkDevice.getSerialNumber();
                String macAddress = zkDevice.getMACAddress();
                String pinWidth = Integer.toString(zkDevice.getPinWidth());
                Calendar calendar = zkDevice.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedTime = sdf.format(calendar.getTime());

                String networkInfo = zkDevice.getNetworkParams();

                mainHandler.post(() -> dialog.setMessage("Reading storage info..."));
                DeviceStorageInfo deviceStorageInfo = zkDevice.getDeviceStorageInfo();

                mainHandler.post(() -> {
                    // Update UI
                    binding.tvDeviceName.setText(deviceName);
                    binding.tvFirmwareVersion.setText(firmwareVer);
                    binding.tvFaceVersion.setText(faceVer);
                    binding.tvFingerVersion.setText(fingerVer);
                    binding.tvPlatform.setText(platform);
                    binding.tvSerialNumber.setText(serialNum);
                    binding.tvMACAddress.setText(macAddress);
                    binding.tvPINWidth.setText(pinWidth);
                    binding.tvDeviceTime.setText(formattedTime);
                    binding.tvNetworkInfo.setText(networkInfo.replace(", ", "\n"));

                    if (deviceStorageInfo != null) {
                        binding.layoutStorageInfo.setVisibility(View.VISIBLE);

                        setupUsageBar(binding.barUsers, deviceStorageInfo.getUserCount(), deviceStorageInfo.getMaxUser());
                        binding.usedUsers.setText(String.valueOf(deviceStorageInfo.getUserCount()));
                        binding.maxUsers.setText(String.valueOf(deviceStorageInfo.getMaxUser()));

                        setupUsageBar(binding.barFingers, deviceStorageInfo.getFingersCount(), deviceStorageInfo.getMaxFingers());
                        binding.usedFingers.setText(String.valueOf(deviceStorageInfo.getFingersCount()));
                        binding.maxFingers.setText(String.valueOf(deviceStorageInfo.getMaxFingers()));

                        setupUsageBar(binding.barAttendance, deviceStorageInfo.getAttnRecordsCount(), deviceStorageInfo.getMaxAttnRecords());
                        binding.usedAttendance.setText(String.valueOf(deviceStorageInfo.getAttnRecordsCount()));
                        binding.maxAttendance.setText(String.valueOf(deviceStorageInfo.getMaxAttnRecords()));

                        if (deviceStorageInfo.getMaxFaces() > 0) {
                            binding.layoutFaces.setVisibility(View.VISIBLE);
                            setupUsageBar(binding.barFaces, deviceStorageInfo.getFacesCount(), deviceStorageInfo.getMaxFaces());
                            binding.usedFaces.setText(String.valueOf(deviceStorageInfo.getFacesCount()));
                            binding.maxFaces.setText(String.valueOf(deviceStorageInfo.getMaxFaces()));
                        } else {
                            binding.layoutFaces.setVisibility(View.GONE);
                        }

                    } else {
                        binding.layoutStorageInfo.setVisibility(View.GONE);
                    }

                    dialog.dismiss();
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    dialog.setMessage("Error reading device");
                    new Handler().postDelayed(dialog::dismiss, 1500);
                });
            }
        });
    }


    private void setupUsageBar(HorizontalBarChart chart, float used, float max) {
        float free = max - used;
        float usageRatio = used / max;

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, new float[]{used, free}));

        int usedColor = (usageRatio > 0.8f) ? Color.parseColor("#EF5350") : (usageRatio > 0.5f) ? Color.parseColor("#FFA726") : Color.parseColor("#66BB6A");

        BarDataSet dataSet = new BarDataSet(entries, null);
        dataSet.setColors(usedColor, Color.LTGRAY);
        dataSet.setDrawValues(false);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        chart.setData(data);
        chart.setFitBars(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setAutoScaleMinMaxEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);


        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        chart.animateY(600);
        chart.invalidate();
    }

}
