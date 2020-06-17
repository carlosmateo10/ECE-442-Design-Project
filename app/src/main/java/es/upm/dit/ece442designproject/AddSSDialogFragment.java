package es.upm.dit.ece442designproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddSSDialogFragment extends DialogFragment implements View.OnClickListener{

    private EditText ssID;
    private EditText ssMAC;
    private EditText ssPIN;
    private View RootView;

    public interface AddSSDialogListener {
        void addSSDialogResult(String id, String MAC, String PIN);
    }

    public AddSSDialogFragment.AddSSDialogListener addSSDialogListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.add_ss_dialog, container, false);

        ssID = RootView.findViewById(R.id.editText_ss_id);
        ssMAC = RootView.findViewById(R.id.editText_ss_MAC);
        ssPIN = RootView.findViewById(R.id.editText_ss_PIN);

        RootView.findViewById(R.id.button_cancel_save_ss).setOnClickListener(this);
        RootView.findViewById(R.id.button_save_ss).setOnClickListener(this);

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
        addSSDialogListener.addSSDialogResult(ssID.getText().toString(), ssMAC.getText().toString(), ssPIN.getText().toString());
        dismiss();
    }

    public void onCancelClicked() { dismiss(); }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addSSDialogListener = (AddSSDialogFragment.AddSSDialogListener) getActivity();
        } catch (ClassCastException e){
        }
    }
}

