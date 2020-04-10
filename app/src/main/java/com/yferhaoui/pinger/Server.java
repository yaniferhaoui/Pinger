package com.yferhaoui.pinger;

import android.database.Cursor;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {

    private String name;

    private String ip;
    private String ipMac;

    private Integer internalPort;
    private Integer externalPort;

    private Long expiryDate;

    private Boolean isPublic;

    private long nextTimePing = 0;
    private boolean isOnline = false;
    private long pingEveryXmilli = 5000;


    public Server(String ip, String name, String ipMac, Integer internalPort, Integer externalPort, Long expiryDate, Boolean isPublic) {
        this.ip = ip;
        this.name = name;
        this.ipMac = ipMac;
        this.internalPort = internalPort;
        this.externalPort = externalPort;
        this.expiryDate = expiryDate;
        this.isPublic = isPublic;
    }

    public Server(Cursor res) {
        this.ip = res.getString(res.getColumnIndex("ip"));
        this.name = res.getString(res.getColumnIndex("name"));
        this.ipMac = res.getString(res.getColumnIndex("ipMac"));
        this.internalPort = res.getInt(res.getColumnIndex("internalPort"));
        this.externalPort = res.getInt(res.getColumnIndex("externalPort"));
        this.expiryDate = res.getLong(res.getColumnIndex("expiryDate"));
        this.isPublic = res.getInt(res.getColumnIndex("isPublic")) > 0;
    }


    public void sendPingRequest() {
        try {
            InetAddress geek = InetAddress.getByName(this.ip);

            if (geek.getHostName() != null && geek.getHostAddress() != null) {
                this.isOnline = true;
            } else {
                this.isOnline = false;
            }
        } catch (UnknownHostException e) {
            //e.printStackTrace();
            this.isOnline = false;
        } finally {
            this.nextTimePing = System.currentTimeMillis() + this.pingEveryXmilli;
        }
    }

    public boolean timeToPing() {
        return this.nextTimePing < System.currentTimeMillis();
    }

    public String getName() {
        return this.name;
    }

    public String getIp() {
        return this.ip;
    }

    public String getIpMac() { return this.ipMac; }

    public Integer getExternalPort() { return this.externalPort; }

    public Integer getInternalPort() { return this.internalPort; }

    public boolean isPublic() { return this.isPublic; }

    public Long getExpiryDate() { return this.expiryDate; }

    public boolean isOnline() {
        return isOnline;
    }

    public String getState() {
        if (this.isOnline()) {
            return "Online";
        } return "Offline";
    }



}
