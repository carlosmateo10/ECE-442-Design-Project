package es.upm.dit.ece442designproject;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class SmartSocketActivity extends AppCompatActivity {

    private TextView ssID, ssMAC, ssPower;
    private ImageView ssState, ssSafety, ssManualBlock;
    private Button ssSwitch;
    private Intent intent;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private EditText ssThreshold;
    private Spinner ssMode, ssUpDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss);

        ssID = findViewById(R.id.ss_id);
        ssMAC = findViewById(R.id.ss_MAC);
        ssPower = findViewById(R.id.ss_power);
        ssState = findViewById(R.id.ss_state);
        ssSafety = findViewById(R.id.ss_safety);
        ssSwitch = findViewById(R.id.ss_swith);
        ssThreshold = findViewById(R.id.ss_threshold);
        ssMode = findViewById(R.id.mode_spinner);
        ssUpDown = findViewById(R.id.up_down_spinner);
        ssManualBlock = findViewById(R.id.ss_manual_block);

        intent = getIntent();

        ssID.setText(intent.getStringExtra("ID"));
        ssMAC.setText(intent.getStringExtra("MAC"));

        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(intent.getStringExtra("MAC"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot smartSocket = task.getResult();
                        ssMode.setSelection((int)(long)smartSocket.get("mode"));
                        ssThreshold.setText(smartSocket.get("threshold").toString());
                        if((boolean)smartSocket.get("up_down") == true) {
                            ssUpDown.setSelection(0);
                        } else {
                            ssUpDown.setSelection(1);
                        }
                    }
                });

        firebaseFirestore.collection("users").document(userId).collection("sockets_states").document(intent.getStringExtra("MAC"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        ssPower.setText("Power: "+documentSnapshot.get("power") + "W");
                        if((boolean)documentSnapshot.get("state") == true) {
                            ssState.setBackgroundResource(R.drawable.button_green_on);
                        } else {
                            ssState.setBackgroundResource(R.drawable.button_red_on);
                        }
                        if((boolean)documentSnapshot.get("power_safety") == false) {
                            ssSafety.setVisibility(View.VISIBLE);
                        } else {
                            ssSafety.setVisibility(View.GONE);
                        }
                        if((boolean)documentSnapshot.get("manual_block") == true) {
                            ssManualBlock.setVisibility(View.VISIBLE);
                        } else {
                            ssManualBlock.setVisibility(View.GONE);
                        }
                    }
                });

        ssSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Smart Socket SWITCH " + intent.getStringExtra("MAC"));
                final Map<String, Object> ss_update = new HashMap<>();

                firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(intent.getStringExtra("MAC"))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot smartSocketDoc = task.getResult();
                        Boolean ext_trig = (Boolean) smartSocketDoc.get("ext_trig");
                        ss_update.put("ext_trig", !ext_trig);
                        firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(intent.getStringExtra("MAC")).update(ss_update);
                    }
                });
            }
        });

    }

    public void deleteSS(View view) {
        firebaseFirestore.collection("users").document(userId).collection("sockets_states").document(intent.getStringExtra("MAC"))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(SmartSocketActivity.this, MainActivity.class));
                    }
                });
    }

    public void saveControl(View view) {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("mode", ssMode.getSelectedItemPosition());
        data.put("threshold", Integer.parseInt(ssThreshold.getText().toString()));
        if(ssUpDown.getSelectedItemPosition() == 0) {
            data.put("up_down", true);
        } else {
            data.put("up_down", false);
        }

        firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(intent.getStringExtra("MAC")).update(data);
    }
}
