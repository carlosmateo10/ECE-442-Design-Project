package es.upm.dit.ece442designproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class EditGroupDialogFragment extends DialogFragment implements View.OnClickListener, SmartSocketEditGroupDialogAdapter.OnSmartSocketCheck {

    private TextView groupID;
    private RecyclerView smartSocketsList;
    private View RootView;
    private SmartSocketEditGroupDialogAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private TextView hubIdTextView;
    private ArrayList<String> ssMACs;

    public interface EditGroupDialogListener {
        void editGroupDialogResult(ArrayList<String> ssMACs);
    }

    public EditGroupDialogFragment.EditGroupDialogListener editGroupDialogListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.edit_group_dialog, container, false);

        groupID = RootView.findViewById(R.id.group_id);
        hubIdTextView = RootView.findViewById(R.id.hub_id_edit_group_dialog);

        hubIdTextView.setText(getArguments().getString("hubId"));
        groupID.setText(getArguments().getString("groupId"));

        smartSocketsList = RootView.findViewById(R.id.ss_list_dialog);

        ssMACs = (ArrayList<String>) getArguments().get("ssMACs");

        RootView.findViewById(R.id.button_cancel_save_ss).setOnClickListener(this);
        RootView.findViewById(R.id.button_save_ss).setOnClickListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //Define Layout
        smartSocketsList.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = firebaseFirestore.collection("hubs").document(getArguments().getString("hubCode")).collection("sockets_states");

        // RecyclerOptions
        FirestoreRecyclerOptions<SmartSocket> options = new FirestoreRecyclerOptions.Builder<SmartSocket>()
                .setLifecycleOwner(this)
                .setQuery(query, SmartSocket.class)
                .build();
        adapter = new SmartSocketEditGroupDialogAdapter(options, this,ssMACs);
        smartSocketsList.setAdapter(adapter);

        return RootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save_ss:
                onSaveClicked();
                break;
            case R.id.button_cancel_save_ss:
                onCancelClicked();
                break;
        }
    }

    public void onSaveClicked() {
        for(String ss : ssMACs) System.out.println(ss);
        editGroupDialogListener.editGroupDialogResult(ssMACs);
        dismiss();
    }

    public void onCancelClicked() { dismiss(); }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            editGroupDialogListener = (EditGroupDialogFragment.EditGroupDialogListener) getActivity();
        } catch (ClassCastException e){
        }
    }

    @Override
    public void onSmartSocketCheck(SmartSocket smartSocket, boolean isChecked) {
        if (isChecked) {
            if (!ssMACs.contains(smartSocket.getMAC())) ssMACs.add(smartSocket.getMAC());
        }
        if (!isChecked) ssMACs.remove(smartSocket.getMAC());
    }
}