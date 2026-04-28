package com.example.monitoring;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VMAdapter extends RecyclerView.Adapter<VMAdapter.VMViewHolder> {

    public interface OnItemClickListener {
        void onEdit(VM vm);
        void onDelete(VM vm);
    }

    private List<VM> vmList;
    private OnItemClickListener listener;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public VMAdapter(List<VM> vmList, OnItemClickListener listener) {
        this.vmList = vmList;
        this.listener = listener;
    }

    public void setVmList(List<VM> vmList) {
        this.vmList = vmList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vm, parent, false);
        return new VMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VMViewHolder holder, int position) {
        VM vm = vmList.get(position);
        holder.tvTitle.setText(vm.getTitle());
        holder.tvIpPort.setText(vm.getIpAddress());
        
        // Default "Acquiring" state
        holder.tvStatusText.setText("ACQUIRING DATA");
        holder.tvStatusText.setTextColor(Color.parseColor("#64748B")); // slate_500
        updateStatusIndicator(holder.vStatusIndicator, Color.parseColor("#F1F5F9")); // slate_100
        holder.ivServerIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#0F172A"))); // slate_900

        checkStatus(vm, holder);

        holder.ivOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.ivOptions);
            popup.getMenu().add("Modify Configuration");
            popup.getMenu().add("Purge from Registry");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().toString().contains("Modify")) {
                    listener.onEdit(vm);
                } else {
                    listener.onDelete(vm);
                }
                return true;
            });
            popup.show();
        });
    }

    private void checkStatus(VM vm, VMViewHolder holder) {
        executorService.execute(() -> {
            boolean isLive = false;
            String ip = vm.getIpAddress();
            
            try {
                if (vm.getPort() != null && !vm.getPort().isEmpty()) {
                    isLive = isPortOpen(ip, Integer.parseInt(vm.getPort()), 4000);
                } else {
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        isLive = address.isReachable(4000);
                    } catch (Exception ignored) {}
                    if (!isLive) isLive = executePing(ip);
                }
            } catch (Exception e) {
                isLive = false;
            }

            final boolean finalIsLive = isLive;
            mainHandler.post(() -> {
                if (finalIsLive) {
                    holder.tvStatusText.setText("SYSTEM OPERATIONAL");
                    holder.tvStatusText.setTextColor(Color.parseColor("#10B981")); // cyber_green
                    updateStatusIndicator(holder.vStatusIndicator, Color.parseColor("#10B981"));
                    holder.ivServerIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#10B981")));
                } else {
                    holder.tvStatusText.setText("CONNECTION FAILED");
                    holder.tvStatusText.setTextColor(Color.parseColor("#FF4B4B")); // cyber_red
                    updateStatusIndicator(holder.vStatusIndicator, Color.parseColor("#FF4B4B"));
                    holder.ivServerIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF4B4B")));
                }
            });
        });
    }

    private boolean isPortOpen(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean executePing(String ip) {
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 -w 4 " + ip);
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void updateStatusIndicator(View view, int color) {
        GradientDrawable background = (GradientDrawable) view.getBackground();
        background.setColor(color);
    }

    @Override
    public int getItemCount() {
        return vmList.size();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    static class VMViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvIpPort, tvDescription, tvStatusText;
        View vStatusIndicator;
        ImageView ivOptions, ivServerIcon;

        public VMViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvIpPort = itemView.findViewById(R.id.tvIpPort);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatusText = itemView.findViewById(R.id.tvStatusText);
            vStatusIndicator = itemView.findViewById(R.id.vStatusIndicator);
            ivOptions = itemView.findViewById(R.id.ivOptions);
            ivServerIcon = itemView.findViewById(R.id.ivServerIcon);
        }
    }
}
