package com.mobilepushnotifications.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MobilePushNotificationConfigController {

    @Autowired
    private Environment environment;

    @GetMapping("/configs")
    public Map getConfigs() {
        Map<String, String> configsMap = new LinkedHashMap<>();
        configsMap.put("Mobile Push Notification Config Property 1", environment.getProperty("mobile.push.notification.configproperty1"));
        configsMap.put("Mobile Push Notification Config Property 2", environment.getProperty("mobile.push.notification.configproperty2"));

        return configsMap;
    }

}
