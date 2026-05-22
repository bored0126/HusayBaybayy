package com.example.husaybaybay.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husaybaybay.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageSectionsActivity extends AppCompatActivity {

    private EditText etSectionName;
    private Button btnAddSection;
    private RecyclerView rvSections;
    private TextView tvEmptySections;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private ListenerRegistration sectionsListener;
    private List<String> sectionList = new ArrayList<>();
    private List<String> sectionIds = new ArrayList<>();
    private SectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sections);

        db = FirebaseFirestore.getInstance();

        etSectionName = findViewById(R.id.etSectionName);
        btnAddSection = findViewById(R.id.btnAddSection);
        rvSections = findViewById(R.id.rvSections);
        tvEmptySections = findViewById(R.id.tvEmptySections);
        btnBack = findViewById(R.id.btnBack);

        adapter = new SectionAdapter();
        rvSections.setLayoutManager(new LinearLayoutManager(this));
        rvSections.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnAddSection.setOnClickListener(v -> addSection());

        startSectionsListener();
    }

    private void startSectionsListener() {
        sectionsListener = db.collection("sections")
                .orderBy("name")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (snapshots != null) {
                        sectionList.clear();
                        sectionIds.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            sectionList.add(doc.getString("name"));
                            sectionIds.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();

                        if (sectionList.isEmpty()) {
                            tvEmptySections.setVisibility(View.VISIBLE);
                            rvSections.setVisibility(View.GONE);
                        } else {
                            tvEmptySections.setVisibility(View.GONE);
                            rvSections.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void addSection() {
        String name = etSectionName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Ilagay ang pangalan ng seksyon.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> section = new HashMap<>();
        section.put("name", name);

        db.collection("sections").add(section)
                .addOnSuccessListener(documentReference -> {
                    etSectionName.setText("");
                    Toast.makeText(this, "Seksyon naidagdag!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteSection(String id) {
        db.collection("sections").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Seksyon na-delete.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmDeleteSection(String id, String name) {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("I-delete ang Seksyon?")
                .setMessage("Sigurado ka ba na nais mong i-delete ang seksyong '" + name + "'?")
                .setPositiveButton("I-delete", (d, which) -> deleteSection(id))
                .setNegativeButton("Kanselahin", null)
                .create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFE74C3C); // destructive red
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF5A4A42); // theme brown
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sectionsListener != null) {
            sectionsListener.remove();
        }
    }

    private class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = sectionList.get(position);
            String id = sectionIds.get(position);
            holder.tvName.setText(name);
            holder.btnDelete.setOnClickListener(v -> confirmDeleteSection(id, name));
        }

        @Override
        public int getItemCount() {
            return sectionList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageButton btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvSectionName);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
