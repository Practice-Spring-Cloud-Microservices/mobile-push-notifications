package com.mobilepushnotifications.components;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AmazonSnsClientWrapper {

    private AmazonSNS amazonSns;

    @Autowired
    public AmazonSnsClientWrapper(AmazonSNS amazonSns) {
        this.amazonSns = amazonSns;
    }


    /**
     * @param deviceToken                  Unique identifier created by the notification service for an app on a device. The specific name
     *                                     for Token will vary, depending on which notification service is being used. For example, when using APNS as the
     *                                     notification service, you need the device token. Alternatively, when using FCM, the device token
     *                                     equivalent is called the registration ID.
     * @param customUserData               informational data
     * @param platformApplicationArn       platform application ARN
     * @param alreadyRegisteredEndpointArn already registered endpoint ARN which may be updated
     * @return $endpoint_arn new registered or updated endpoint arn
     * @throws AmazonSNSException amazon sns exception
     */

    public String registerEndpoint(String deviceToken, String customUserData, String platformApplicationArn, String alreadyRegisteredEndpointArn) {
        String endpointArn = alreadyRegisteredEndpointArn;

        boolean updateNeeded = false;
        boolean createNeeded = (endpointArn == null);

        if (createNeeded) {
            // No platform endpoint ARN is stored; need to create one.
            endpointArn = this.createEndpoint(deviceToken, platformApplicationArn, customUserData);
            createNeeded = false;
        }

        //"Retrieving platform endpoint data..."
        // Look up the platform endpoint and make sure the data in it is current, even if it was just created.

        try {

            GetEndpointAttributesRequest geaReq =
                    new GetEndpointAttributesRequest()
                            .withEndpointArn(endpointArn);
            GetEndpointAttributesResult geaRes =
                    amazonSns.getEndpointAttributes(geaReq);

            updateNeeded = !geaRes.getAttributes().get("Token").equals(deviceToken)
                    || !geaRes.getAttributes().get("Enabled").equals("true")
                    || !geaRes.getAttributes().get("CustomUserData").equals(customUserData);

        } catch (NotFoundException nfe) {
            // We had a stored ARN, but the platform endpoint associated with it
            // disappeared/deleted. Recreate it.
            createNeeded = true;

        }

        if (createNeeded) {
            endpointArn = this.createEndpoint(deviceToken, platformApplicationArn, customUserData);
        }

        if (updateNeeded) {
            // The platform endpoint is out of sync with the current data;
            // update the token and enable it.
            this.update_endpoint_attributes(deviceToken, customUserData, endpointArn);
        }

        return endpointArn;
    }

    /**
     * @return Endpoint ARN, never null
     */
    private String createEndpoint(String deviceToken, String platformApplicationArn, String customUserData) {
        String endpointArn = null;

        try {

            CreatePlatformEndpointRequest cpeReq =
                    new CreatePlatformEndpointRequest()
                            .withPlatformApplicationArn(platformApplicationArn)
                            .withToken(deviceToken)
                            .withCustomUserData(customUserData);
            CreatePlatformEndpointResult cpeRes = amazonSns.createPlatformEndpoint(cpeReq);
            endpointArn = cpeRes.getEndpointArn();

        } catch (InvalidParameterException ipe) {
            String message = ipe.getErrorMessage();
            //preg_match("/.*Endpoint (arn:aws:sns[^ ]+) already exists with the same Token.*/", $msg, $matches);
            Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists with the same Token.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                endpointArn = m.group(1);
            } else {
                // Rethrow the exception, the input is actually bad.
                throw ipe;
            }
        }

        return endpointArn;
    }

    private void update_endpoint_attributes(String deviceToken, String customUserData, String endpointArn) {
        Map<String, String> attribs = new HashMap<>();
        attribs.put("Token", deviceToken);
        attribs.put("Enabled", "true");
        attribs.put("CustomUserData", customUserData);

        SetEndpointAttributesRequest saeReq =
                new SetEndpointAttributesRequest()
                        .withEndpointArn(endpointArn)
                        .withAttributes(attribs);
        amazonSns.setEndpointAttributes(saeReq);
    }


    public void sendNotification(String endpointArn, String jsonMsg) {
        amazonSns.publish(
                new PublishRequest()
                        .withMessageStructure("json")
                        .withTargetArn(endpointArn)
                        .withMessage(jsonMsg));
    }

}
