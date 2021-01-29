package com.mobilepushnotifications.service;

import java.util.Collection;

public interface MobilePushNotificationService {

    void registerEndpoint(String deviceToken, int userId, String platform);

    void pushNotificationToUsers(Collection<Integer> userIds, String title, String message, String url);
}
