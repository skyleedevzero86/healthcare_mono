package com.sleekydz86.web.userInfo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.web.global.util.AES256Util;
import com.sleekydz86.web.global.util.GatewayUtils;
import com.sleekydz86.web.global.util.PagingUtil;
import com.sleekydz86.web.global.service.PasswordService;
import com.sleekydz86.web.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/userInfo")
@RequiredArgsConstructor
public class UserInfoController {

    @Value("${gateway.version}")
    private String version;

    @Value("${gateway.usermanagement.uri}")
    private String uri;

    private final PagingUtil pagingUtil;
    private final PasswordService passwordService;

    @SuppressWarnings("unchecked")
    @GetMapping(value = {
            "/list_user",
            "/list_par",
            "/list_doc"
    })
    public String list(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session, UserDTO dto,
            @RequestParam Map<String, Object> map) throws Exception {

        model.addAttribute("path", req.getRequestURI().replaceAll("/userInfo/", ""));
        model.addAttribute("uri", req.getRequestURI());

        model.addAttribute("dto", dto);
        String division = req.getRequestURI().replaceAll("/userInfo/list_", "");

        JSONObject body = new JSONObject();
        body.put("recordCountPerPage", dto.getRecordCountPerPage());
        body.put("pageOffset", (dto.getPageIndex() - 1) * dto.getRecordCountPerPage());
        body.put("pageIdx", dto.getPageIndex());
        body.put("searchKeyword", dto.getSearchKeyword());
        log.info(this.getClass().toString(), body);

        if (division.equals("doc")) {
            body.put("userRoleFk", "3");
        } else if (division.equals("par")) {
            body.put("userRoleFk", "2");
        } else if (division.equals("user")) {
            body.put("userRoleFk", "1");
            if (!session.getAttribute("userRoleFk").equals("4")) {
                body.put("userId", session.getAttribute("userId"));
            }
        }
        String str = (String) GatewayUtils.post(new URL(uri + version + "/list"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (result.get("resultCode").equals("0000")) {
            result = (Map<String, Object>) result.get("resultData");
            for (Map<String, Object> d : (ArrayList<Map<String, Object>>) result.get("list")) {
                d.put("birthEnc", AES256Util.decrypt((String) d.get("birthEnc")));
                d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                list.add(d);
            }
        }
        model.addAttribute("paginationInfo", pagingUtil.getPageInfo(dto, (int) result.get("totalCount")));
        model.addAttribute("list", list);
        result.remove("list");
        model.addAttribute("result", result);

        return "/userInfo/list";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/manage_userList")
    public String userlist(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session,
            UserDTO dto) throws Exception {

        model.addAttribute("uri", req.getRequestURI());

        JSONObject body = new JSONObject();
        body.put("recordCountPerPage", dto.getRecordCountPerPage());
        body.put("pageOffset", (dto.getPageIndex() - 1) * dto.getRecordCountPerPage());
        body.put("pageIdx", dto.getPageIndex());
        body.put("searchKeyword", dto.getSearchKeyword());
        log.info(this.getClass().toString(), body);

        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));

        String str = (String) GatewayUtils.post(new URL(uri + version + "/manage_userList"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (result.get("resultCode").equals("0000")) {
            result = (Map<String, Object>) result.get("resultData");
            String birth = "";
            int userAge;
            for (Map<String, Object> d : (ArrayList<Map<String, Object>>) result.get("list")) {
                birth = AES256Util.decrypt((String) d.get("birthEnc"));
                d.put("birthEnc", birth);
                userAge = LocalDate.now().getYear() - Integer.parseInt(birth.substring(0, 4));
                d.put("userAge", userAge);
                d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                d.put("age", userAge);

                list.add(d);
            }
        }

        log.info("ash list : " + list);
        model.addAttribute("paginationInfo", pagingUtil.getPageInfo(dto, (int) result.get("totalCount")));
        model.addAttribute("list", list);
        result.remove("list");
        model.addAttribute("result", result);

        return "/userInfo/user_manage_list";
    }

    @GetMapping({ "/userBoardInfo", "/analysis", "/community" })
    public String userInfo(HttpServletRequest req, HttpServletResponse res, HttpSession session, Model model,
            @RequestParam(defaultValue = "") String userinfoId) throws Exception {

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
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());
            ObjectMapper obj = new ObjectMapper();

            JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);
            JSONObject userInfo = new JSONObject();
            JSONArray userRolelist = new JSONArray();

            Map<String, Object> userHealthavg = null;
            int age = 0, bodyAge = 0, weight = 0, height = 0;
            log.info("ash bio info " + str);

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
                } else if (key.equals("height")) {
                    height = Integer.parseInt((String) userInfo.get(key));
                    model.addAttribute(key, userInfo.get(key));
                } else if (key.equals("weight")) {
                    weight = Integer.parseInt((String) userInfo.get(key));
                    model.addAttribute(key, userInfo.get(key));
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
            session.setAttribute("UserinfoId", userinfoId);

            log.info("ash found id " + userinfoId);
            model.addAttribute("userRolelist", list);
            model.addAttribute("userHealthavg", userHealthavg);

            if (req.getRequestURI().contains("analysis")) {
                return "/health/analysis";
            } else if (req.getRequestURI().contains("community")) {
                body.put("age", age);
                str = (String) GatewayUtils.post(new URL("http://localhost:8085/community/v1/findBoardlist"),
                        GatewayUtils.tokenCheck(session, res),
                        body.toString());

                log.info("ash ash result " + str);
                JSONObject commuResult = str.isEmpty() ? new JSONObject() : new JSONObject(str);
                List<Map<String, Object>> commulist = null;
                if (!commuResult.isNull("resultData")) {
                    commulist = obj.readValue(commuResult.get("resultData").toString(), ArrayList.class);
                }
                model.addAttribute("commulist", commulist);
                bodyAge = age;
                if (((height * height) / weight) * 10000 < 25) {
                    bodyAge -= 5;
                } else {
                    bodyAge += 1;
                }

                model.addAttribute("bodyAge", bodyAge);

                return "/community/communitymain";
            }
        } catch (MalformedURLException e) {
            log.error("잘못된 URL", e);
        } catch (Exception e) {
            log.error("대시보드 조회 중 오류 발생", e);
        }

        return "/user/dashboard";
    }

