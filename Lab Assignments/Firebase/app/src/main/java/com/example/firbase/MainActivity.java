package com.example.firbase;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private TextInputEditText etName, etEmail;
    private Button btnSave;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;

    private FirebaseFirestore db;
    private CollectionReference usersRef;

    private String selectedUserId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerView);

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveUser());

        // Listen for real-time updates
        listenForUpdates();
    }

    private void listenForUpdates() {
        usersRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                userList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    User user = doc.toObject(User.class);
                    user.setId(doc.getId());
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(name, email);

        if (selectedUserId == null) {
            // Create
            usersRef.add(user)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Update
            usersRef.document(selectedUserId).set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void clearFields() {
        etName.setText("");
        etEmail.setText("");
        selectedUserId = null;
        btnSave.setText("Save User");
    }

    @Override
    public void onEditClick(User user) {
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        selectedUserId = user.getId();
        btnSave.setText("Update User");
    }

    @Override
    public void onDeleteClick(User user) {
        usersRef.document(user.getId()).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}