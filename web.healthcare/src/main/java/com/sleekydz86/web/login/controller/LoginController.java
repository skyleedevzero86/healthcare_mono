package com.sleekydz86.web.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.web.global.util.AES256Util;
import com.sleekydz86.web.global.util.GatewayUtils;
import com.sleekydz86.web.global.util.ImagesUtil;
import com.sleekydz86.web.global.util.JwtTokenUtils;
import com.sleekydz86.web.global.util.Sha256;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class LoginController {

    @Value("${gateway.version}")
    private String version;

    @Value("${gateway.auth.uri}")
    private String authUri;

    @Autowired
    JwtTokenUtils jwtTokenUtils;

    /**
     * @apiNote 로그인 view
     * @return view
     */
    @GetMapping("/signin")
    public String singin(HttpServletRequest req, HttpServletResponse res, HttpSession session,
            @RequestParam Map<String, Object> map, Model model) throws Exception {
        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("result", model.getAttribute("result"));

        return "redirect:/userInfo/userBoardInfo?userinfoId=" + session.getAttribute("UserinfoId");
    }

    /**
     * @apiNote 회원가입 view
     * @return view
     */
    @RequestMapping("/signup")
    public String singup(HttpServletRequest req, HttpServletResponse res, HttpSession session,
            @RequestParam Map<String, Object> map, Model model) throws Exception {
        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("result", model.getAttribute("result"));
        return "redirect:/";
    }

    /**
     * @apiNote ID 중복체크(ajax)
     * @return Map
     */
    @RequestMapping("/duplicateId")
    @ResponseBody
    public Map<String, Object> duplicateId(HttpServletRequest req, HttpServletResponse res, Model model,
            @RequestParam Map<String, Object> param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject body = new JSONObject(param);

        String str = (String) GatewayUtils.post(new URL(authUri + version + "/duplicateId"), null, body.toString());
        ObjectMapper obj = new ObjectMapper();
        map = obj.readValue(str, Map.class); // 결과 값 map parsing
        return map;
    }

    /**
     * @apiNote 회원가입 처리
     * @return Map
     */
    @PostMapping("/succesSignup")
    public String succesSignup(HttpServletRequest req, HttpServletResponse res, Model model,
            @RequestParam Map<String, Object> map,
            @RequestParam("profileImg") MultipartFile image) {

        String imgFileName = "";
        JSONObject body = new JSONObject();

        for (String str : map.keySet()) {
            if (str.equals("userPwEnc")) { // SHA-256
                String passwd = (String) map.get("userPwEnc");
                try {
                    passwd = Sha256.encryt(passwd); // PW 암호화(SHA-256)
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                body.put(str, passwd);
            } else if (str.equals("birthEnc") || str.equals("telNumEnc")) {
                body.put(str, AES256Util.encrypt((String) map.get(str))); // 암호화(AES256)
                // body.put("age",Year.now()-birthEnc )
            } else {
                body.put(str, map.get(str));
                model.addAttribute(str, map.get(str));
            }
        }

        if (image != null) {
            try {
                imgFileName = ImagesUtil.write(image); // tmp경로에 파일 복사
                body.put("imgFileName", imgFileName);
            } catch (Exception e) {
            }
        }

        String str = null;
        try {
            str = (String) GatewayUtils.post(new URL(authUri + version + "/signup"), null, body.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject(str);
        model.addAttribute("result", result);
        if (result.get("resultCode").equals("0000")) {
            return "redirect:/";
        } else {
            return "forward:/user/signup";
        }
    }

    /**
     * @return Map
     * @apiNote 로그인 처리
     */
    @PostMapping("/loginCheck")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest req, HttpServletResponse res, Model model,
            HttpSession session, String token,
            @RequestParam Map<String, Object> map) throws Exception {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());

        // gateway+"auth/test"
        JSONObject body = new JSONObject();
        String passwd = Sha256.encryt((String) map.get("userPwEnc"));

        for (String str : map.keySet()) {
            if (str.equals("userPwEnc")) {
                body.put(str, passwd);
            } else {
                body.put(str, map.get(str));
            }
        }

        body.put("source", "W"); // web, moblie 구분 값
        model.addAttribute("result", body);

        String str = (String) GatewayUtils.post(new URL(authUri + version + "/signin"), null, body.toString());

        JSONObject result = new JSONObject(str);
        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> resMap = new HashMap<>();
        resMap = obj.readValue(str, Map.class); // 결과 값 Map parsing
        String resultCode = (String) result.opt("resultCode");
        model.addAttribute("result", result);
        log.info("ash login result " + str);
        if (resultCode.equals("0000")) {
            if (!result.isNull("resultData")) {
                result = (JSONObject) result.opt("resultData");
                String rfToken = (String) result.optString("refreshToken");
                token = (String) result.optString("accessToken");
                session.setAttribute("userId", result.get("userId"));
                session.setAttribute("sessUserNm", result.get("userNm"));
                session.setAttribute("acToken", token);
                session.setAttribute("rfToken", rfToken);
                Claims claims = jwtTokenUtils.parseClaims(token); // 토큰 분석
                session.setAttribute("userRoleFk", claims.get("role")); // 토큰 분석 결과(role:권한 값)
                session.setAttribute("sessuserRoleFk", claims.get("role"));
                resMap.put("userRoleFk", claims.get("role"));
                resMap.put("userId", result.get("userId"));

            }
        }
        return resMap;
    }

    /**
     * @apiNote 회원가입 처리
     * @return Map
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session,
            @RequestParam Map<String, Object> map) throws Exception {
        // 세션 확인(인증 값 존재 여부)
        if (session.getValueNames().length == 0)
            return "redirect:/user/signin";
        JSONObject body = new JSONObject();

        String str = (String) GatewayUtils.post(new URL(authUri + version + "/logout"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);
        // 로그아웃 처리: 세션 정리
        session.invalidate();

        return "redirect:/";
    }

    /**
     * @apiNote 담당자(의사) 검색(Ajax) (현재: 미사용)
     * @return Map
     */
    @PostMapping("/doctorSearch")
    @ResponseBody
    public Map<String, Object> doctorSearch(@RequestParam Map<String, Object> map)
            throws Exception, MalformedURLException {
        JSONObject body = new JSONObject();
        body.put("userNm", map.get("doctorNm"));
        String str = (String) GatewayUtils.post(new URL(authUri + version + "/searchDoctor"),
                body.toString());
        ObjectMapper obj = new ObjectMapper();
        map = obj.readValue(str, Map.class);

        return map;
    }

    /**
     * @apiNote 보호자 검색(Ajax) (현재: 미사용)
     * @return Map
     */
    @PostMapping("/parentSearch")
    @ResponseBody
    public Map<String, Object> parentSearch(@RequestParam Map<String, Object> map)
            throws Exception, MalformedURLException {
        JSONObject body = new JSONObject();
        body.put("userNm", map.get("parentNm"));
        body.put("birthEnc", AES256Util.encrypt((String) map.get("parentBirthEnc"))); // 검색 조건 평문 암호화
        body.put("telNumEnc", AES256Util.encrypt((String) map.get("parentTelNumEnc"))); // 검색 조건 평문 암호화
        String str = (String) GatewayUtils.post(new URL(authUri + version + "/searchParent"),
                body.toString());
        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = new HashMap<String, Object>();
        result = obj.readValue(str, Map.class); // 결과 값 Map parsing
        map = (Map<String, Object>) result.get("resultData");
        ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
        if (map != null) {
            if (map.get("list") != null) {
                for (Map<String, Object> d : (ArrayList<Map<String, Object>>) map.get("list")) {
                    d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc"))); // 결과 값: 암호화된 값 복호화
                    arr.add(d);
                }
                result.put("resultData", arr);
            }
        }
        return result;
    }

    /**
     * @apiNote 개발용 암호화(개발용 API)
     * @return Map
     */
    @PostMapping("/encrypt")
    @ResponseBody
    public Map<String, Object> encrypt(@RequestBody Map<String, Object> map) throws Exception, MalformedURLException {
        for (String key : map.keySet()) {
            map.put(key, AES256Util.encrypt((String) map.get(key)));
        }
        return map;
    }

    /**
     * @apiNote 복호화(개발용 API)
     * @return Map
     */
    @PostMapping("/decrypt")
    @ResponseBody
    public Map<String, Object> decrypt(@RequestBody Map<String, Object> map) throws Exception, MalformedURLException {
        for (String key : map.keySet()) {
            map.put(key, AES256Util.decrypt((String) map.get(key)));
        }
        return map;
    }
}