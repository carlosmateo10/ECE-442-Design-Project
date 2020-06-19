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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GroupActivity extends AppCompatActivity implements EditGroupDialogFragment.EditGroupDialogListener{

    private TextView timeOnPickerText, timeOffPickerText, timeOnText, timeOffText, groupID, groupCount, groupPower, thresholdText, upDownText;
    private Button groupSwitch, groupTimeSchedule;
    private Intent intent;
    private FirebaseFirestore firebaseFirestore;
    private EditText groupThreshold;
    private Spinner groupMode, groupUpDown;
    private String hubCode, hubId;
    private ArrayList<String> ssMACs;
    private EditGroupDialogFragment editGroupDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupID = findViewById(R.id.group_id);
        groupCount = findViewById(R.id.group_count);
        groupPower = findViewById(R.id.group_power);
        groupSwitch = findViewById(R.id.group_swith);
        groupThreshold = findViewById(R.id.group_threshold);
        groupMode = findViewById(R.id.mode_spinner);
        groupUpDown = findViewById(R.id.up_down_spinner);
        thresholdText = findViewById(R.id.textView_threshold);
        upDownText = findViewById(R.id.textView_up_down);
        timeOnPickerText = findViewById(R.id.timeOnPicker);
        timeOffPickerText = findViewById(R.id.timeOffPicker);
        timeOnText = findViewById(R.id.textView_time_on);
        timeOffText = findViewById(R.id.textView_time_off);
        groupTimeSchedule = findViewById(R.id.time_trig_btn);

        editGroupDialogFragment = new EditGroupDialogFragment();

        intent = getIntent();

        ssMACs = new ArrayList<>();

        groupID.setText(intent.getStringExtra("groupID"));
        groupCount.setText(intent.getIntExtra("count", 0)+" Smarts Sockets");

        hubCode = intent.getStringExtra("hubCode");
        hubId = intent.getStringExtra("hubId");

        System.out.println(hubCode);
        System.out.println(intent.getStringExtra("groupID"));

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot group = task.getResult();
                        groupMode.setSelection((int)(long)group.get("mode"));
                        groupThreshold.setText(group.get("threshold").toString());
                        if((boolean)group.get("up_down") == true) {
                            groupUpDown.setSelection(0);
                        } else {
                            groupUpDown.setSelection(1);
                        }
                        if(group.get("time_on").toString()=="") {
                            timeOnPickerText.setText("SELECT TIME ON");
                        } else {
                            timeOnPickerText.setText(group.get("time_on").toString());
                        }
                        if(group.get("time_off").toString()=="") {
                            timeOffPickerText.setText("SELECT TIME OFF");
                        } else {
                            timeOffPickerText.setText(group.get("time_off").toString());
                        }
                        ssMACs = (ArrayList<String>)group.get("SmartSockets");
                    }
                });

        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        groupCount.setText(documentSnapshot.get("count")+" Smarts Sockets");
                    }
                });

        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()) {
                            if((boolean)documentSnapshot.get("time_trig") == true) {
                                groupTimeSchedule.setText("TIME SCHEDULE ON");
                            } else {
                                groupTimeSchedule.setText("TIME SCHEDULE OFF");
                            }
                        }
                    }
                });

        groupSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String ss : ssMACs) {
                    System.out.println("Smart Socket SWITCH " + ss);
                    final Map<String, Object> ss_update = new HashMap<>();
                    final String MAC = ss;

                    firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(MAC)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot smartSocketDoc = task.getResult();
                            Boolean ext_trig = (Boolean) smartSocketDoc.get("ext_trig");
                            ss_update.put("ext_trig", !ext_trig);
                            firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(MAC).update(ss_update);
                        }
                    });
                }
            }
        });

        groupTimeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, Object> ss_update = new HashMap<>();
                firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID"))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot smartSocketDoc = task.getResult();
                        Boolean time_trig = (Boolean) smartSocketDoc.get("time_trig");
                        ss_update.put("time_trig", !time_trig);
                        for (String ss : ssMACs) {
                            System.out.println("Smart Socket TIME SCHEDULE " + ss);
                            final Map<String, Object> ss_update = new HashMap<>();
                            final String MAC = ss;
                            ss_update.put("time_trig", !time_trig);
                            firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(MAC).update(ss_update);
                        }
                        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID")).update(ss_update);
                    }
                });
            }
        });

        groupMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==4){
                    System.out.println("Time Schedule mode selected");
                    groupThreshold.setVisibility(View.GONE);
                    groupUpDown.setVisibility(View.GONE);
                    thresholdText.setVisibility(View.GONE);
                    upDownText.setVisibility(View.GONE);
                    timeOnPickerText.setVisibility(View.VISIBLE);
                    timeOffPickerText.setVisibility(View.VISIBLE);
                    timeOnText.setVisibility(View.VISIBLE);
                    timeOffText.setVisibility(View.VISIBLE);
                } else {
                    groupThreshold.setVisibility(View.VISIBLE);
                    groupUpDown.setVisibility(View.VISIBLE);
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
                mTimePicker = new TimePickerDialog(GroupActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(GroupActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    public void modifyGroup(View view) {
        Bundle args = new Bundle();
        args.putString("hubId", hubId);
        args.putString("hubCode", hubCode);
        args.putString("groupId", groupID.getText().toString());
        args.putStringArrayList("ssMACs", ssMACs);
        editGroupDialogFragment.setArguments(args);
        editGroupDialogFragment.show(getSupportFragmentManager(), "EditGroupDialogFragment");
    }

    public void deleteGroup(View view) {
        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID"))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(GroupActivity.this, HubActivity.class);
                        intent.putExtra("hubCode", hubCode);
                        intent.putExtra("hubId", hubId);
                        startActivity(intent);
                    }
                });
    }

    public void saveControl(View view) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (groupMode.getSelectedItemPosition() == 4) {
            data.put("time_on", timeOnPickerText.getText());
            data.put("time_off", timeOffPickerText.getText());
        } else {
            data.put("mode", groupMode.getSelectedItemPosition());
            data.put("threshold", Integer.parseInt(groupThreshold.getText().toString()));
            if(groupUpDown.getSelectedItemPosition() == 0) {
                data.put("up_down", true);
            } else {
                data.put("up_down", false);
            }
        }
        for (String ss : ssMACs) {
            firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(ss).update(data);
        }
        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID")).update(data);
    }

    @Override
    public void editGroupDialogResult(ArrayList<String> ssMACs) {
        Map<String, Object> group = new HashMap<>();
        group.put("SmartSockets", ssMACs);
        group.put("count", ssMACs.size());

        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(intent.getStringExtra("groupID")).update(group);
    }
}
