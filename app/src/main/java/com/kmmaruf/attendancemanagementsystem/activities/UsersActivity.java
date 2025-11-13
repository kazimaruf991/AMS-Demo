package com.kmmaruf.attendancemanagementsystem.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.kmmaruf.attendancemanagementsystem.R;
import com.kmmaruf.attendancemanagementsystem.adapters.RegisteredFingerAdapter;
import com.kmmaruf.attendancemanagementsystem.adapters.UserAdapter;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDeviceManager;
import com.kmmaruf.attendancemanagementsystem.databinding.ActivityUsersBinding;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.ZKDevice;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.exceptions.ZKConnectionException;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Finger;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Template;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.User;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UsersActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {
    private ActivityUsersBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    List<User> allUsers = new ArrayList<>();
    UserAdapter adapter;

    String[] searchOptions = {"User ID", "Name", "Privilege", "Card"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSearchBy.setAdapter(adapter);
        readUsers();

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void readUsers() {
        PleaseWaitDialog dialog = new PleaseWaitDialog(this);
        dialog.setMessage("Reading users from device...");
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

                mainHandler.post(() -> dialog.setMessage("Fetching user list..."));
                allUsers = zkDevice.getUsers();
                allUsers.sort((u1, u2) -> {
                    int id1 = Integer.parseInt(u1.userId);
                    int id2 = Integer.parseInt(u2.userId);
                    return Integer.compare(id1, id2);
                });

                mainHandler.post(() -> {
                    dialog.dismiss();

                    if (!allUsers.isEmpty()) {
                        adapter = new UserAdapter(new ArrayList<>(allUsers), this);

                        binding.recyclerUsers.setAdapter(adapter);

                        binding.tvUserCounts.setText(allUsers.size() + " users found");
                    } else {
                        Toast.makeText(this, "No users found on device", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    dialog.setMessage("Error reading users");
                    new Handler().postDelayed(dialog::dismiss, 1500);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateUserList() {
        PleaseWaitDialog dialog = new PleaseWaitDialog(this);
        dialog.setMessage("Getting latest user data from device...");
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

                mainHandler.post(() -> dialog.setMessage("Fetching user list..."));
                allUsers = zkDevice.getUsers();
                allUsers.sort((u1, u2) -> {
                    int id1 = Integer.parseInt(u1.userId);
                    int id2 = Integer.parseInt(u2.userId);
                    return Integer.compare(id1, id2);
                });

                mainHandler.post(() -> {
                    dialog.dismiss();

                    if (!allUsers.isEmpty()) {
                        if (binding.editSearch.getText().toString().trim().isEmpty()) {
                            adapter = new UserAdapter(new ArrayList<>(allUsers), this);

                            binding.recyclerUsers.setAdapter(adapter);

                            binding.tvUserCounts.setText(allUsers.size() + " users found");
                        } else {
                            filterUsers(binding.editSearch.getText().toString());
                        }
                    } else {
                        Toast.makeText(this, "No users found on device", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    dialog.setMessage("Error reading users");
                    new Handler().postDelayed(dialog::dismiss, 1500);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void filterUsers(String query) {
        String selectedField = binding.spinnerSearchBy.getSelectedItem().toString();

        if (query.trim().isEmpty()) {
            binding.tvUserCounts.setText(allUsers.size() + " users found");
            adapter.updateList(new ArrayList<>(allUsers));
            return;
        }

        List<User> filtered = new ArrayList<>();
        for (User user : allUsers) {
            switch (selectedField) {
                case "Name":
                    if (user.name.toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(user);
                    }
                    break;
                case "User ID":
                    if (user.userId.equalsIgnoreCase(query)) {
                        filtered.add(user);
                    }
                    break;
                case "Privilege":
                    if (String.valueOf(user.privilege).equals(query)) {
                        filtered.add(user);
                    }
                    break;
                case "Card":
                    if (String.valueOf(user.card).equals(query)) {
                        filtered.add(user);
                    }
                    break;
            }
        }
        binding.tvUserCounts.setText(filtered.size() + " users found");
        adapter.updateList(filtered);
    }

    public User getUpdatedUserData(User user) {
        ZKDevice zkDevice = ZKDeviceManager.getInstance();
        List<User> userList = zkDevice.getUsers();
        if (userList != null) {
            for (User u : userList) {
                if (u.equals(user)) return u;
            }
        }
        return null;
    }

    @Override
    public void onEdit(User user) {
        PleaseWaitDialog waitDialog = new PleaseWaitDialog(UsersActivity.this);
        waitDialog.setMessage("Validating user...");
        waitDialog.setCancelable(false);
        waitDialog.show();

        executor.execute(() -> {
            ZKDevice zkDevice = ZKDeviceManager.getInstance();

            mainHandler.post(() -> waitDialog.setMessage("Checking device connection..."));
            if (!zkDevice.isConnected()) {
                mainHandler.post(() -> waitDialog.setMessage("Re-connecting..."));
                if (!zkDevice.reconnect()) {
                    throw new ZKConnectionException("Failed to connect");
                }
            }

            User updatedUser = getUpdatedUserData(user);

            mainHandler.post(() -> {
                waitDialog.dismiss();

                if (updatedUser == null) {
                    Toast.makeText(UsersActivity.this, "Selected user no longer exists on the device.", Toast.LENGTH_LONG).show();
                    updateUserList();
                    return;
                }

                View view = LayoutInflater.from(UsersActivity.this).inflate(R.layout.dialog_edit_user, null);

                TextInputEditText editName = view.findViewById(R.id.editName);
                TextInputEditText editUserId = view.findViewById(R.id.editUserId);
                TextInputEditText editGroupId = view.findViewById(R.id.editGroupId);
                TextInputEditText editPassword = view.findViewById(R.id.editPassword);
                TextInputEditText editPrivilege = view.findViewById(R.id.editPrivilege);
                TextInputEditText editCard = view.findViewById(R.id.editCard);

                editName.setText(updatedUser.name);
                editUserId.setText(updatedUser.userId);
                editGroupId.setText(updatedUser.groupId);
                editPassword.setText(updatedUser.password);
                editPrivilege.setText(String.valueOf(updatedUser.privilege));
                editCard.setText(String.valueOf(updatedUser.card));

                new MaterialAlertDialogBuilder(UsersActivity.this).setTitle("Edit User").setView(view).setCancelable(false).setPositiveButton("Save", (dialogInterface, i) -> {
                    PleaseWaitDialog dialog = new PleaseWaitDialog(UsersActivity.this);
                    dialog.setMessage("Updating user...");
                    dialog.setCancelable(false);
                    dialog.show();

                    executor.execute(() -> {
                        try {
                            String name = editName.getText().toString().trim();
                            String userId = editUserId.getText().toString().trim();
                            String groupId = editGroupId.getText().toString().trim();
                            String password = editPassword.getText().toString().trim();
                            int privilege = Integer.parseInt(editPrivilege.getText().toString().trim());
                            long card = Long.parseLong(editCard.getText().toString().trim());

                            User newUser = new User(updatedUser.uid, name, privilege, password, groupId, userId, card);

                            zkDevice.disableDevice();
                            boolean success = zkDevice.setUser(newUser);
                            zkDevice.enableDevice();
                            zkDevice.refreshData();

                            mainHandler.post(() -> {
                                dialog.dismiss();
                                if (success) {
                                    Toast.makeText(UsersActivity.this, "User updated: " + newUser.name, Toast.LENGTH_SHORT).show();
                                    updateUserList();
                                } else {
                                    Toast.makeText(UsersActivity.this, "Failed to update user information.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (Exception e) {
                            mainHandler.post(() -> {
                                dialog.setMessage("Error updating user");
                                new Handler().postDelayed(dialog::dismiss, 1500);
                                Toast.makeText(UsersActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                }).setNegativeButton("Cancel", null).show();
            });
        });
    }


    @Override
    public void onDelete(User user) {
        PleaseWaitDialog waitDialog = new PleaseWaitDialog(UsersActivity.this);
        waitDialog.setMessage("Validating user...");
        waitDialog.setCancelable(false);
        waitDialog.show();

        executor.execute(() -> {
            try {
                ZKDevice zkDevice = ZKDeviceManager.getInstance();

                mainHandler.post(() -> waitDialog.setMessage("Checking device connection..."));
                if (!zkDevice.isConnected()) {
                    mainHandler.post(() -> waitDialog.setMessage("Re-connecting..."));
                    if (!zkDevice.reconnect()) {
                        throw new ZKConnectionException("Failed to connect");
                    }
                }

                User updatedUser = getUpdatedUserData(user);

                mainHandler.post(() -> {
                    waitDialog.dismiss();

                    if (updatedUser == null) {
                        Toast.makeText(UsersActivity.this, "Selected user no longer exists on the device.", Toast.LENGTH_LONG).show();
                        updateUserList();
                        return;
                    }

                    new MaterialAlertDialogBuilder(UsersActivity.this).setTitle("Delete User").setMessage("Are you sure you want to delete user \"" + updatedUser.name + "\" (ID: " + updatedUser.userId + ")?").setCancelable(false).setPositiveButton("Delete", (dialog, which) -> {
                        PleaseWaitDialog deleteDialog = new PleaseWaitDialog(UsersActivity.this);
                        deleteDialog.setMessage("Deleting user...");
                        deleteDialog.setCancelable(false);
                        deleteDialog.show();

                        executor.execute(() -> {
                            try {
                                boolean success = zkDevice.deleteUser(updatedUser.uid, updatedUser.userId);

                                mainHandler.post(() -> {
                                    deleteDialog.dismiss();
                                    if (success) {
                                        Toast.makeText(UsersActivity.this, "User deleted: " + updatedUser.name, Toast.LENGTH_SHORT).show();
                                        updateUserList();
                                    } else {
                                        Toast.makeText(UsersActivity.this, "Failed to delete user: " + updatedUser.name, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                mainHandler.post(() -> {
                                    deleteDialog.setMessage("Error deleting user");
                                    new Handler().postDelayed(deleteDialog::dismiss, 1500);
                                    Toast.makeText(UsersActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                        });
                    }).setNegativeButton("Cancel", null).show();
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    waitDialog.dismiss();
                    Toast.makeText(this, "Error re-loading users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    @Override
    public void onShowFingers(User user) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_registered_fingers, null);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this).setTitle(user.name + " - " + user.userId).setView(dialogView).setCancelable(false).create();

        RecyclerView recycler = dialogView.findViewById(R.id.recyclerRegisteredFingers);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnRegister = dialogView.findViewById(R.id.btnRegisterFinger);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        PleaseWaitDialog loadingDialog = new PleaseWaitDialog(this);
        loadingDialog.setMessage("Loading registered fingers...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        executor.execute(() -> {
            try {
                ZKDevice zkDevice = ZKDeviceManager.getInstance();

                mainHandler.post(() -> loadingDialog.setMessage("Checking device connection..."));
                if (!zkDevice.isConnected()) {
                    mainHandler.post(() -> loadingDialog.setMessage("Re-connecting..."));
                    if (!zkDevice.reconnect()) {
                        throw new ZKConnectionException("Failed to connect");
                    }
                }

                List<Finger> allFingers = zkDevice.getTemplates();

                List<Template> userTemplates = new ArrayList<>();
                for (Finger finger : allFingers) {
                    if (finger.uid == user.uid) {
                        userTemplates.add(new Template(finger, user));
                    }
                }

                userTemplates.sort(Comparator.comparingInt(t -> t.finger.fid));

                mainHandler.post(() -> {
                    loadingDialog.dismiss();
                    RegisteredFingerAdapter adapter = new RegisteredFingerAdapter(userTemplates, (template, regFingerAdapter) -> {
                        int position = userTemplates.indexOf(template);
                        if (zkDevice.deleteUserTemplate(template.user.uid, template.finger.fid, template.user.userId)) {
                            userTemplates.remove(position);
                            regFingerAdapter.notifyItemRemoved(position);
                            Toast.makeText(this, "Deleted FID: " + template.finger.fid, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to delete FID: " + template.finger.fid, Toast.LENGTH_SHORT).show();
                        }
                    });

                    recycler.setAdapter(adapter);
                    dialog.show();
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Error loading fingers: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnRegister.setOnClickListener(v -> {
            dialog.dismiss();
            regFinger(user);
        });
    }


    private void regFinger(User user) {
        PleaseWaitDialog waitDialog = new PleaseWaitDialog(UsersActivity.this);
        waitDialog.setMessage("Validating user...");
        waitDialog.setCancelable(false);
        waitDialog.show();

        executor.execute(() -> {
            ZKDevice zkDevice = ZKDeviceManager.getInstance();

            mainHandler.post(() -> waitDialog.setMessage("Checking device connection..."));
            if (!zkDevice.isConnected()) {
                mainHandler.post(() -> waitDialog.setMessage("Re-connecting..."));
                if (!zkDevice.reconnect()) {
                    throw new ZKConnectionException("Failed to connect");
                }
            }

            User updatedUser = getUpdatedUserData(user);

            mainHandler.post(() -> {
                waitDialog.dismiss();

                if (updatedUser == null) {
                    Toast.makeText(UsersActivity.this, "Selected user no longer exists on the device.", Toast.LENGTH_LONG).show();
                    updateUserList();
                    return;
                }

                String[] fingerOptions = {"0 - Right Thumb", "1 - Right Index", "2 - Right Middle", "3 - Right Ring", "4 - Right Little", "5 - Left Thumb", "6 - Left Index", "7 - Left Middle", "8 - Left Ring", "9 - Left Little"};

                final int[] selectedIndex = {1};

                new MaterialAlertDialogBuilder(UsersActivity.this).setTitle("Select Finger to Register").setSingleChoiceItems(fingerOptions, 1, (dialog, which) -> {
                    selectedIndex[0] = which;
                }).setPositiveButton("Register", (dialog, which) -> {
                    int fingerIndex = selectedIndex[0];

                    MaterialAlertDialogBuilder enrollDialog = new MaterialAlertDialogBuilder(UsersActivity.this);
                    enrollDialog.setMessage("Registering finger...");
                    enrollDialog.setCancelable(false);

                    enrollDialog.setNegativeButton("Cancel", (d, w) -> {
                        zkDevice.cancelCapture();
                        Toast.makeText(UsersActivity.this, "Enrollment cancelled", Toast.LENGTH_SHORT).show();

                        d.dismiss();
                    });

                    AlertDialog alertDialog = enrollDialog.show();

                    Future<?> enrollTask = executor.submit(() -> {
                        try {
                            zkDevice.deleteUserTemplate(updatedUser.uid, fingerIndex, updatedUser.userId);

                            if (Thread.currentThread().isInterrupted()) return;

                            boolean success = zkDevice.enrollUser(updatedUser.uid, fingerIndex, updatedUser.userId);

                            mainHandler.post(() -> {
                                alertDialog.dismiss();
                                if (success) {
                                    Toast.makeText(UsersActivity.this, "Registration of " + fingerOptions[fingerIndex] + " for " + updatedUser.name + " success.", Toast.LENGTH_SHORT).show();
                                    updateUserList();
                                } else {
                                    Toast.makeText(UsersActivity.this, "Failed to register finger", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (Exception e) {
                            mainHandler.post(() -> {
                                enrollDialog.setMessage("Error registering finger");
                                new Handler().postDelayed(alertDialog::dismiss, 1500);
                                Toast.makeText(UsersActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                }).setNegativeButton("Cancel", null).show();
            });
        });
    }

}
