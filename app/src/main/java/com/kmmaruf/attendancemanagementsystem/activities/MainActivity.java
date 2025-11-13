package com.kmmaruf.attendancemanagementsystem.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.kmmaruf.attendancemanagementsystem.R;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDeviceManager;
import com.kmmaruf.attendancemanagementsystem.databinding.ActivityMainBinding;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDevice;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKConnectionException;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.User;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String deviceName = getIntent().getStringExtra("device_name");
        binding.tvDeviceName.setText(deviceName);

        binding.btnShowDeviceInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            startActivity(intent);
        });

        binding.btnShowUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
        });

        binding.btnAddUsers.setOnClickListener(v -> {
            showAddNewUserDialog();
        });

        binding.btnShowRecords.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordsActivity.class);
            startActivity(intent);
        });

        binding.btnSetTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                TimePickerDialog timePicker = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                    PleaseWaitDialog dialog = new PleaseWaitDialog(this);
                    dialog.setMessage("Setting device time...");
                    dialog.setCancelable(false);
                    dialog.show();

                    executor.execute(() -> {
                        ZKDevice zkDevice = ZKDeviceManager.getInstance();
                        mainHandler.post(() -> dialog.setMessage("Checking device connection..."));
                        if (!zkDevice.isConnected()) {
                            mainHandler.post(() -> dialog.setMessage("Re-connecting..."));
                            if (!zkDevice.reconnect()) {
                                throw new ZKConnectionException("Failed to connect");
                            }
                        }
                        int second = 0;

                        boolean success = zkDevice.setTime(year, month + 1, dayOfMonth, hourOfDay, minute, second);

                        mainHandler.post(() -> {
                            dialog.dismiss();
                            if (success) {
                                Toast.makeText(this, "Time set successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to set time", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.setTitle("Select Date");
            datePicker.show();
        });

        binding.btnRestartDevice.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this).setTitle("Confirm Restart").setMessage("Are you sure you want to restart the device?").setPositiveButton("Restart", (dialogInterface, which) -> {
                PleaseWaitDialog dialog = new PleaseWaitDialog(this);
                dialog.setMessage("Restarting device...");
                dialog.setCancelable(false);
                dialog.show();

                executor.execute(() -> {
                    ZKDevice zkDevice = ZKDeviceManager.getInstance();

                    mainHandler.post(() -> dialog.setMessage("Checking device connection..."));
                    if (!zkDevice.isConnected()) {
                        mainHandler.post(() -> dialog.setMessage("Re-connecting..."));
                        if (!zkDevice.reconnect()) {
                            mainHandler.post(() -> {
                                dialog.dismiss();
                                Toast.makeText(this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                    }

                    boolean success = zkDevice.restart();

                    mainHandler.post(() -> {
                        dialog.dismiss();
                        if (success) {
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(this, "Device restarted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to restart device", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }).setNegativeButton("Cancel", null).show();
        });

        binding.btnPowerOffDevice.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this).setTitle("Confirm Power Off").setMessage("Are you sure you want to power off the device?").setPositiveButton("Power Off", (dialogInterface, which) -> {
                PleaseWaitDialog dialog = new PleaseWaitDialog(this);
                dialog.setMessage("Powering off device...");
                dialog.setCancelable(false);
                dialog.show();

                executor.execute(() -> {
                    ZKDevice zkDevice = ZKDeviceManager.getInstance();

                    mainHandler.post(() -> dialog.setMessage("Checking device connection..."));
                    if (!zkDevice.isConnected()) {
                        mainHandler.post(() -> dialog.setMessage("Re-connecting..."));
                        if (!zkDevice.reconnect()) {
                            mainHandler.post(() -> {
                                dialog.dismiss();
                                Toast.makeText(this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                    }

                    boolean success = zkDevice.poweroff();

                    mainHandler.post(() -> {
                        dialog.dismiss();
                        if (success) {
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(this, "Device powered off successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to power off device", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }).setNegativeButton("Cancel", null).show();
        });

        binding.btnShowTemplates.setOnClickListener(v -> {
            Intent intent = new Intent(this, TemplatesActivity.class);
            startActivity(intent);
        });


        binding.btnStartLiveCapture.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_live_capture, null);
            TextView tvCaptureLog = dialogView.findViewById(R.id.tvCaptureLog);

            AlertDialog captureDialog = new MaterialAlertDialogBuilder(this).setTitle("Live Capture").setView(dialogView).setCancelable(false).setNegativeButton("Stop", (dialog, which) -> {
                ZKDeviceManager.getInstance().endLiveCapture();
            }).create();

            captureDialog.show();

            PleaseWaitDialog loadingDialog = new PleaseWaitDialog(this);
            loadingDialog.setMessage("Starting live capture...");
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            executor.execute(() -> {
                ZKDevice zkDevice = ZKDeviceManager.getInstance();

                mainHandler.post(() -> loadingDialog.setMessage("Checking device connection..."));
                if (!zkDevice.isConnected()) {
                    mainHandler.post(() -> loadingDialog.setMessage("Re-connecting..."));
                    if (!zkDevice.reconnect()) {
                        mainHandler.post(() -> {
                            loadingDialog.dismiss();
                            Toast.makeText(this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
                            captureDialog.dismiss();
                        });
                        return;
                    }
                }

                mainHandler.post(() -> {
                    loadingDialog.dismiss();
                    zkDevice.startLiveCapture(line -> runOnUiThread(() -> {
                        tvCaptureLog.append(line + "\n");
                    }), 10);
                });
            });
        });


        binding.btnRefreshDevice.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this).setTitle("Confirm Refresh").setMessage("Are you sure you want to refresh the device?").setPositiveButton("Refresh", (dialogInterface, which) -> {
                PleaseWaitDialog dialog = new PleaseWaitDialog(this);
                dialog.setMessage("Refreshing device...");
                dialog.setCancelable(false);
                dialog.show();

                executor.execute(() -> {
                    ZKDevice zkDevice = ZKDeviceManager.getInstance();

                    mainHandler.post(() -> dialog.setMessage("Checking device connection..."));
                    if (!zkDevice.isConnected()) {
                        mainHandler.post(() -> dialog.setMessage("Re-connecting..."));
                        if (!zkDevice.reconnect()) {
                            mainHandler.post(() -> {
                                dialog.dismiss();
                                Toast.makeText(this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                    }

                    boolean success = zkDevice.refreshData(); // Replace with your actual method

                    mainHandler.post(() -> {
                        dialog.dismiss();
                        if (success) {
                            Toast.makeText(this, "Device refreshed successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to refresh device", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }).setNegativeButton("Cancel", null).show();
        });


        binding.btnDisconnectDevice.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this).setTitle("Confirm Disconnect").setMessage("Are you sure you want to disconnect the device?").setPositiveButton("Disconnect", (dialog, which) -> {
                boolean success = ZKDeviceManager.getInstance().disconnect(); // Replace with your actual disconnect method
                if (success) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "Device disconnected successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to disconnect device", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Cancel", null).show();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZKDevice zkDevice = ZKDeviceManager.getInstance();
        if (zkDevice.isConnected()) {
            zkDevice.disconnect();
        }
        zkDevice.nativeDestroy();
    }

    public void showAddNewUserDialog() {
        ZKDevice zkDevice = ZKDeviceManager.getInstance();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);

        TextInputEditText editName = view.findViewById(R.id.editName);
        TextInputEditText editUserId = view.findViewById(R.id.editUserId);
        TextInputEditText editGroupId = view.findViewById(R.id.editGroupId);
        TextInputEditText editPassword = view.findViewById(R.id.editPassword);
        TextInputEditText editPrivilege = view.findViewById(R.id.editPrivilege);
        TextInputEditText editCard = view.findViewById(R.id.editCard);

        editUserId.setEnabled(true);

        editName.setText("");
        editUserId.setText("");
        editGroupId.setText("");
        editPassword.setText("");
        editPrivilege.setText("0");
        editCard.setText("0");

        AlertDialog dialog = new MaterialAlertDialogBuilder(this).setTitle("Add New User").setView(view).setCancelable(false).setPositiveButton("Add", null) // We'll override this later
                .setNegativeButton("Cancel", null).create();

        dialog.setOnShowListener(d -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setEnabled(false);

            TextWatcher validationWatcher = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean valid = !editName.getText().toString().trim().isEmpty() && !editUserId.getText().toString().trim().isEmpty() && !editPrivilege.getText().toString().trim().isEmpty() && !editCard.getText().toString().trim().isEmpty();

                    addButton.setEnabled(valid);
                    editUserId.setError(null);
                }
            };

            editName.addTextChangedListener(validationWatcher);
            editUserId.addTextChangedListener(validationWatcher);
            editGroupId.addTextChangedListener(validationWatcher);
            editPassword.addTextChangedListener(validationWatcher);
            editPrivilege.addTextChangedListener(validationWatcher);
            editCard.addTextChangedListener(validationWatcher);

            addButton.setOnClickListener(v -> {
                String name = editName.getText().toString().trim();
                String userId = editUserId.getText().toString().trim();
                String groupId = editGroupId.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String privilegeStr = editPrivilege.getText().toString().trim();
                String cardStr = editCard.getText().toString().trim();

                if (name.isEmpty() || userId.isEmpty() || privilegeStr.isEmpty() || cardStr.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int privilege;
                long card;
                try {
                    privilege = Integer.parseInt(privilegeStr);
                    card = Long.parseLong(cardStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid privilege or card number", Toast.LENGTH_SHORT).show();
                    return;
                }

                PleaseWaitDialog waitDialog = new PleaseWaitDialog(this);
                waitDialog.setMessage("Adding user...");
                waitDialog.setCancelable(false);
                waitDialog.show();

                executor.execute(() -> {
                    try {
                        mainHandler.post(() -> dialog.setMessage("Checking device connection..."));
                        if (!zkDevice.isConnected()) {
                            mainHandler.post(() -> dialog.setMessage("Re-connecting..."));
                            if (!zkDevice.reconnect()) {
                                throw new ZKConnectionException("Failed to connect");
                            }
                        }
                        List<User> userList = zkDevice.getUsers();
                        boolean duplicate = false;
                        for (User u : userList) {
                            if (u.userId.equals(userId)) {
                                duplicate = true;
                                break;
                            }
                        }

                        if (duplicate) {
                            mainHandler.post(() -> {
                                waitDialog.dismiss();
                                editUserId.setError("User ID already exists");
                                Toast.makeText(this, "User ID already exists", Toast.LENGTH_LONG).show();
                            });
                            return;
                        }

                        User newUser = new User(zkDevice.getNextUid(), name, privilege, password, groupId, userId, card);
                        zkDevice.disableDevice();
                        boolean success = zkDevice.setUser(newUser);
                        zkDevice.enableDevice();
                        zkDevice.refreshData();

                        mainHandler.post(() -> {
                            waitDialog.dismiss();
                            if (success) {
                                Toast.makeText(this, "User Added: " + newUser.name, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(this, "Failed to add user.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        mainHandler.post(() -> {
                            waitDialog.setMessage("Error adding user");
                            new Handler().postDelayed(waitDialog::dismiss, 1500);
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
            });
        });

        dialog.show();
    }
}
