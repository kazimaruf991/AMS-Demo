package com.kmmaruf.attendancemanagementsystem.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kmmaruf.attendancemanagementsystem.adapters.RecordsAdapter;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDeviceManager;
import com.kmmaruf.attendancemanagementsystem.databinding.ActivityRecordsBinding;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDevice;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKConnectionException;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Attendance;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordsActivity extends AppCompatActivity {
    private ActivityRecordsBinding binding;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.recyclerRecords.setLayoutManager(new LinearLayoutManager(this));
        readAttendanceRecords();
    }

    private void readAttendanceRecords() {
        PleaseWaitDialog dialog = new PleaseWaitDialog(this);
        dialog.setMessage("Reading attendance records from device...");
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

                mainHandler.post(() -> dialog.setMessage("Fetching attendance list..."));
                List<Attendance> allRecords = zkDevice.getAttendance();

                mainHandler.post(() -> {
                    dialog.dismiss();

                    if (!allRecords.isEmpty()) {
                        RecordsAdapter adapter = new RecordsAdapter(new ArrayList<>(allRecords));

                        binding.recyclerRecords.setAdapter(adapter);

                        binding.tvRecordCounts.setText(allRecords.size() + " records found");
                    } else {
                        Toast.makeText(this, "No record found on device", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    dialog.setMessage("Error reading records");
                    new Handler().postDelayed(dialog::dismiss, 1500);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
