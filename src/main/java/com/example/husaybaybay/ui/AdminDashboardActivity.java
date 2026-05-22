package com.example.husaybaybay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husaybaybay.R;
import com.example.husaybaybay.data.model.DictionaryRepository;
import com.example.husaybaybay.data.model.PlayerScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView rvPlayers;
    private TextView tvAdminName, tvTotalPlayers, tvTotalWords, tvEmpty;
    private Button btnLogout, btnManageSections;
    private Spinner spinnerFilterSection;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration playersListener;

    private List<PlayerScore> playerList = new ArrayList<>();
    private List<PlayerScore> filteredPlayerList = new ArrayList<>();
    private List<String> sectionsList = new ArrayList<>();
    private PlayerScoreAdapter adapter;
    private int totalDictionaryWords;
    private String selectedSectionFilter = "Lahat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        totalDictionaryWords = DictionaryRepository.getDictionaryWords().size();

        rvPlayers = findViewById(R.id.rvPlayers);
        tvAdminName = findViewById(R.id.tvAdminName);
        tvTotalPlayers = findViewById(R.id.tvTotalPlayers);
        tvTotalWords = findViewById(R.id.tvTotalWords);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnLogout = findViewById(R.id.btnLogout);
        btnManageSections = findViewById(R.id.btnManageSections);
        spinnerFilterSection = findViewById(R.id.spinnerFilterSection);

        tvTotalWords.setText(String.valueOf(totalDictionaryWords));

        // Apply scale animations to buttons
        com.example.husaybaybay.util.AnimationHelper.applyScaleAnimation(btnLogout);
        com.example.husaybaybay.util.AnimationHelper.applyScaleAnimation(btnManageSections);

        adapter = new PlayerScoreAdapter(filteredPlayerList, totalDictionaryWords);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(adapter);

        // Show admin name
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("name");
                            tvAdminName.setText("Welcome, " + (name != null ? name : "Admin"));
                        }
                    });
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnManageSections.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageSectionsActivity.class));
        });

        loadSections();

        // Start real-time listener for player data
        startPlayersListener();
    }

    private void loadSections() {
        db.collection("sections").orderBy("name").addSnapshotListener((snapshots, error) -> {
            if (error != null) return;
            if (snapshots != null) {
                sectionsList.clear();
                sectionsList.add("Lahat"); // "All"
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    sectionsList.add(doc.getString("name"));
                }
                
                ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, 
                        R.layout.item_spinner_selected, sectionsList);
                sectionAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinnerFilterSection.setAdapter(sectionAdapter);
                
                spinnerFilterSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedSectionFilter = sectionsList.get(position);
                        applyFilter();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void applyFilter() {
        filteredPlayerList.clear();
        if (selectedSectionFilter.equals("Lahat")) {
            filteredPlayerList.addAll(playerList);
        } else {
            for (PlayerScore p : playerList) {
                if (selectedSectionFilter.equals(p.getSection())) {
                    filteredPlayerList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        tvTotalPlayers.setText(String.valueOf(filteredPlayerList.size()));
        if (filteredPlayerList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPlayers.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPlayers.setVisibility(View.VISIBLE);
        }
    }

    private void startPlayersListener() {
        playersListener = db.collection("users")
                .whereEqualTo("role", "player")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (snapshots != null) {
                        playerList.clear();

                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            PlayerScore player = new PlayerScore();
                            player.setUid(doc.getId());
                            player.setName(doc.getString("name"));
                            player.setEmail(doc.getString("email"));

                            // Game 1 scores
                            Long g1Last = doc.getLong("game1LastScore");
                            Long g1High = doc.getLong("game1HighScore");
                            player.setGame1LastScore(g1Last != null ? g1Last.intValue() : 0);
                            player.setGame1HighScore(g1High != null ? g1High.intValue() : 0);

                            // Game 2 words completed
                            Long g2Words = doc.getLong("game2WordsCompleted");
                            player.setGame2WordsCompleted(g2Words != null ? g2Words.intValue() : 0);

                            // Game 3 words completed
                            Long g3Words = doc.getLong("game3WordsCompleted");
                            player.setGame3WordsCompleted(g3Words != null ? g3Words.intValue() : 0);

                            player.setSection(doc.getString("section"));

                            playerList.add(player);
                        }

                        applyFilter();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playersListener != null) {
            playersListener.remove();
        }
    }
}
