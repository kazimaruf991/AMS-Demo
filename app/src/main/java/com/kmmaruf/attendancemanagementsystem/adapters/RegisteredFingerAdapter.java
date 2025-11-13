package com.kmmaruf.attendancemanagementsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kmmaruf.attendancemanagementsystem.R;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Template;

import java.util.List;

public class RegisteredFingerAdapter extends RecyclerView.Adapter<RegisteredFingerAdapter.FingerViewHolder> {

    public interface OnDeleteListener {
        void onDelete(Template template, RegisteredFingerAdapter adapter);
    }

    private final List<Template> templates;
    private final OnDeleteListener deleteListener;

    String[] fingerOptions = {"0 - Right Thumb", "1 - Right Index", "2 - Right Middle", "3 - Right Ring", "4 - Right Little", "5 - Left Thumb", "6 - Left Index", "7 - Left Middle", "8 - Left Ring", "9 - Left Little"};

    public RegisteredFingerAdapter(List<Template> templates, OnDeleteListener deleteListener) {
        this.templates = templates;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public FingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registered_finger, parent, false);
        return new FingerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FingerViewHolder holder, int position) {
        Template template = templates.get(position);
        String userId = (template.user != null) ? template.user.userId : String.valueOf(template.finger.uid);
        holder.tvFingerInfo.setText(fingerOptions[template.finger.fid]);

        holder.btnDeleteFinger.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(v.getContext()).setTitle("Delete Finger").setMessage("Are you sure you want to delete this fingerprint?").setPositiveButton("Delete", (dialog, which) -> deleteListener.onDelete(template, this)).setNegativeButton("Cancel", null).show();
        });
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public static class FingerViewHolder extends RecyclerView.ViewHolder {
        TextView tvFingerInfo;
        ImageView btnDeleteFinger;

        public FingerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFingerInfo = itemView.findViewById(R.id.tvFingerInfo);
            btnDeleteFinger = itemView.findViewById(R.id.btnDeleteFinger);
        }
    }
}

