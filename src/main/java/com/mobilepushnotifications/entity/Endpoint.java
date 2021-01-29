package com.mobilepushnotifications.entity;

import javax.persistence.*;

@Entity
@Table(name = "endpoint")
public class Endpoint {

    @Id
    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "platform", nullable = false)
    private String platform;

    @Column(name = "endpoint_arn", nullable = false)
    private String endpointArn;

    @Column(name = "last_update_time", nullable = false)
    private long lastUpdateTime;

    public Endpoint() {
    }

    public Endpoint(String deviceToken, int userId, String platform, String endpointArn, long lastUpdateTime) {
        this.deviceToken = deviceToken;
        this.userId = userId;
        this.platform = platform;
        this.endpointArn = endpointArn;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getEndpointArn() {
        return endpointArn;
    }

    public void setEndpointArn(String endpointArn) {
        this.endpointArn = endpointArn;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "deviceToken='" + deviceToken + '\'' +
                ", userId=" + userId +
                ", platform='" + platform + '\'' +
                ", endpointArn='" + endpointArn + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
