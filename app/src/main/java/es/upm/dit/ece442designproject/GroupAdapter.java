package es.upm.dit.ece442designproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class GroupAdapter extends FirestoreRecyclerAdapter<Group, GroupAdapter.GroupViewHolder> {

    private GroupAdapter.OnGroupListClick onGroupListClick;

    public GroupAdapter(@NonNull FirestoreRecyclerOptions<Group> options, OnGroupListClick onGroupListClickick) {
        super(options);
        this.onGroupListClick = onGroupListClickick;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupAdapter.GroupViewHolder holder, int position, @NonNull Group model) {
        holder.groupId.setText(model.getId());
        holder.groupCount.setText(model.getCount()+" Smarts Sockets");

    }

    @NonNull
    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.GroupViewHolder(view);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView groupId;
        TextView groupCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            groupId = itemView.findViewById(R.id.group_id);
            groupCount = itemView.findViewById(R.id.group_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onGroupListClick.onGroupClick(getItem(getAdapterPosition()));
        }

    }

    public interface OnGroupListClick {
        void onGroupClick(Group group);
    }
}