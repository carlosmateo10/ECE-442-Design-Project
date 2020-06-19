package es.upm.dit.ece442designproject;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SmartSocketActivity extends AppCompatActivity {

    private TextView ssID, ssMAC, ssPower, thresholdText, upDownText;
    private ImageView ssState, ssSafety, ssManualBlock;
    private Button ssSwitch, ssTimeSchedule;
    private Intent intent;
    private FirebaseFirestore firebaseFirestore;
    private String hubCode, hubId, userID;
    private EditText ssThreshold;
    private Spinner ssMode, ssUpDown;
    private TextView timeOnPickerText, timeOffPickerText, timeOnText, timeOffText;

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
        thresholdText = findViewById(R.id.textView_threshold);
        upDownText = findViewById(R.id.textView_up_down);
        timeOnPickerText = findViewById(R.id.timeOnPicker);
        timeOffPickerText = findViewById(R.id.timeOffPicker);
        timeOnText = findViewById(R.id.textView_time_on);
        timeOffText = findViewById(R.id.textView_time_off);
        ssTimeSchedule = findViewById(R.id.time_trig_btn);

        intent = getIntent();

        ssID.setText(intent.getStringExtra("ID"));
        ssMAC.setText(intent.getStringExtra("MAC"));
        hubCode = intent.getStringExtra("hubCode");
        hubId = intent.getStringExtra("hubId");
        userID = FirebaseAuth.getInstance().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC"))
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
                        if(smartSocket.get("time_on").toString()=="") {
                            timeOnPickerText.setText("SELECT TIME ON");
                        } else {
                            timeOnPickerText.setText(smartSocket.get("time_on").toString());
                        }
                        if(smartSocket.get("time_off").toString()=="") {
                            timeOffPickerText.setText("SELECT TIME OFF");
                        } else {
                            timeOffPickerText.setText(smartSocket.get("time_off").toString());
                        }
                    }
                });

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_states").document(intent.getStringExtra("MAC"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()) {
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
                    }
                });

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()) {
                            if((boolean)documentSnapshot.get("time_trig") == true) {
                                ssTimeSchedule.setText("TIME SCHEDULE ON");
                            } else {
                                ssTimeSchedule.setText("TIME SCHEDULE OFF");
                            }
                        }
                    }
                });

        ssSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Smart Socket SWITCH " + intent.getStringExtra("MAC"));
                final Map<String, Object> ss_update = new HashMap<>();

                firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC"))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot smartSocketDoc = task.getResult();
                        Boolean ext_trig = (Boolean) smartSocketDoc.get("ext_trig");
                        ss_update.put("ext_trig", !ext_trig);
                        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC")).update(ss_update);
                    }
                });
            }
        });

        ssTimeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Smart Socket TIME SCHEDULE " + intent.getStringExtra("MAC"));
                final Map<String, Object> ss_update = new HashMap<>();

                firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC"))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot smartSocketDoc = task.getResult();
                        Boolean time_trig = (Boolean) smartSocketDoc.get("time_trig");
                        ss_update.put("time_trig", !time_trig);
                        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC")).update(ss_update);
                    }
                });
            }
        });

        ssMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==4){
                    System.out.println("Time Schedule mode selected");
                    ssThreshold.setVisibility(View.GONE);
                    ssUpDown.setVisibility(View.GONE);
                    thresholdText.setVisibility(View.GONE);
                    upDownText.setVisibility(View.GONE);
                    timeOnPickerText.setVisibility(View.VISIBLE);
                    timeOffPickerText.setVisibility(View.VISIBLE);
                    timeOnText.setVisibility(View.VISIBLE);
                    timeOffText.setVisibility(View.VISIBLE);
                } else {
                    ssThreshold.setVisibility(View.VISIBLE);
                    ssUpDown.setVisibility(View.VISIBLE);
                    thresholdText.setVisibility(View.VISIBLE);
                    upDownText.setVisibility(View.VISIBLE);
                    timeOnPickerText.setVisibility(View.GONE);
                    timeOffPickerText.setVisibility(View.GONE);
                    timeOnText.setVisibility(View.GONE);
                    timeOffText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        timeOnPickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SmartSocketActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHourString;
                        String selectedMinuteString;
                        if (selectedHour < 10) {
                            selectedHourString = "0"+selectedHour;
                        } else {
                            selectedHourString = Integer.toString(selectedHour);
                        }
                        if (selectedMinute < 10) {
                            selectedMinuteString = "0"+selectedMinute;
                        } else {
                            selectedMinuteString = Integer.toString(selectedMinute);
                        }
                        timeOnPickerText.setText( selectedHourString + ":" + selectedMinuteString);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        timeOffPickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SmartSocketActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHourString;
                        String selectedMinuteString;
                        if (selectedHour < 10) {
                            selectedHourString = "0"+selectedHour;
                        } else {
                            selectedHourString = Integer.toString(selectedHour);
                        }
                        if (selectedMinute < 10) {
                            selectedMinuteString = "0"+selectedMinute;
                        } else {
                            selectedMinuteString = Integer.toString(selectedMinute);
                        }
                        timeOffPickerText.setText( selectedHourString + ":" + selectedMinuteString);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }

    public void deleteSS(View view) {
        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC")).delete();
        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_states").document(intent.getStringExtra("MAC"))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(SmartSocketActivity.this, HubActivity.class);
                        intent.putExtra("hubCode", hubCode);
                        intent.putExtra("hubId", hubId);
                        startActivity(intent);
                    }
                });

        firebaseFirestore.collection("users").document(userID).collection("hubs").document(hubCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        long count = (long) documentSnapshot.get("count");

                        Map<String, Object> data = new HashMap<String, Object>();
                        data.put("count", count-1);
                        firebaseFirestore.collection("users").document(userID).collection("hubs").document(hubCode).update(data);
                    }
                });

        firebaseFirestore.collection("hubs").document(hubCode).collection("groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot groups = task.getResult();

                        for( DocumentSnapshot group : groups) {
                            ArrayList<String> ssMACs = (ArrayList<String>) group.get("SmartSockets");
                            if (ssMACs.contains(intent.getStringExtra("MAC"))) {
                                ssMACs.remove(intent.getStringExtra("MAC"));
                                long count = (long) group.get("count");

                                Map<String, Object> data = new HashMap<String, Object>();
                                data.put("count", count-1);
                                data.put("SmartSockets", ssMACs);

                                firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(group.getId()).update(data);
                            }
                        }
                    }
                });
    }

    public void saveControl(View view) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (ssMode.getSelectedItemPosition() == 4) {
            data.put("time_on", timeOnPickerText.getText());
            data.put("time_off", timeOffPickerText.getText());
        } else {
            data.put("mode", ssMode.getSelectedItemPosition());
            data.put("threshold", Integer.parseInt(ssThreshold.getText().toString()));
            if(ssUpDown.getSelectedItemPosition() == 0) {
                data.put("up_down", true);
            } else {
                data.put("up_down", false);
            }
        }

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(intent.getStringExtra("MAC")).update(data);
    }
}
