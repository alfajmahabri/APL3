package com.example.monitoring;

import java.util.UUID;

public class VM {
    private String id;
    private String title;
    private String ipAddress;
    private String port; // Optional
    private String description;

    public VM() {
        this.id = UUID.randomUUID().toString();
    }

    public VM(String title, String ipAddress, String port, String description) {
        this();
        this.title = title;
        this.ipAddress = ipAddress;
        this.port = port;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
