package es.upm.dit.ece442designproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SmartSocketAdapter.OnSmartSocketListClick, SmartSocketAdapter.OnSmartSocketSwitchClick, AddSSDialogFragment.AddSSDialogListener {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView smartSocketsList;
    private SmartSocketAdapter adapter;
    private String userId;
    private Button logoutBtn;
    private AddSSDialogFragment addSSDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSSDialogFragment = new AddSSDialogFragment();

        firebaseFirestore = FirebaseFirestore.getInstance();
        smartSocketsList = findViewById(R.id.ssList);
        logoutBtn  = findViewById(R.id.logout_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Define Layout
        smartSocketsList.setLayoutManager(new LinearLayoutManager(this));

       Query query = firebaseFirestore.collection("users").document(userId).collection("sockets_states");

        // RecyclerOptions
        FirestoreRecyclerOptions<SmartSocket> options = new FirestoreRecyclerOptions.Builder<SmartSocket>()
                .setLifecycleOwner(this)
                .setQuery(query, SmartSocket.class)
                .build();
        adapter = new SmartSocketAdapter(options, this, this, this);
        smartSocketsList.setAdapter(adapter);
    }

    @Override
    public void onSmartSocketClick(SmartSocket smartSocket) {
        Intent intent = new Intent(MainActivity.this, SmartSocketActivity.class);
        intent.putExtra("MAC", smartSocket.getMAC());
        intent.putExtra("ID", smartSocket.getId());
        startActivity(intent);
    }

    public void onSmartSocketSwitch(SmartSocket smartSocket) {
        System.out.println("Smart Socket SWITCH " + smartSocket.getMAC());
        final Map<String, Object> ss_update = new HashMap<>();
        final String MAC = smartSocket.getMAC();

        firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(smartSocket.getMAC())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot smartSocketDoc = task.getResult();
                    Boolean ext_trig = (Boolean) smartSocketDoc.get("ext_trig");
                    ss_update.put("ext_trig", !ext_trig);
                    firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(MAC).update(ss_update);

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

        firebaseFirestore.collection("users").document(userId).collection("sockets_states").document(MAC).set(data_state);

        Map<String, Object> data_control = new HashMap<String, Object>();

        data_control.put("MAC", MAC);
        data_control.put("PIN", PIN);
        data_control.put("mode", 0);
        data_control.put("threshold", 800);
        data_control.put("ext_trig", false);
        data_control.put("up_down", false);

        firebaseFirestore.collection("users").document(userId).collection("sockets_control").document(MAC).set(data_control);
    }
}