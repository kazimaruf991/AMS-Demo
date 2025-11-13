package com.kmmaruf.attendancemanagementsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kmmaruf.attendancemanagementsystem.R;
import com.kmmaruf.attendancemanagementsystem.nativeclasses.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> users;
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(User user);

        void onDelete(User user);

        void onShowFingers(User user);
    }

    public UserAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void updateList(List<User> newList) {
        users.clear();
        users.addAll(newList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvUserName.setText(user.name);
        holder.tvUserId.setText("User ID: " + user.userId);
        holder.tvPrivilege.setText("Privilege: " + user.privilege);
        holder.tvCard.setText("Card: " + user.card);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
        holder.btnShowFingers.setOnClickListener(v -> listener.onShowFingers(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserId, tvPrivilege, tvCard;
        MaterialButton btnEdit, btnDelete, btnShowFingers;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvPrivilege = itemView.findViewById(R.id.tvPrivilege);
            tvCard = itemView.findViewById(R.id.tvCard);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnShowFingers = itemView.findViewById(R.id.btnShowFingers);
        }
    }
}
