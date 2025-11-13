package com.kmmaruf.attendancemanagementsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmmaruf.attendancemanagementsystem.R;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.Attendance;

import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.UserViewHolder> {
    private final List<Attendance> records;

    public RecordsAdapter(List<Attendance> records) {
        this.records = records;
    }

    public void updateList(List<Attendance> newList) {
        records.clear();
        records.addAll(newList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Attendance record = records.get(position);
        holder.tvUserId.setText("User ID: " + record.userId);
        holder.tvTime.setText("Punch: " + record.timestamp);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvTime;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
