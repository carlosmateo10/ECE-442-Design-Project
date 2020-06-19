package es.upm.dit.ece442designproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AddHubDialogFragment.AddHubDialogListener, HubAdapter.OnHubListClick {

    private FirebaseFirestore firebaseFirestore;
    private AddHubDialogFragment addHubDialogFragment;
    private String userId;
    private HubAdapter adapter;
    private RecyclerView hubsList;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addHubDialogFragment = new AddHubDialogFragment();

        firebaseFirestore = FirebaseFirestore.getInstance();
        hubsList = findViewById(R.id.hubsList);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        logoutBtn  = findViewById(R.id.logout_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        //Define Layout
        hubsList.setLayoutManager(new LinearLayoutManager(this));

        Query query = firebaseFirestore.collection("users").document(userId).collection("hubs");

        // RecyclerOptions
        FirestoreRecyclerOptions<Hub> options = new FirestoreRecyclerOptions.Builder<Hub>()
                .setLifecycleOwner(this)
                .setQuery(query, Hub.class)
                .build();
        adapter = new HubAdapter(options, this);
        hubsList.setAdapter(adapter);
    }


    public void addHub(View view) {
        addHubDialogFragment.show(getSupportFragmentManager(), "AddHubDialogFragment");
    }

    @Override
    public void addHubDialogResult(String id, String code) {
        Map<String, Object> data_hub = new HashMap<String, Object>();

        data_hub.put("id", id);
        data_hub.put("code", code);
        data_hub.put("count", 0);

        firebaseFirestore.collection("users").document(userId).collection("hubs").document(code).set(data_hub);
    }

    @Override
    public void onHubClick(Hub hub) {
        Intent intent = new Intent(MainActivity.this, HubActivity.class);
        intent.putExtra("hubCode", hub.getCode());
        intent.putExtra("hubId", hub.getId());
        startActivity(intent);
    }
}
