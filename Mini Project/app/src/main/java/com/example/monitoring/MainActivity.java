package com.example.monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements VMAdapter.OnItemClickListener {

    private RecyclerView rvVms;
    private VMAdapter vmAdapter;
    private FloatingActionButton fabAdd;
    private LinearLayout llEmptyState;
    private StorageHelper storageHelper;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final int REQUEST_CODE_ADD_VM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvVms = findViewById(R.id.rvVms);
        fabAdd = findViewById(R.id.fabAdd);
        llEmptyState = findViewById(R.id.llEmptyState);

        rvVms.setLayoutManager(new LinearLayoutManager(this));
        vmAdapter = new VMAdapter(new ArrayList<>(), this);
        rvVms.setAdapter(vmAdapter);

        storageHelper = new StorageHelper(this);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddVMActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_VM);
        });

        loadVMs();
    }

    @Override
    public void onEdit(VM vm) {
        Intent intent = new Intent(this, AddVMActivity.class);
        intent.putExtra("vm_id", vm.getId());
        intent.putExtra("vm_title", vm.getTitle());
        intent.putExtra("vm_ip", vm.getIpAddress());
        intent.putExtra("vm_port", vm.getPort());
        intent.putExtra("vm_desc", vm.getDescription());
        startActivityForResult(intent, REQUEST_CODE_ADD_VM);
    }

    @Override
    public void onDelete(VM vm) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Delete Instance")
                .setMessage("Remove '" + vm.getTitle() + "' from monitoring?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    storageHelper.deleteVM(vm.getId());
                    loadVMs();
                })
                .setNegativeButton("Keep", null)
                .show();
    }

    private void loadVMs() {
        List<VM> vms = storageHelper.getAllVMs();
        vmAdapter.setVmList(vms);
        
        if (vms.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvVms.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvVms.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadVMs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vmAdapter.shutdown();
        executorService.shutdown();
    }
}
