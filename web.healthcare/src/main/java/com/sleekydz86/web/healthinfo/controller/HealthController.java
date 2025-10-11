package com.sleekydz86.web.healthinfo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.web.global.util.GatewayUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/health")
public class HealthController {

    @Value("${gateway.healthcare.uri}")
    private String uri;

    @Value("${gateway.version}")
    private String version;

    @PostMapping(path = { "/temperature", "/spo2", "/sleep", "/heartrate", "/step", "/stress", "/bloodpress",
            "/repiratory" })
    public String healthInfo(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,
            Model model, HttpSession session) {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        if (map.getOrDefault("searchUserId", null) != null
                && map.getOrDefault("searchUserId", null).equals("undefined")) { // 검색 ID 없으면 내 ID
            map.put("searchUserId", session.getAttribute("userId"));
        }
        model.addAttribute("searchUserId", map.getOrDefault("searchUserId", session.getAttribute("userId")));
        model.addAttribute("userNm", map.getOrDefault("userNm", null));
        return "/health/healthInfo";
    }

    @PostMapping("/healthInfoChart")
    @ResponseBody
    public Object healthInfoChart(HttpServletRequest req, HttpServletResponse res,
            @RequestParam Map<String, Object> map, Model model, HttpSession session) {
        JSONObject body = new JSONObject(map);
        Map<String, Object> result = new HashMap<>();
        String url = "/customMinuteDashBRDChart";
        if (map.containsKey("searchWrd")) {
            if (map.get("query").equals("D"))
                url = "/customMinuteChart";
            else {
                if (map.get("searchWrd").equals("step") || map.get("searchWrd").equals("sleep")
                        || map.get("searchWrd").equals("heartrate")) {
                    url = "/healthInfoChart";
                } else {
                    url = "/minmaxHealthInfoChart";
                }
            }
        }
        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());
            ObjectMapper obj = new ObjectMapper();
            result = obj.readValue(str, Map.class);
            if (result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/healthrealtimeData")
    @ResponseBody
    public Object healthrealtimeData(HttpServletRequest req, HttpServletResponse res,
            @RequestParam Map<String, Object> map, Model model, HttpSession session) {
        JSONObject body = new JSONObject(map);
        Map<String, Object> result = new HashMap<>();

        String url = "/realtimeBiodata";
        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());
            log.info(str);
            ObjectMapper obj = new ObjectMapper();
            result = obj.readValue(str, Map.class);
            if (result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }

            log.info("ash realtime param " + str);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/healthgraphBiodata")
    @ResponseBody
    public Object healthgraphBiodata(HttpServletRequest req, HttpServletResponse res,
            @RequestParam Map<String, Object> map, Model model, HttpSession session) {
        String url = "";
        url = "/graphBiodata";
        log.info("ash param" + map.toString());
        JSONObject body = new JSONObject(map);
        Map<String, Object> result = new HashMap<>();
        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());
            ObjectMapper obj = new ObjectMapper();

            result = obj.readValue(str, Map.class);
            if (result.containsKey("resultCode") && result.get("resultCode").equals("5001")) {
                session.removeAttribute("acToken"); // 세션종료
                session.removeAttribute("rfToken");
            }
            log.info("ash analysis param " + str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @PostMapping("/healthdailyData")
    @ResponseBody
    public Object healthdailyData(HttpServletRequest req, HttpServletResponse res,
            @RequestParam Map<String, Object> map, Model model, HttpSession session) {
        String url = "";
        url = "/dailydata";
        log.info("ash param" + map.toString());
        JSONObject body = new JSONObject(map);
        Map<String, Object> result = new HashMap<>();
        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());
            ObjectMapper obj = new ObjectMapper();

            result = obj.readValue(str, Map.class);
            if (result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }
            log.info("ash analysis param " + str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @PostMapping("/healthinfoDailySleep")
    @ResponseBody
    public Object healthinfoDailySleep(HttpServletRequest req, HttpServletResponse res,
            @RequestParam Map<String, Object> map, Model model, HttpSession session) {
        String url = "";
        url = "/healthinfoDailySleep";
        JSONObject body = new JSONObject(map);
        Map<String, Object> result = new HashMap<>();
        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());

            ObjectMapper obj = new ObjectMapper();
            result = obj.readValue(str, Map.class);

            if (result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @PostMapping("/healthScoreList")
    @ResponseBody
    public Object healthScoreList(HttpServletRequest req, HttpServletResponse res,
            @RequestParam Map<String, Object> map, Model model, HttpSession session) {
        JSONObject body = new JSONObject(map);
        Map<String, Object> result = new HashMap<>();

        String url = "";
        url = "/healthScoreList";
        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());

            ObjectMapper obj = new ObjectMapper();
            result = obj.readValue(str, Map.class);
            log.info("ash result" + result);
            if (result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
