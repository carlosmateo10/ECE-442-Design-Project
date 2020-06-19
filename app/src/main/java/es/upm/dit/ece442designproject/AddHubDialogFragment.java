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

public class AddHubDialogFragment extends DialogFragment implements View.OnClickListener{

    private EditText hubID;
    private EditText hubCode;
    private View RootView;

    public interface AddHubDialogListener {
        void addHubDialogResult(String id, String code);
    }

    public AddHubDialogListener addHubDialogListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.add_hub_dialog, container, false);

        hubID = RootView.findViewById(R.id.editText_hub_id);
        hubCode = RootView.findViewById(R.id.editText_hub_code);

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
        addHubDialogListener.addHubDialogResult(hubID.getText().toString(), hubCode.getText().toString());
        dismiss();
    }

    public void onCancelClicked() { dismiss(); }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addHubDialogListener = (AddHubDialogListener) getActivity();
        } catch (ClassCastException e){
        }
    }
}
