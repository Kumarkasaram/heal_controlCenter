package com.heal.controlcenter.util;

import com.appnomic.appsone.common.util.Commons;
import com.appnomic.appsone.model.JWTData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

@Component
@Slf4j
public class CommonUtils {
    @Autowired
    KeyCloakAuthService keyCloakAuthService;

    private static final Gson gson = new GsonBuilder().create();

    public static <T> T jsonToObject(String jsonStr, Type clazz) {
        return gson.fromJson(jsonStr, clazz);
    }

    public static ObjectMapper getObjectMapperWithHtmlEncoder() {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule("HTML-Encoder", objectMapper.version()).addDeserializer(String.class, new EscapeHTML());

        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }

    public String getUserId(String authKey) throws ControlCenterException {
        JWTData jwtData = keyCloakAuthService.extractUserDetails(authKey);
        return jwtData.getSub();
    }

    public static String getDecryptedData(String encryptedData) {
        //TODO BouncyCastle encryption algorithm has to be used in further releases.
        return new String(Base64.getDecoder().decode(encryptedData));
    }

    public String decryptInBCEC(String input) throws ServerException {
        if (input != null && !input.isEmpty()) {
            try {
                Security.removeProvider(Constants.BC_PROVIDER_NAME);
                return Commons.decrypt(input);
            } catch (Exception e) {
                log.error("Exception encountered while decrypting the password. Details: {}", e.getMessage());
                throw new ServerException("Error occurred while decrypting the password.");
            }
        }
        return "";
    }

    public String encryptInBCEC(String input) throws ServerException {
        if (input != null && !input.isEmpty()) {
            try {
                Security.removeProvider(Constants.BC_PROVIDER_NAME);
                return Commons.encrypt(input);
            } catch (Exception e) {
                log.error("Exception encountered while decrypting the password. Details: {}", e.getMessage(), e);
                throw new ServerException("Error occurred while decrypting the password.");
            }
        }
        return "";
    }

    public static Long getGMTToEpochTime(String time) {
        DateFormat simpleDateFormat;
        try {
            simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = simpleDateFormat.parse(time.trim());
            return date.getTime();
        }catch (Exception e)    {
            return 0L;
        }
    }
}

class EscapeHTML extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        String s = jp.getValueAsString();
        return StringEscapeUtils.escapeHtml4(s);
    }

}

