package com.example.bookswap.auth;

import android.util.Base64;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JWTManager {

    private static final String SECRET = "BookSwapSuperSecretKey";

    // --- Utility: HMAC SHA256 ---
    private static String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());
        return Base64.encodeToString(hash, Base64.NO_WRAP | Base64.URL_SAFE);
    }

    // --- Create JWT Token ---
    public static String createToken(int userId, String name, String email, long loginTime) {
        try {
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            JSONObject payload = new JSONObject();
            payload.put("userId", userId);
            payload.put("name", name);
            payload.put("email", email);
            payload.put("loginTime", loginTime);

            String headerBase =
                    Base64.encodeToString(header.toString().getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);
            String payloadBase =
                    Base64.encodeToString(payload.toString().getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);

            String signature = hmacSha256(headerBase + "." + payloadBase, SECRET);

            return headerBase + "." + payloadBase + "." + signature;

        } catch (Exception e) {
            return null;
        }
    }

    // --- Validate token ---
    public static boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String expectedSig = hmacSha256(header + "." + payload, SECRET);

            return expectedSig.equals(signature);

        } catch (Exception e) {
            return false;
        }
    }

    // --- Extract payload as JSON ---
    private static JSONObject getPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
            return new JSONObject(payloadJson);
        } catch (Exception e) {
            return null;
        }
    }

    // --- Get userID ---
    public static int getUserId(String token) {
        try {
            return getPayload(token).getInt("userId");
        } catch (Exception e) {
            return -1;
        }
    }

    // --- Get user name ---
    public static String getUserName(String token) {
        try {
            return getPayload(token).getString("name");
        } catch (Exception e) {
            return "";
        }
    }

    // --- Get user email ---
    public static String getEmail(String token) {
        try {
            return getPayload(token).getString("email");
        } catch (Exception e) {
            return "";
        }
    }

    // --- Get login timestamp ---
    public static long getLoginTime(String token) {
        try {
            return getPayload(token).getLong("loginTime");
        } catch (Exception e) {
            return 0;
        }
    }
}
