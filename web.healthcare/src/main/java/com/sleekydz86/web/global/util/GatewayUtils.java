package com.sleekydz86.web.global.util;

import com.sleekydz86.web.global.exception.GatewayPostException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class GatewayUtils {

    private static String version;
    private static String authUri;
    private static String secret;
    private static int timeout;

    public GatewayUtils(@Value("${token.secret}") String SECRET, @Value("${gateway.auth.uri}") String AUTHURI,
            @Value("${gateway.version}") String VERSION, @Value("${request.timeout}") int TIMEOUT) {
        secret = SECRET;
        authUri = AUTHURI;
        version = VERSION;
        timeout = TIMEOUT;
    }

    public static Object get(URL url, String tokenKey) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", "Bearer " + tokenKey)
                .build();

        Response response = null;
        String responseStr = "";
        try {
            response = client.newCall(request).execute();
            responseStr = response.body().string();
        } catch (IOException e) {
            throw new GatewayPostException(e);
        }

        return responseStr;
    }

    @SuppressWarnings("deprecation")
    public static Object post(URL url, String tokenKey, String bodyStr) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = !bodyStr.isEmpty() ? RequestBody.create(mediaType, bodyStr) : null;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", "Bearer " + tokenKey)
                .build();

        Response response = null;
        String responseStr = "";
        try {
            response = client.newCall(request).execute();
            responseStr = response.body().string();
        } catch (IOException e) {
            throw new GatewayPostException(e);
        }

        return responseStr;
    }

    @SuppressWarnings("deprecation")
    public static Object post(URL url, String bodyStr) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = !bodyStr.isEmpty() ? RequestBody.create(mediaType, bodyStr) : null;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build();

        Response response = null;
        String responseStr = "";

        try {
            response = client.newCall(request).execute();
            responseStr = response.body().string();
        } catch (IOException e) {
            throw new GatewayPostException(e);
        }

        return responseStr;
    }

    @SuppressWarnings("deprecation")
    public static String tokenCheck(HttpSession session, HttpServletResponse res) {
        String acToken = (String) session.getAttribute("acToken");
        String rfToken = (String) session.getAttribute("rfToken");

        // 토큰이 null이거나 비어있으면 null 반환
        if (acToken == null || acToken.isEmpty()) {
            return null;
        }

        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils(secret);
        JSONObject jwt = new JSONObject(jwtTokenUtils.parseClaims(acToken));

        Date exp = new Date((Integer) jwt.get("exp") * 1000L);
        Date now = new Date();

        if (!exp.after(now)) {
            if (acToken != null && !acToken.isEmpty()) {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(timeout, TimeUnit.SECONDS)
                        .readTimeout(timeout, TimeUnit.SECONDS)
                        .writeTimeout(timeout, TimeUnit.SECONDS)
                        .build();

                MediaType mediaType = MediaType.parse("application/json");
                JSONObject obj = new JSONObject();

                RequestBody body = RequestBody.create(mediaType, obj.toString());

                Request request = new Request.Builder()
                        .url(authUri + version + "/refresh")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("Authorization", "Bearer " + acToken)
                        .addHeader("refreshToken", "Bearer " + rfToken)
                        .build();

                Response response = null;
                JSONObject result = null;
                try {
                    response = client.newCall(request).execute();
                    result = new JSONObject(response.body().string());

                    if (!result.isNull("resultData") && !result.isEmpty()) {
                        result = (JSONObject) result.get("resultData");

                        if (!result.isNull("refreshToken") && !result.isEmpty()) {
                            rfToken = (String) result.get("refreshToken");
                            acToken = (String) result.get("accessToken");
                            session.setAttribute("acToken", acToken);
                            session.setAttribute("rfToken", rfToken);
                        }
                    }
                } catch (IOException e) {
                    throw new GatewayPostException(e);
                }
            }
        }
        return acToken;
    }
}