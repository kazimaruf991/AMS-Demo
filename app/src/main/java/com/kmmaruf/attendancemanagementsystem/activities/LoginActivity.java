package com.kmmaruf.attendancemanagementsystem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDeviceManager;
import com.kmmaruf.attendancemanagementsystem.databinding.ActivityLoginBinding;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDevice;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKException;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ZKDevice zkDevice;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnConnect.setOnClickListener(v -> {
            connectToDevice();
        });
    }

    private void connectToDevice() {
        String ip = binding.editIp.getText().toString();
        String port = binding.editPort.getText().toString();
        String password = binding.editPassword.getText().toString();

        PleaseWaitDialog dialog = new PleaseWaitDialog(this);
        dialog.setMessage("Connecting to device...");
        dialog.setCancelable(false);
        dialog.show();

        executor.execute(() -> {
            zkDevice = ZKDeviceManager.getInstance();
            try {
                boolean connected = zkDevice.connect(ip, Integer.parseInt(port), Integer.parseInt(password));

                mainHandler.post(() -> {
                    dialog.dismiss();
                    if (connected) {
                        String deviceName = zkDevice.getDeviceName();
                        Toast.makeText(this, "Connected to the device successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("device_name", deviceName);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Can't connect to device", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (ZKException e) {
                mainHandler.post(() -> {
                    dialog.setMessage("Error: " + e.getMessage());
                    new Handler().postDelayed(dialog::dismiss, 1500);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

}