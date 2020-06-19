package es.upm.dit.ece442designproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HubActivity extends AppCompatActivity implements SmartSocketAdapter.OnSmartSocketListClick, SmartSocketAdapter.OnSmartSocketSwitchClick, AddSSDialogFragment.AddSSDialogListener, AddGroupDialogFragment.AddGroupDialogListener, GroupAdapter.OnGroupListClick {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView hubRecyclerView;
    private SmartSocketAdapter smartSocketsAdapter;
    private GroupAdapter groupsAdapter;
    private Button logoutBtn;
    private AddSSDialogFragment addSSDialogFragment;
    private AddGroupDialogFragment addGroupDialogFragment;
    private Intent intent;
    private String hubCode;
    private String hubId;
    private TextView hubIdText;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        addSSDialogFragment = new AddSSDialogFragment();
        addGroupDialogFragment = new AddGroupDialogFragment();

        firebaseFirestore = FirebaseFirestore.getInstance();
        hubRecyclerView = findViewById(R.id.hub_recyclerView);
        logoutBtn  = findViewById(R.id.logout_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HubActivity.this, LoginActivity.class));
            }
        });

        intent = getIntent();
        hubCode = intent.getStringExtra("hubCode");
        hubId = intent.getStringExtra("hubId");

        hubIdText = findViewById(R.id.hub_id);
        hubIdText.setText(hubId);

        userID = FirebaseAuth.getInstance().getUid();

        //Define Layout
        hubRecyclerView.setLayoutManager(new LinearLayoutManager(this));

       // Smart Sockets query
        Query querySmartSockets = firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_states");

        // RecyclerOptions
        FirestoreRecyclerOptions<SmartSocket> optionsSmartSockets = new FirestoreRecyclerOptions.Builder<SmartSocket>()
                .setLifecycleOwner(this)
                .setQuery(querySmartSockets, SmartSocket.class)
                .build();
        smartSocketsAdapter = new SmartSocketAdapter(optionsSmartSockets, this, this, this);

        // Groups query
        Query queryGroups = firebaseFirestore.collection("hubs").document(hubCode).collection("groups");

        // RecyclerOptions
        FirestoreRecyclerOptions<Group> optionsGroups = new FirestoreRecyclerOptions.Builder<Group>()
                .setLifecycleOwner(this)
                .setQuery(queryGroups, Group.class)
                .build();
        groupsAdapter = new GroupAdapter(optionsGroups, this);

        hubRecyclerView.setAdapter(smartSocketsAdapter);
    }

    @Override
    public void onSmartSocketClick(SmartSocket smartSocket) {
        Intent intent = new Intent(HubActivity.this, SmartSocketActivity.class);
        intent.putExtra("MAC", smartSocket.getMAC());
        intent.putExtra("ID", smartSocket.getId());
        intent.putExtra("hubCode", hubCode);
        startActivity(intent);
    }

    public void onSmartSocketSwitch(SmartSocket smartSocket) {
        System.out.println("Smart Socket SWITCH " + smartSocket.getMAC());
        final Map<String, Object> ss_update = new HashMap<>();
        final String MAC = smartSocket.getMAC();

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(smartSocket.getMAC())
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

    public void addSS(View view) {
        addSSDialogFragment.show(getSupportFragmentManager(), "AddSSDialogFragment");
    }

    @Override
    public void addSSDialogResult(String id, String MAC, String PIN) {
        Map<String, Object> data_state = new HashMap<String, Object>();

        data_state.put("Id", id);
        data_state.put("MAC", MAC);
        data_state.put("PIN", PIN);
        data_state.put("power", 0);
        data_state.put("power_safety", true);
        data_state.put("state", false);
        data_state.put("manual_block", false);

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_states").document(MAC).set(data_state);

        Map<String, Object> data_control = new HashMap<String, Object>();

        data_control.put("MAC", MAC);
        data_control.put("PIN", PIN);
        data_control.put("mode", 0);
        data_control.put("threshold", 800);
        data_control.put("ext_trig", false);
        data_control.put("up_down", false);
        data_control.put("time_trig", false);
        data_control.put("time_on", "");
        data_control.put("time_off", "");

        firebaseFirestore.collection("hubs").document(hubCode).collection("sockets_control").document(MAC).set(data_control);

        firebaseFirestore.collection("users").document(userID).collection("hubs").document(hubCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot hub = task.getResult();
                Map<String, Object> hub_update = new HashMap<>();
                long count = (long) hub.get("count");
                System.out.println("HAY" + count + " SMART SOCKETS");
                hub_update.put("count", count+1);
                firebaseFirestore.collection("users").document(userID).collection("hubs").document(hubCode).update(hub_update);
            }
        });
    }

    public void addGroup(View view) {
        Bundle args = new Bundle();
        args.putString("hubId", hubId);
        args.putString("hubCode", hubCode);
        addGroupDialogFragment.setArguments(args);
        addGroupDialogFragment.show(getSupportFragmentManager(), "AddGroupDialogFragment");
    }

    @Override
    public void addGroupDialogResult(String id, ArrayList<String> ssMACs) {
        Map<String, Object> group = new HashMap<>();
        group.put("SmartSockets", ssMACs);
        group.put("count", ssMACs.size());
        group.put("id", id);
        group.put("mode", 0);
        group.put("threshold", 800);
        group.put("ext_trig", false);
        group.put("up_down", false);
        group.put("time_trig", false);
        group.put("time_on", "");
        group.put("time_off", "");

        firebaseFirestore.collection("hubs").document(hubCode).collection("groups").document(id).set(group);
    }

    @Override
    public void onGroupClick(Group group) {
        System.out.println("GROUP: "+group.getId());
        Intent intent = new Intent(HubActivity.this, GroupActivity.class);
        intent.putExtra("groupID", group.getId());
        intent.putExtra("count", group.getCount());
        intent.putExtra("hubCode", hubCode);
        intent.putExtra("hubId", hubId);
        startActivity(intent);
    }

    public void loadGroups(View view) {
        hubRecyclerView.setAdapter(groupsAdapter);
    }

    public void loadSmartSockets(View view) {
        hubRecyclerView.setAdapter(smartSocketsAdapter);
    }
}