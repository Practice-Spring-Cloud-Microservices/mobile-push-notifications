package com.mobilepushnotifications.controller;

import com.mobilepushnotifications.service.MobilePushNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MobilePushNotificationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Environment environment;
    private MobilePushNotificationService mobilePushNotificationService;

    @Autowired
    public MobilePushNotificationController(Environment environment, MobilePushNotificationService mobilePushNotificationService) {
        this.environment = environment;
        this.mobilePushNotificationService = mobilePushNotificationService;
    }

    @PostMapping("/endpoint")
    public ResponseEntity<Map> registerEndpoint(@RequestBody Map<String, Object> payload) {
        logger.info("request uri = {}", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        String deviceToken = (String) payload.get("deviceToken");
        int userId = (int) payload.get("userId");
        String platform = (String) payload.get("platform");
        mobilePushNotificationService.registerEndpoint(deviceToken, userId, platform);

        return wrapUpResponse(null, null, true);
    }

    @PostMapping("/notification")
    public ResponseEntity<Map> pushNotification(@RequestBody Map<String, Object> payload) {
        logger.info("request uri = {}", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        Collection<Integer> userIds = (Collection<Integer>) payload.get("userIds");
        String title = (String) payload.get("title");
        String message = (String) payload.get("message");
        String url = (String) payload.get("url");
        mobilePushNotificationService.pushNotificationToUsers(userIds, title, message, url);

        return wrapUpResponse(null, null, true);
    }

    private ResponseEntity<Map> wrapUpResponse(String responseContentKey, Object responseContent, boolean isNullResponseOk) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("handler_server", environment.getProperty("server.port"));
        if(responseContent != null) {
            resultMap.put(responseContentKey, responseContent);
        }

        HttpStatus status = isNullResponseOk || responseContent != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(resultMap, status);
    }

}
