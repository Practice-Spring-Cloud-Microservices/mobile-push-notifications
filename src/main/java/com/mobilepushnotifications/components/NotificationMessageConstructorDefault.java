package com.mobilepushnotifications.components;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationMessageConstructorDefault implements NotificationMessageConstructor {
    @Override
    public String constructNotificationMessage(String title, String message, String url) {
        Map msgMap = ImmutableMap.of(
                "GCM", constructFcmMessage(title, message, url),
                "APNS", constructApnsMessage(title, message, url)
        );

        return toJsonString(msgMap);
    }

    private static String constructFcmMessage(String title, String message, String url) {
        Map msgMap = ImmutableMap.of(
                "data", ImmutableMap.of(
                        "title", title, "message", message, "url", url)
        );
        return toJsonString(msgMap);
    }

    private static String constructApnsMessage(String title, String message, String url) {
        Map msgMap = ImmutableMap.of(
                "aps", ImmutableMap.of(
                        "alert", ImmutableMap.of(
                                "title", title, "body", message
                        )
                ),
                "url", url
        );
        return toJsonString(msgMap);
    }

    private static String toJsonString(Map theMap) {
        return new GsonBuilder()
                .create()
                .toJson(theMap);
    }
}
