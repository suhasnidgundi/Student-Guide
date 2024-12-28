package com.zeal.studentguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeal.studentguide.R;
import com.zeal.studentguide.models.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private final List<User> users;
    private final UserClickListener listener;

    public interface UserClickListener {
        void onUserClick(User user);
        void onUserLongClick(User user);
    }

    public UserListAdapter(List<User> users, UserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName;
        private final TextView textEmail;
        private final TextView textRole;
        private final TextView textStatus;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textRole = itemView.findViewById(R.id.textRole);
            textStatus = itemView.findViewById(R.id.textStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onUserClick(users.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onUserLongClick(users.get(position));
                    return true;
                }
                return false;
            });
        }

        void bind(User user) {
            textName.setText(user.getName() != null ? user.getName() : "N/A");
            textEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            textRole.setText(user.getRole() != null ? user.getRole().toString() : "N/A");
            textStatus.setText(user.isActive() ? "Active" : "Inactive");

            int statusColor = user.isActive() ?
                    itemView.getContext().getColor(R.color.green) :
                    itemView.getContext().getColor(R.color.red);
            textStatus.setTextColor(statusColor);
        }
    }
}