package com.kmmaruf.attendancemanagementsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmmaruf.attendancemanagementsystem.R;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Finger;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Template;

import java.util.List;

public class TemplatesAdapter extends RecyclerView.Adapter<TemplatesAdapter.TemplateViewHolder> {

    private final List<Template> templates;
    private final LayoutInflater inflater;

    public TemplatesAdapter(Context context, List<Template> templates) {
        this.templates = templates;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        Template template = templates.get(position);
        Finger finger = template.finger;

        holder.tvUserId.setText("UserId: " + template.getDisplayUserId());
        holder.tvFid.setText("FID: " + finger.fid);
        holder.tvValid.setText("Valid: " + finger.valid);
        holder.tvTemplate.setText("Template: " + toHexString(finger.templateData));
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvFid, tvValid, tvTemplate;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvFid = itemView.findViewById(R.id.tvFid);
            tvValid = itemView.findViewById(R.id.tvValid);
            tvTemplate = itemView.findViewById(R.id.tvTemplate);
        }
    }

    private String toHexString(byte[] data) {
        if (data == null || data.length == 0) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
