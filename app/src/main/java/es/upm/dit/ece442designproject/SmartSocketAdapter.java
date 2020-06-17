package es.upm.dit.ece442designproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

class SmartSocketAdapter extends FirestoreRecyclerAdapter<SmartSocket, SmartSocketAdapter.SmartSocketViewHolder> {

    private Context context;
    private OnSmartSocketListClick onSmartSocketListClick;
    private OnSmartSocketSwitchClick onSmartSocketSwitchClick;

    public SmartSocketAdapter(@NonNull FirestoreRecyclerOptions<SmartSocket> options, Context context, OnSmartSocketListClick onSmartSocketListClick, OnSmartSocketSwitchClick onSmartSocketSwitchClick) {
        super(options);
        this.context = context;
        this.onSmartSocketListClick = onSmartSocketListClick;
        this.onSmartSocketSwitchClick = onSmartSocketSwitchClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull SmartSocketViewHolder holder, int position, @NonNull SmartSocket model) {
        holder.ssMAC.setText(model.getMAC());
        holder.ssId.setText(model.getId());
        if(model.isState() == true) {
            holder.ssState.setBackgroundResource(R.drawable.button_green_on);
        } else {
            holder.ssState.setBackgroundResource(R.drawable.button_red_on);
        }

    }

    @NonNull
    @Override
    public SmartSocketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smartsocket_item, parent, false);
        return new SmartSocketViewHolder(view);
    }

    public class SmartSocketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ssMAC;
        TextView ssId;
        ImageView ssState;
        Button ssSwitch;

        public SmartSocketViewHolder(@NonNull View itemView) {
            super(itemView);

            ssMAC = itemView.findViewById(R.id.ss_MAC);
            ssId = itemView.findViewById(R.id.ss_id);
            ssState = itemView.findViewById(R.id.state_image);
            ssSwitch = itemView.findViewById(R.id.ss_switch_list);

            ssSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSmartSocketSwitchClick.onSmartSocketSwitch(getItem(getAdapterPosition()));
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSmartSocketListClick.onSmartSocketClick(getItem(getAdapterPosition()));
        }

    }

    public interface OnSmartSocketListClick {
        void onSmartSocketClick(SmartSocket smartSocket);
    }

    public interface OnSmartSocketSwitchClick {
        void onSmartSocketSwitch(SmartSocket smartSocket);
    }
}
