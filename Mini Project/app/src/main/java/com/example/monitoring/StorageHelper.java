package com.example.monitoring;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageHelper {
    private static final String PREF_NAME = "monitoring_prefs";
    private static final String KEY_VMS = "vms_list";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public StorageHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<VM> getAllVMs() {
        String json = sharedPreferences.getString(KEY_VMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<VM>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveVMs(List<VM> vms) {
        String json = gson.toJson(vms);
        sharedPreferences.edit().putString(KEY_VMS, json).apply();
    }

    public void addVM(VM vm) {
        List<VM> vms = getAllVMs();
        vms.add(vm);
        saveVMs(vms);
    }

    public void updateVM(VM updatedVm) {
        List<VM> vms = getAllVMs();
        for (int i = 0; i < vms.size(); i++) {
            if (vms.get(i).getId().equals(updatedVm.getId())) {
                vms.set(i, updatedVm);
                break;
            }
        }
        saveVMs(vms);
    }

    public void deleteVM(String id) {
        List<VM> vms = getAllVMs();
        vms.removeIf(vm -> vm.getId().equals(id));
        saveVMs(vms);
    }
}
