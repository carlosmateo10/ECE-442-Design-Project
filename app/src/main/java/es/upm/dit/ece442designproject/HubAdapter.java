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

public class HubAdapter extends FirestoreRecyclerAdapter<Hub, HubAdapter.HubViewHolder> {

    private OnHubListClick onHubListClick;

    public HubAdapter(@NonNull FirestoreRecyclerOptions<Hub> options, HubAdapter.OnHubListClick onHubListClick) {
        super(options);
        this.onHubListClick = onHubListClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull HubAdapter.HubViewHolder holder, int position, @NonNull Hub model) {
        holder.hubCode.setText(model.getCode());
        holder.hubId.setText(model.getId());
        holder.hubCount.setText(model.getCount()+" Smarts Sockets");
    }

    @NonNull
    @Override
    public HubAdapter.HubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hub_item, parent, false);
        return new HubAdapter.HubViewHolder(view);
    }

    public class HubViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView hubId;
        TextView hubCode;
        TextView hubCount;

        public HubViewHolder(@NonNull View itemView) {
            super(itemView);

            hubId = itemView.findViewById(R.id.hub_id);
            hubCode = itemView.findViewById(R.id.hub_code);
            hubCount = itemView.findViewById(R.id.hub_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onHubListClick.onHubClick(getItem(getAdapterPosition()));
        }

    }

    public interface OnHubListClick {
        void onHubClick(Hub hub);
    }

}