    @GetMapping("/mypage")
    public String mypage(HttpServletRequest req, HttpServletResponse res, HttpSession session, Model model)
            throws Exception {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));
        String str = (String) GatewayUtils.post(new URL(uri + version + "/userInfo"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);

        if (!result.isNull("resultData")) {
            result = (JSONObject) result.get("resultData");
        }

        for (String key : result.keySet()) {
            if (!key.equals("userRoleFk")) {
                if (key.equals("birthEnc") || key.equals("telNumEnc"))
                    model.addAttribute(key, AES256Util.decrypt((String) result.get(key)));
                else if (key.equals("guardian")) {
                    ObjectMapper obj = new ObjectMapper();
                    ArrayList<Map<String, Object>> arr = obj.readValue(result.get("guardian").toString(),
                            ArrayList.class);
                    ;
                    if (arr != null && arr.size() > 0) {
                        String guardianNm = "";
                        List<Map<String, Object>> resarr = new ArrayList<Map<String, Object>>();
                        int count = 0;
                        ArrayList<String> gardianNmArr = new ArrayList<String>();
                        for (Map<String, Object> map : arr) {
                            map.put("telNumEnc", AES256Util.decrypt((String) map.get("telNumEnc")));
                            resarr.add(map);
                            if (count != 0)
                                guardianNm += ", ";
                            guardianNm += (String) map.get("userNm") + " " + (String) map.get("telNumEnc");
                            count++;
                            gardianNmArr.add((String) map.get("userNm"));
                        }
                        model.addAttribute("guardianNm", guardianNm);
                        model.addAttribute("gardianNmArr", gardianNmArr);
                        result.put(key, resarr);
                        model.addAttribute(key, result.get(key));
                    }
                } else
                    model.addAttribute(key, result.get(key));
            }
        }

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("searchUserId", session.getAttribute("userId"));

        return "/user/mypage_new";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/doctorSearch")
    @ResponseBody
    public Map<String, Object> doctorSearch(HttpServletRequest req, HttpServletResponse res, Model model,
            HttpSession session, UserDTO dto, @RequestParam Map<String, Object> param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("userRoleFk", param.get("userRoleFk"));
        body.put("userNm", param.get("doctorNm"));
        String str = (String) GatewayUtils.post(new URL(uri + version + "/searchDoctor"),
                GatewayUtils.tokenCheck(session, res), body.toString());
        ObjectMapper obj = new ObjectMapper();

        map = obj.readValue(str, Map.class);
        if (map.get("resultCode").equals("1003")) {
            session.removeAttribute("acToken");
            session.removeAttribute("rfToken");
        }
        return map;

    }

    @SuppressWarnings("unchecked")
    @PostMapping("/parentSearch")
    @ResponseBody
    public Map<String, Object> parentSearch(HttpServletRequest req, HttpServletResponse res, Model model,
            HttpSession session, UserDTO dto, @RequestParam Map<String, Object> map) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userNm", map.get("parentNm"));
        body.put("birthEnc", AES256Util.encrypt((String) map.get("parentBirthEnc")));
        body.put("telNumEnc", AES256Util.encrypt((String) map.get("parentTelNumEnc")));
        String str = (String) GatewayUtils.post(new URL(uri + version + "/searchParent"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = new HashMap<String, Object>();
        result = obj.readValue(str, Map.class);
        map = (Map<String, Object>) result.get("resultData");

        ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
        if (map != null) {
            if (map.get("list") != null) {
                for (Map<String, Object> d : (ArrayList<Map<String, Object>>) map.get("list")) {
                    d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                    d.put("birthEnc", AES256Util.decrypt((String) d.get("birthEnc")));
                    arr.add(d);
                }
                result.put("resultData", arr);
            }
        }
        return result;
    }

    @PostMapping("/uptUserInfo")
    public String uptUserInfo(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session)
            throws Exception {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));
        String str = null;
        try {
            str = (String) GatewayUtils.post(new URL(uri + version + "/userInfo"),
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());
        } catch (MalformedURLException e) {
            log.error("잘못된 URL: {}", uri + version + "/userInfo", e);
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
        }
        JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);

        if (!result.isNull("resultData")) {
            result = (JSONObject) result.get("resultData");
        }
        for (String key : result.keySet()) {
            if (!key.equals("userRoleFk")) {
                if (key.equals("birthEnc") || key.equals("telNumEnc"))
                    model.addAttribute(key, AES256Util.decrypt((String) result.get(key)));
                else if (key.equals("guardian")) {
                    ObjectMapper obj = new ObjectMapper();
                    ArrayList<Map<String, Object>> arr = obj.readValue(result.get("guardian").toString(),
                            ArrayList.class);
                    ArrayList<String> gardianNmArr = new ArrayList<String>();
                    if (arr != null && arr.size() > 0) {
                        String guardianNm = "";
                        List<Map<String, Object>> resarr = new ArrayList<Map<String, Object>>();
                        int count = 0;
                        for (Map<String, Object> map : arr) {
                            map.put("telNumEnc", AES256Util.decrypt((String) map.get("telNumEnc")));
                            resarr.add(map);
                            if (count != 0)
                                guardianNm += ", ";
                            guardianNm += (String) map.get("userNm") + " " + (String) map.get("telNumEnc");
                            count++;
                            gardianNmArr.add((String) map.get("userNm"));
                        }
                        model.addAttribute("guardianNm", guardianNm);
                        model.addAttribute("gardianNmArr", gardianNmArr);
                        result.put(key, resarr);
                        model.addAttribute(key, result.get(key));
                    }
                } else
                    model.addAttribute(key, result.get(key));
            }
        }
        return "/user/updateuser";
    }

    @PostMapping("/uptUserInfoAct")
    public String uptUserInfoAct(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session,
            @RequestParam Map<String, Object> map) {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());

        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));
        for (String key : map.keySet()) {
            if (key.equals("birthEnc") || key.equals("telNumEnc"))
                body.put(key, AES256Util.encrypt((String) map.get(key)));
            else
                body.put(key, map.get(key));
        }
        String str = null;
        try {
            str = (String) GatewayUtils.post(new URL(uri + version + "/updateUserInfo"),
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());
        } catch (MalformedURLException e) {
            log.error("잘못된 URL: {}", uri + version + "/updateUserInfo", e);
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생", e);
        }
        return "redirect:/userInfo/mypage";
    }

    @PostMapping("/updatePasswd")
    @ResponseBody
    public Map<String, Object> updatePasswd(HttpServletRequest req, HttpServletResponse res, Model model,
            HttpSession session, @RequestParam Map<String, Object> map) throws Exception {
        Map<String, Object> result = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));

        for (String key : map.keySet()) {
            if (key.equals("userPwEnc") || key.equals("newUserPwEnc"))
                body.put(key, passwordService.encode((String) map.get(key)));
            else
                body.put(key, map.get(key));
        }

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri + version + "/updatePasswd"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        ObjectMapper obj = new ObjectMapper();
        result = obj.readValue(str, Map.class);
        if (result.get("resultCode").equals("1003")) {
            session.removeAttribute("acToken");
            session.removeAttribute("rfToken");
        } else if (result.get("resultCode").equals("0000")) {
            session.invalidate();
        }
        return result;
    }

    @PostMapping("/secession")
    @ResponseBody
    public Map<String, Object> secession(HttpServletRequest req, HttpServletResponse res, Model model,
            HttpSession session) throws Exception {
        Map<String, Object> result = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri + version + "/deleteUserInfo"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        ObjectMapper obj = new ObjectMapper();
        result = obj.readValue(str, Map.class);
        if (result.get("resultCode").equals("1003")) {
            session.removeAttribute("acToken");
            session.removeAttribute("rfToken");
        }
        if (result.get("resultCode").equals("0000")) {
            session.invalidate();
        }
        return result;
    }

    @PostMapping("/drguardianList")
    @ResponseBody
    public Object drguardianList(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,
            Model model, HttpSession session) throws Exception {

        JSONObject body = new JSONObject(map);

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri + version + "/drguardianList"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<>();

        if (result.get("resultCode").equals("0000")) {

            if (ObjectUtils.isEmpty(result.get("resultData"))) {
                Map<String, Object> a = new HashMap<>();
                a.put("userNm", " ");
                a.put("email", "Healthcare@naver.com");
                a.put("telNumEnc", "01030641230");
                a.put("userId", "1");

                list.add(a);
            } else {
                list = (ArrayList<Map<String, Object>>) result.get("resultData");

                for (Map<String, Object> d : list) {
                    if (d.containsKey("telNumEnc")) {
                        d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                    }
                }

            }

        }
        model.addAttribute("list", list);
        model.addAttribute("result", result);
        return list;
    }

    @PostMapping("/searchuserList")
    public String searchuserList(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,
            Model model, HttpSession session) {

        JSONObject body = new JSONObject(map);
        String url = "";
        url = "/search_userList";

        try {
            String str = (String) GatewayUtils.post(new URL(uri + version + url), GatewayUtils.tokenCheck(session, res),
                    body.toString());
            log.info("ash searchuserlist: " + str);
            ObjectMapper obj = new ObjectMapper();
            Map<String, Object> result = obj.readValue(str, Map.class);

            ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            if (result.get("resultCode").equals("0000")) {
                result = (Map<String, Object>) result.get("resultData");
                for (Map<String, Object> d : (ArrayList<Map<String, Object>>) result.get("list")) {
                    d.put("birthEnc", AES256Util.decrypt((String) d.get("birthEnc")));
                    d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                    list.add(d);
                }
            }

            if (result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }

        } catch (MalformedURLException e) {
            log.error("잘못된 URL", e);
        } catch (Exception e) {
            log.error("사용자 상세 정보 조회 중 오류 발생", e);
        }
        return "/userInfo/user_add_detail";
    }

    @PostMapping("/searchUser")
    @ResponseBody
    public Object searchUser(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,
            Model model, HttpSession session) throws Exception {

        JSONObject body = new JSONObject(map);

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri + version + "/search_userList"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<>();

        log.info("ash result userlist" + str);
        if (result.get("resultCode").equals("0000")) {

            if (ObjectUtils.isEmpty(result.get("resultData"))) {
            } else {
                list = (ArrayList<Map<String, Object>>) result.get("resultData");
                String birthDateString = "";
                int age = 0;
                for (Map<String, Object> d : list) {
                    if (d.containsKey("birthEnc")) {
                        birthDateString = AES256Util.decrypt((String) d.get("birthEnc"));
                        d.put("birthEnc", birthDateString);
                        if (!(birthDateString.isEmpty() && birthDateString.equals(""))) {
                            LocalDate birthDate = LocalDate.parse(birthDateString);
                            LocalDate currentDate = LocalDate.now();
                            age = Period.between(birthDate, currentDate).getYears();
                            d.put("age", age);
                        }

                    }
                    if (d.containsKey("deptNm")) {
                        if (d.get("deptNm") == null) {
                            d.put("deptNm", "healthcare");
                        }
                    }
                }
            }
        }
        return list;
    }
}
