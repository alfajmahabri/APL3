package com.example.monitoring;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddVMActivity extends AppCompatActivity {

    private EditText etTitle, etIpAddress, etPort, etDescription;
    private Button btnSave;
    private StorageHelper storageHelper;
    private String editVmId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        etTitle = findViewById(R.id.etTitle);
        etIpAddress = findViewById(R.id.etIpAddress);
        etPort = findViewById(R.id.etPort);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        storageHelper = new StorageHelper(this);

        // Check if we are in Edit mode
        if (getIntent().hasExtra("vm_id")) {
            editVmId = getIntent().getStringExtra("vm_id");
            etTitle.setText(getIntent().getStringExtra("vm_title"));
            etIpAddress.setText(getIntent().getStringExtra("vm_ip"));
            etPort.setText(getIntent().getStringExtra("vm_port"));
            etDescription.setText(getIntent().getStringExtra("vm_desc"));
            btnSave.setText("Update Server");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Server");
            }
        }

        btnSave.setOnClickListener(v -> saveVM());
    }

    private void saveVM() {
        String title = etTitle.getText().toString().trim();
        String ip = etIpAddress.getText().toString().trim();
        String port = etPort.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (title.isEmpty() || ip.isEmpty()) {
            Toast.makeText(this, "Please fill Title and IP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editVmId == null) {
            VM vm = new VM(title, ip, port, desc);
            storageHelper.addVM(vm);
            Toast.makeText(this, "Server Added Successfully", Toast.LENGTH_SHORT).show();
        } else {
            VM vm = new VM();
            vm.setId(editVmId);
            vm.setTitle(title);
            vm.setIpAddress(ip);
            vm.setPort(port);
            vm.setDescription(desc);
            storageHelper.updateVM(vm);
            Toast.makeText(this, "Server Updated Successfully", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
