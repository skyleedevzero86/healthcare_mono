package com.sleekydz86.web.aidoctor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.web.global.exception.ResponseException;
import com.sleekydz86.web.global.util.GatewayUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/aidoctor")
public class AIResponseController {

    @Value("${gateway.healthcare.uri}")
    private String uri;

    @Value("${gateway.version}")
    private String version;

    @PostMapping("/chat_ai")
    @ResponseBody
    public Object getAIResult(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,
            Model model, HttpSession session) {
        JSONObject body = new JSONObject(map);
        ObjectMapper obj = new ObjectMapper();

        Map<String, Object> result = new HashMap<>();
        String str = "";
        try {
            str = (String) GatewayUtils.post(new URL(uri + version + "/chat_ai"),
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());

            result = obj.readValue(str, Map.class);
        } catch (MalformedURLException | JsonProcessingException e) {
            throw new ResponseException(e);
        }

        return result;
    }
}
