package com.mobilepushnotifications.service;

import com.google.gson.GsonBuilder;
import com.mobilepushnotifications.components.AmazonSnsClientWrapper;
import com.mobilepushnotifications.components.NotificationMessageConstructor;
import com.mobilepushnotifications.dao.EndpointRepository;
import com.mobilepushnotifications.entity.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

@Service
public class MobilePushNotificationServiceDefault implements MobilePushNotificationService {

    private Environment environment;
    private EndpointRepository endpointRepository;
    private AmazonSnsClientWrapper amazonSnsClientWrapper;

    private NotificationMessageConstructor notificationMessageConstructor;

    @Autowired
    public MobilePushNotificationServiceDefault(
            Environment environment,
            EndpointRepository endpointRepository,
            AmazonSnsClientWrapper amazonSnsClientWrapper,
            NotificationMessageConstructor notificationMessageConstructor) {
        this.environment = environment;
        this.endpointRepository = endpointRepository;
        this.amazonSnsClientWrapper = amazonSnsClientWrapper;
        this.notificationMessageConstructor = notificationMessageConstructor;
    }

    @Override
    @Transactional
    public void registerEndpoint(String deviceToken, int userId, String platform) {
        Endpoint alreadyRegisteredEndpoint = endpointRepository.findById(deviceToken).orElse(null);
        String alreadyRegisteredEndpointArn = alreadyRegisteredEndpoint != null ? alreadyRegisteredEndpoint.getEndpointArn() : null;

        String customUserData = constructCustomUserData(userId);
        String platformApplicationArn = environment.getProperty("aws.sns.platform_application_arn." + platform.toLowerCase());

        String endpointArn = this.amazonSnsClientWrapper.registerEndpoint(deviceToken, customUserData, platformApplicationArn, alreadyRegisteredEndpointArn);

        endpointRepository.save(
                new Endpoint(deviceToken, userId, platform, endpointArn, currentTimeMillis())
        );
    }

    private static String constructCustomUserData(int userId) {
        Map<String, Integer> personMap = new HashMap<>();
        personMap.put("uid", userId);
        return new GsonBuilder().create().toJson(personMap);
    }


    @Override
    public void pushNotificationToUsers(Collection<Integer> userIds, String title, String message, String url) {
        userIds.forEach(userId -> {
            List<Endpoint> endpointsByUserId = endpointRepository.findByUserId(userId);
            endpointsByUserId.forEach(endpoint -> pushNotificationToEndpoint(endpoint.getEndpointArn(), title, message, url));
        });
    }

    private void pushNotificationToEndpoint(String endpointArn, String title, String message, String url) {
        String notificationMessage = notificationMessageConstructor.constructNotificationMessage(title, message, url);
        amazonSnsClientWrapper.sendNotification(endpointArn, notificationMessage);
    }
}
