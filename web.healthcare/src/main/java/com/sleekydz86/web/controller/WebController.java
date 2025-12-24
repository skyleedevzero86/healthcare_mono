package com.sleekydz86.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.web.client.HealthcareClient;
import com.sleekydz86.web.client.UserClient;
import com.sleekydz86.web.dto.User;
import com.sleekydz86.web.global.util.AES256Util;
import com.sleekydz86.web.global.util.GatewayUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {

    private final HealthcareClient healthcareClient;
    private final UserClient userClient;

    @Value("${gateway.version}")
    private String version;

    @Value("${gateway.usermanagement.uri}")
    private String uri;

    public WebController(HealthcareClient healthcareClient, UserClient userClient) {
        this.healthcareClient = healthcareClient;
        this.userClient = userClient;
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest req, HttpSession session,
            @RequestParam(defaultValue = "") String userinfoId) {
        
        String acToken = (String) session.getAttribute("acToken");
        if (acToken == null || acToken.isEmpty()) {
            return "index";
        }

        try {
            model.addAttribute("uri", req.getRequestURI());
            model.addAttribute("url", req.getRequestURL());
            JSONObject body = new JSONObject();

            if (userinfoId.isEmpty() || userinfoId.equals("")) {
                body.put("userId", session.getAttribute("userId"));
            } else {
                body.put("userId", userinfoId);
            }

            body.put("userRoleFk", "1");
            String str = null;
            try {
                str = (String) GatewayUtils.post(new URL(uri + version + "/userBoardInfo"),
                        GatewayUtils.tokenCheck(session, null),
                        body.toString());
                ObjectMapper obj = new ObjectMapper();

                JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);
                JSONObject userInfo = new JSONObject();
                JSONArray userRolelist = new JSONArray();

                Map<String, Object> userHealthavg = null;
                int age = 0;

                if (!result.isNull("resultData")) {
                    result = (JSONObject) result.get("resultData");
                    if (!result.isNull("userBioinfo")) {
                        userInfo = (JSONObject) result.get("userBioinfo");
                    }
                    if (!result.isNull("userRolelist")) {
                        userRolelist = (JSONArray) result.get("userRolelist");
                    }
                    if (!result.isNull("userHealthavg")) {
                        userHealthavg = obj.readValue(result.get("userHealthavg").toString(), HashMap.class);
                    }
                }

                for (String key : userInfo.keySet()) {
                    if (key.equals("birthEnc")) {
                        String birthDateString = AES256Util.decrypt((String) userInfo.get(key));
                        model.addAttribute(key, birthDateString);
                        LocalDate birthDate = LocalDate.parse(birthDateString);
                        LocalDate currentDate = LocalDate.now();
                        age = Period.between(birthDate, currentDate).getYears();
                        session.setAttribute("ageAvg", age);
                    } else if (key.equals("telNumEnc")) {
                        model.addAttribute(key, AES256Util.decrypt((String) userInfo.get(key)));
                    } else if (key.equals("gender")) {
                        model.addAttribute(key, userInfo.get(key));
                        session.setAttribute("gender", userInfo.get(key));
                    } else {
                        model.addAttribute(key, userInfo.get(key));
                    }
                }

                ArrayList<Map<String, Object>> list = null;
                String phonenum = "";
                if (!userRolelist.isEmpty()) {
                    list = obj.readValue(userRolelist.toString(), ArrayList.class);
                    for (Map<String, Object> map : list) {
                        if (map.containsKey("telNumEnc")) {
                            phonenum = AES256Util.decrypt((String) map.get("telNumEnc"));
                            map.put("telNumEnc", phonenum.substring(0, 3) + "-" + phonenum.substring(3, 7) + "-"
                                    + phonenum.substring(7));
                        }
                    }
                }

                model.addAttribute("searchUserId", session.getAttribute("userId"));
                session.setAttribute("UserinfoId", userinfoId.isEmpty() ? session.getAttribute("userId") : userinfoId);
                model.addAttribute("userRolelist", list);
                model.addAttribute("userHealthavg", userHealthavg);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        
        return "index";
    }

    @GetMapping("/patients")
    public String patients(Model model) {
        return "patients";
    }

    @GetMapping("/web/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            User user = userClient.getCurrentUser("Bearer " + token);
            model.addAttribute("user", user);
        }
        return "dashboard";
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
