package es.upm.dit.ece442designproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class SmartSocketEditGroupDialogAdapter extends FirestoreRecyclerAdapter<SmartSocket, SmartSocketEditGroupDialogAdapter.SmartSocketViewHolder> {

    private SmartSocketEditGroupDialogAdapter.OnSmartSocketCheck onSmartSocketCheck;
    private ArrayList<String> ssMACs;

    public SmartSocketEditGroupDialogAdapter(@NonNull FirestoreRecyclerOptions<SmartSocket> options, SmartSocketEditGroupDialogAdapter.OnSmartSocketCheck onSmartSocketCheck, ArrayList<String> ssMACs) {
        super(options);
        this.onSmartSocketCheck = onSmartSocketCheck;
        this.ssMACs = ssMACs;
    }

    @Override
    protected void onBindViewHolder(@NonNull SmartSocketEditGroupDialogAdapter.SmartSocketViewHolder holder, int position, @NonNull SmartSocket model) {
        holder.ssId.setText(model.getId());
        holder.ssMAC.setText(model.getMAC());
        if(ssMACs.contains(model.getMAC())) {
            holder.ssCheck.setChecked(true);
        }
    }

    @NonNull
    @Override
    public SmartSocketEditGroupDialogAdapter.SmartSocketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smartsocket_add_group, parent, false);
        return new SmartSocketEditGroupDialogAdapter.SmartSocketViewHolder(view);
    }

    public class SmartSocketViewHolder extends RecyclerView.ViewHolder {

        TextView ssId, ssMAC;
        CheckBox ssCheck;

        public SmartSocketViewHolder(@NonNull View itemView) {
            super(itemView);

            ssId = itemView.findViewById(R.id.ss_id);
            ssMAC = itemView.findViewById(R.id.ss_MAC);
            ssCheck = itemView.findViewById(R.id.ss_checkBox);

            ssCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onSmartSocketCheck.onSmartSocketCheck(getItem(getAdapterPosition()), isChecked);
                }
            });
        }

    }

    public interface OnSmartSocketCheck {
        void onSmartSocketCheck(SmartSocket smartSocket, boolean isChecked);
    }
}