package com.kmmaruf.attendancemanagementsystem.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kmmaruf.attendancemanagementsystem.adapters.TemplatesAdapter;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDeviceManager;
import com.kmmaruf.attendancemanagementsystem.databinding.ActivityTemplatesBinding;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDevice;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKConnectionException;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Finger;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Template;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.User;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TemplatesActivity extends AppCompatActivity {

    private TemplatesAdapter adapter;
    private ExecutorService executor;
    private Handler mainHandler;
    private ActivityTemplatesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTemplatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        binding.recyclerTemplates.setLayoutManager(new LinearLayoutManager(this));

        readTemplates();
    }

    private void readTemplates() {
        PleaseWaitDialog dialog = new PleaseWaitDialog(this);
        dialog.setMessage("Reading fingerprint templates...");
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

                mainHandler.post(() -> dialog.setMessage("Fetching users from device..."));
                List<User> allUsers = zkDevice.getUsers();
                Map<Integer, User> userMap = new HashMap<>();
                for (User user : allUsers) {
                    userMap.put(user.uid, user);
                }

                mainHandler.post(() -> dialog.setMessage("Fetching templates from device..."));
                List<Finger> allFingers = zkDevice.getTemplates();

                List<Template> templates = new ArrayList<>();
                for (Finger finger : allFingers) {
                    User user = userMap.get(finger.uid);
                    templates.add(new Template(finger, user));
                }

                templates.sort((t1, t2) -> {
                    int userId1 = (t1.user != null && isNumeric(t1.user.userId)) ? Integer.parseInt(t1.user.userId) : t1.finger.uid;

                    int userId2 = (t2.user != null && isNumeric(t2.user.userId)) ? Integer.parseInt(t2.user.userId) : t2.finger.uid;

                    int cmp = Integer.compare(userId1, userId2);
                    if (cmp != 0) return cmp;

                    return Integer.compare(t1.finger.fid, t2.finger.fid);
                });


                mainHandler.post(() -> {
                    dialog.dismiss();

                    if (!templates.isEmpty()) {
                        adapter = new TemplatesAdapter(this, templates);
                        binding.recyclerTemplates.setAdapter(adapter);
                        binding.tvTemplatesCounts.setText(templates.size() + " templates found");
                    } else {
                        Toast.makeText(this, "No templates found on device", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    dialog.setMessage("Error reading templates");
                    new Handler().postDelayed(dialog::dismiss, 1500);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
