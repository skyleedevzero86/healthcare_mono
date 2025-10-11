package com.sleekydz86.web.userInfo.controller;



@Slf4j
@Controller
@RequestMapping("/userInfo")
public class UserInfoController {

    @Value("${gateway.version}")
    private String version;

    @Value("${gateway.management.uri}")
    private String uri;

    @Autowired
    private PagingUtil pagingUtil;


    /**
     * @apiNote 사용자 목록 view
     * @param UserDTO = 유저 dto
     * @param PaginationInfo: 조회 조건, 결과 값 페이징 관련
     * @return view
     */
    @SuppressWarnings("unchecked")
    @GetMapping(value = {
            "/list_user",
            "/list_par",
            "/list_doc"
    })
    public String list(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session, UserDTO dto
            , @RequestParam Map<String, Object> map
    ) throws Exception{

        model.addAttribute("path", req.getRequestURI().replaceAll("/userInfo/", ""));
        model.addAttribute("uri", req.getRequestURI());

        model.addAttribute("dto",dto);
        String division = req.getRequestURI().replaceAll("/userInfo/list_", "");

        JSONObject body = new JSONObject();
        body.put("recordCountPerPage", dto.getRecordCountPerPage());
        body.put("pageOffset", (dto.getPageIndex()-1) * dto.getRecordCountPerPage());
        body.put("pageIdx", dto.getPageIndex());
        body.put("searchKeyword", dto.getSearchKeyword());
        log.info(this.getClass().toString(),body);

        // url을 구분하여 권한 검색 조건 분리 및 request body 조건 추가
        if(division.equals("doc")) {
            body.put("userRoleFk" ,"3");
        }else if(division.equals("par")) {
            body.put("userRoleFk","2");
        }else if(division.equals("user")) {
            body.put("userRoleFk","1");
            if(!session.getAttribute("userRoleFk").equals("4")) {
                body.put("userId",session.getAttribute("userId"));
            }
        }
        String str = (String) GatewayUtils.post(new URL(uri+version+"/list"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if(result.get("resultCode").equals("0000")) {
            result = (Map<String, Object>) result.get("resultData");
            for(Map<String, Object> d : (ArrayList<Map<String, Object>>)result.get("list")) {
                d.put("birthEnc", AES256Util.decrypt((String) d.get("birthEnc")));
                d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                list.add(d);
            }
        }
        model.addAttribute("paginationInfo", pagingUtil.getPageInfo(dto,(int) result.get("totalCount")));
        model.addAttribute("list", list);
        result.remove("list");
        model.addAttribute("result", result);

        return "/userInfo/list";
    }

    /**
     * @apiNote 사용자 목록 view
     * @param UserDTO = 유저 dto
     * @param PaginationInfo: 조회 조건, 결과 값 페이징 관련
     * @return view
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/manage_userList")
    public String userlist(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session,  UserDTO dto) throws Exception{

        model.addAttribute("uri", req.getRequestURI());

        JSONObject body = new JSONObject();
        body.put("recordCountPerPage", dto.getRecordCountPerPage());
        body.put("pageOffset", (dto.getPageIndex()-1) * dto.getRecordCountPerPage());
        body.put("pageIdx", dto.getPageIndex());
        body.put("searchKeyword", dto.getSearchKeyword());
        log.info(this.getClass().toString(),body);

        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));

        String str = (String) GatewayUtils.post(new URL(uri+version+"/manage_userList"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());


        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if(result.get("resultCode").equals("0000")) {
            result = (Map<String, Object>) result.get("resultData");
            String birth="";
            int userAge;
            for(Map<String, Object> d : (ArrayList<Map<String, Object>>)result.get("list")) {
                birth = AES256Util.decrypt((String) d.get("birthEnc"));
                d.put("birthEnc", birth);
                userAge = 2024- Integer.parseInt(birth.substring(0,4));
                d.put("userAge", userAge);
                d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                d.put("age", userAge);

                list.add(d);
            }
        }

        log.info("ash list : " + list);
        model.addAttribute("paginationInfo", pagingUtil.getPageInfo(dto,(int) result.get("totalCount")));
        model.addAttribute("list", list);
        result.remove("list");
        model.addAttribute("result", result);

        return "/userInfo/user_manage_list";
    }

    @GetMapping ({"/userBoardInfo", "/analysis", "/community"})
    public String userInfo(HttpServletRequest req, HttpServletResponse res, HttpSession session, Model model, @RequestParam(defaultValue = "") String userinfoId) throws Exception {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        JSONObject body = new JSONObject();

        if(userinfoId.isEmpty() || userinfoId.equals("")) {
            body.put("userId", session.getAttribute("userId"));
        }
        else {
            body.put("userId", userinfoId);
        }

        body.put("userRoleFk", "1");
        String str = null;
        try {
            str = (String) GatewayUtils.post(new URL(uri+version+"/userBoardInfo"),
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());
            ObjectMapper obj = new ObjectMapper();

            JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);
            JSONObject userInfo = new JSONObject();
            JSONArray userRolelist = new JSONArray();

            Map<String,Object>  userHealthavg = null;
            int age = 0, bodyAge = 0 ,weight = 0 , height = 0;
            log.info("ash bio info " + str);

            if(!result.isNull("resultData")) {
                result = (JSONObject) result.get("resultData");
                if(!result.isNull("userBioinfo")) {
                    userInfo = (JSONObject) result.get("userBioinfo");
                }
                if(!result.isNull("userRolelist")) {
                    userRolelist = (JSONArray) result.get("userRolelist");
                }
                if(!result.isNull("userHealthavg")) {
                    userHealthavg = obj.readValue( result.get("userHealthavg").toString(), HashMap.class);
                }
            }

            for(String key : userInfo.keySet()) {
                if (key.equals("birthEnc")){
                    String birthDateString= AES256Util.decrypt((String) userInfo.get(key));
                    model.addAttribute(key,birthDateString);
                    LocalDate birthDate = LocalDate.parse(birthDateString);
                    LocalDate currentDate = LocalDate.now();

                    // 나이 계산
                    age = Period.between(birthDate, currentDate).getYears();

                    session.setAttribute("ageAvg", age);
                }
                else if(key.equals("telNumEnc")){
                    model.addAttribute(key, AES256Util.decrypt((String) userInfo.get(key)));
                }else if(key.equals("gender")) {

                    model.addAttribute(key, userInfo.get(key));
                    session.setAttribute("gender", userInfo.get(key));
                }
                else if(key.equals("height")) {
                    height = Integer.parseInt((String) userInfo.get(key));
                    model.addAttribute(key, userInfo.get(key));
                }
                else if(key.equals("weight")) {
                    weight = Integer.parseInt((String) userInfo.get(key));
                    model.addAttribute(key, userInfo.get(key));
                }
                else{
                    model.addAttribute(key, userInfo.get(key));
                }
            }

            ArrayList<Map<String,Object>>  list = null ;
            String phonenum = "" ;
            if(!userRolelist.isEmpty()) {
                list = obj.readValue(userRolelist.toString(), ArrayList.class);

                for(Map<String,Object> map :list) {	//
                    // 보호자 정보 중 암호화된 값 복호화
                    if (map.containsKey("telNumEnc")) {
                        phonenum = AES256Util.decrypt((String) map.get("telNumEnc"));
                        map.put("telNumEnc", phonenum.substring(0, 3) + "-" + phonenum.substring(3, 7) + "-" + phonenum.substring(7));
                    }
                }
            }

            model.addAttribute("searchUserId", session.getAttribute("userId"));
            session.setAttribute("UserinfoId", userinfoId);

            log.info("ash found id " + userinfoId);
            model.addAttribute("userRolelist", list);
            model.addAttribute("userHealthavg", userHealthavg);


            if(req.getRequestURI().contains("analysis"))
            {
                return "/health/analysis";
            }
            else if(req.getRequestURI().contains("community"))
            {
                body.put("age", age);
                str = (String) GatewayUtils.post(new URL("http://localhost:8085/community/v1/findBoardlist"),
                        GatewayUtils.tokenCheck(session, res),
                        body.toString());

                log.info("ash ash result " + str);
                JSONObject commuResult = str.isEmpty() ? new JSONObject() : new JSONObject(str);
                List<Map<String, Object>> commulist = null;
                if(!commuResult.isNull("resultData")) {
                    commulist = obj.readValue(commuResult.get("resultData").toString(), ArrayList.class);
                }
                model.addAttribute("commulist", commulist);
                bodyAge = age;
                if(((height * height) / weight) * 10000 < 25) { //BMI 지수가 비만보다 낮아야됨
                    bodyAge -= 5;
                }else{
                    bodyAge += 1;
                }

                model.addAttribute("bodyAge", bodyAge);

                return "/community/communitymain";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "/user/dashboard";
    }

    /**
     * @apiNote 내정보 조회 view
     * @return view
     */
    @GetMapping("/mypage")
    public String mypage(HttpServletRequest req, HttpServletResponse res, HttpSession session, Model model) throws Exception {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));
        String str = (String) GatewayUtils.post(new URL(uri+version+"/userInfo"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);

        if(!result.isNull("resultData")) {
            result = (JSONObject) result.get("resultData");
        }

        for(String key : result.keySet()) {
            if(!key.equals("userRoleFk")) {
                if(key.equals("birthEnc") || key.equals("telNumEnc"))
                    model.addAttribute(key, AES256Util.decrypt((String) result.get(key)));
                else if (key.equals("guardian")) {	// guardian:보호자(JSONObject)
                    ObjectMapper obj = new ObjectMapper();
                    ArrayList<Map<String,Object>> arr = obj.readValue(result.get("guardian").toString(), ArrayList.class);;
                    if(arr != null && arr.size()>0) {
                        String guardianNm = "";
                        List<Map<String, Object>> resarr = new ArrayList<Map<String, Object>>();
                        int count = 0;
                        ArrayList<String> gardianNmArr = new ArrayList<String>();
                        for(Map<String,Object> map :arr) {	// 보호자 정보 중 암호화된 값 복호화
                            map.put("telNumEnc", AES256Util.decrypt((String) map.get("telNumEnc")));
                            resarr.add(map);
                            if(count != 0)
                                guardianNm += ", ";
                            guardianNm += (String) map.get("userNm")+" "+(String)map.get("telNumEnc");
                            count++;
                            gardianNmArr.add((String) map.get("userNm"));
                        }
                        model.addAttribute("guardianNm", guardianNm);
                        model.addAttribute("gardianNmArr", gardianNmArr);
                        result.put(key, resarr);
                        model.addAttribute(key, result.get(key));
                    }
                }else
                    model.addAttribute(key, result.get(key));
            }
        }

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        model.addAttribute("searchUserId", session.getAttribute("userId"));

        return "/user/mypage_new";
    }

    /**
     * @apiNote 담당자(의사) 조회(ajax)
     * @return Map
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/doctorSearch")
    @ResponseBody
    public Map<String, Object> doctorSearch(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session, UserDTO dto, @RequestParam Map<String, Object> param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("userRoleFk", param.get("userRoleFk"));
        body.put("userNm", param.get("doctorNm"));
        String str = (String) GatewayUtils.post(new URL(uri+version+"/searchDoctor"), GatewayUtils.tokenCheck(session, res), body.toString());
        ObjectMapper obj = new ObjectMapper();

        map = obj.readValue(str, Map.class);
        // 1003: 인증 정보가 잘못된 상태 (인터페이스 정의서 참고)
        if(map.get("resultCode").equals("1003")) {
            session.removeAttribute("acToken");
            session.removeAttribute("rfToken");
        }
        return map;

    }

    /**
     * @apiNote 보호자 조회(ajax)
     * @return Map
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/parentSearch")
    @ResponseBody
    public Map<String, Object> parentSearch(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session, UserDTO dto, @RequestParam Map<String, Object> map) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userNm", map.get("parentNm"));
        body.put("birthEnc", AES256Util.encrypt((String) map.get("parentBirthEnc")));	// 평문 암호화
        body.put("telNumEnc",AES256Util.encrypt((String) map.get("parentTelNumEnc")));	// 평문 암호화
        String str = (String) GatewayUtils.post(new URL(uri+version+"/searchParent"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map <String, Object> result = new HashMap<String, Object>();
        result = obj.readValue(str, Map.class);	// ObjectMapper 이용하여 결과 값 Map pasing
        map = (Map<String, Object>) result.get("resultData");

        ArrayList<Map<String, Object>> arr = new ArrayList<Map<String,Object>>();
        if(map != null) {
            if(map.get("list") != null) {
                for(Map<String, Object> d : (ArrayList<Map<String, Object>>)map.get("list")) {
                    d.put("telNumEnc", AES256Util.decrypt((String)d.get("telNumEnc")));	// 복호화
                    d.put("birthEnc", AES256Util.decrypt((String)d.get("birthEnc")));	// 복호화
                    arr.add(d);
                }
                result.put("resultData", arr);
            }
        }
        return result;
    }

    /**
     * @apiNote 내정보 수정 view
     * @return view
     */
    @PostMapping("/uptUserInfo")
    public String uptUserInfo(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session) throws Exception {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));
        String str = null;
        try {
            str = (String) GatewayUtils.post(new URL(uri+version+"/userInfo"),
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = str.isEmpty() ? new JSONObject() : new JSONObject(str);

        if(!result.isNull("resultData")) {
            result = (JSONObject) result.get("resultData");
        }
        for(String key : result.keySet()) {
            if(!key.equals("userRoleFk")) {
                if(key.equals("birthEnc") || key.equals("telNumEnc"))
                    model.addAttribute(key, AES256Util.decrypt((String) result.get(key)));
                else if (key.equals("guardian")) {
                    ObjectMapper obj = new ObjectMapper();
                    ArrayList<Map<String,Object>> arr = obj.readValue(result.get("guardian").toString(), ArrayList.class);	// 결과 값 ArrayList pasing
                    ArrayList<String> gardianNmArr = new ArrayList<String>();
                    if(arr != null && arr.size()>0) {
                        String guardianNm = "";
                        List<Map<String, Object>> resarr = new ArrayList<Map<String, Object>>();
                        int count = 0;
                        for(Map<String,Object> map :arr) {
                            map.put("telNumEnc", AES256Util.decrypt((String) map.get("telNumEnc")));	//복호화
                            resarr.add(map);
                            if(count != 0)
                                guardianNm += ", ";
                            guardianNm += (String) map.get("userNm")+" "+(String)map.get("telNumEnc");
                            count++;
                            gardianNmArr.add((String) map.get("userNm"));
                        }
                        model.addAttribute("guardianNm", guardianNm);
                        model.addAttribute("gardianNmArr", gardianNmArr);
                        result.put(key, resarr);
                        model.addAttribute(key, result.get(key));
                    }
                }else
                    model.addAttribute(key, result.get(key));
            }
        }
        return "/user/updateuser";
    }

    /**
     * @apiNote 내정보 수정 처리
     * @return view
     */
    @PostMapping("/uptUserInfoAct")
    public String uptUserInfoAct(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session, @RequestParam Map<String, Object> map) {

        model.addAttribute("uri", req.getRequestURI());
        model.addAttribute("url", req.getRequestURL());

        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));
        for(String key : map.keySet()) {
            if(key.equals("birthEnc") || key.equals("telNumEnc"))
                body.put(key, AES256Util.encrypt((String) map.get(key)));	// 평문 암호화
            else
                body.put(key, map.get(key));
        }
        String str = null;
        try {
            str = (String) GatewayUtils.post(new URL(uri+version+"/updateUserInfo"),
                    GatewayUtils.tokenCheck(session, res),
                    body.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/userInfo/mypage";
    }

    /**
     * @apiNote 비밀번호 변경(ajax)
     * @return Map
     */
    @PostMapping("/updatePasswd")
    @ResponseBody
    public Map<String, Object> updatePasswd(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session, @RequestParam Map<String, Object> map) throws Exception {
        Map<String, Object> result = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));

        for(String key : map.keySet()) {
            if(key.equals("userPwEnc") || key.equals("newUserPwEnc"))
                body.put(key, Sha256.encryt((String) map.get(key)));	// sha-256 암호화
            else
                body.put(key, map.get(key));
        }

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri+version+"/updatePasswd"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        ObjectMapper obj = new ObjectMapper();
        result = obj.readValue(str, Map.class);		// 결과 값 Map parsing
        if(result.get("resultCode").equals("1003")) {	// 코드 인터페이스 정의서 참고
            session.removeAttribute("acToken");
            session.removeAttribute("rfToken");
        }else if(result.get("resultCode").equals("0000")) {
            for(String key : session.getValueNames()) {
                session.removeAttribute(key);
            }
        }
        return result;
    }

    /**
     * @apiNote 회원 탈퇴(ajax)
     * @return Map
     */
    @PostMapping("/secession")
    @ResponseBody
    public Map<String, Object> secession(HttpServletRequest req, HttpServletResponse res, Model model, HttpSession session) throws Exception {
        Map<String, Object> result = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("userId", session.getAttribute("userId"));
        body.put("userRoleFk", session.getAttribute("userRoleFk"));

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri+version+"/deleteUserInfo"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());
        ObjectMapper obj = new ObjectMapper();
        result = obj.readValue(str, Map.class);		// 결과 값 Map parsing
        if(result.get("resultCode").equals("1003")) { // 코드 인터페이스 정의서 참고
            session.removeAttribute("acToken");
            session.removeAttribute("rfToken");
        }
        if(result.get("resultCode").equals("0000")) {
            for(String key : session.getValueNames()) {
                session.removeAttribute(key);
            }
        }
        return result;
    }

    @PostMapping("/drguardianList")
    @ResponseBody
    public Object drguardianList(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,Model model, HttpSession session) throws Exception {

        JSONObject body = new JSONObject(map);

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri+version+"/drguardianList"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);


        ArrayList<Map<String,Object>> list = new ArrayList<>(); //[{userNm="",email=""...}]

        if(result.get("resultCode").equals("0000")) {

            if (ObjectUtils.isEmpty(result.get("resultData"))) { //관련된 사용자가 없어 빈객체만 전달되었는지 확인
                // user_role, dept_nm, user_nm, email, tel_num_enc
                Map<String, Object> a = new HashMap<>();
                a.put("userNm" ," ");
                a.put("email", "Healthcare@naver.com");
                a.put("telNumEnc", "01030641230" );
                a.put("userId", "1" );

                list.add(a);
            }
            else {
                list = (ArrayList<Map<String, Object>>)result.get("resultData");

                for (Map<String, Object> d : list) {
                    // 각 객체의 "telNumEnc" 값을 복호화
                    if(d.containsKey("telNumEnc")) {
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
    public String searchuserList(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map, Model model, HttpSession session) {

        JSONObject body = new JSONObject(map);
        String url = "";
        url = "/search_userList";

        try {
            String str = (String) GatewayUtils.post(new URL(uri+version+url), GatewayUtils.tokenCheck(session, res), body.toString());
            log.info("ash searchuserlist: " + str);
            ObjectMapper obj = new ObjectMapper();
            Map<String, Object> result = obj.readValue(str, Map.class);

            ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            if(result.get("resultCode").equals("0000")) {
                result = (Map<String, Object>) result.get("resultData");
                for(Map<String, Object> d : (ArrayList<Map<String, Object>>)result.get("list")) {
                    d.put("birthEnc", AES256Util.decrypt((String) d.get("birthEnc")));
                    d.put("telNumEnc", AES256Util.decrypt((String) d.get("telNumEnc")));
                    list.add(d);
                }
            }

            if(result.containsKey("resultCode") && result.get("resultCode").equals("1003")) {
                session.removeAttribute("acToken");
                session.removeAttribute("rfToken");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/userInfo/user_add_detail";
    }

    @PostMapping("/searchUser")
    @ResponseBody
    public Object searchUser(HttpServletRequest req, HttpServletResponse res, @RequestParam Map<String, Object> map,Model model, HttpSession session) throws Exception {

        JSONObject body = new JSONObject(map);

        String str = null;
        str = (String) GatewayUtils.post(new URL(uri+version+"/search_userList"),
                GatewayUtils.tokenCheck(session, res),
                body.toString());

        ObjectMapper obj = new ObjectMapper();
        Map<String, Object> result = obj.readValue(str, Map.class);


        ArrayList<Map<String,Object>> list = new ArrayList<>(); //[{userNm="",email=""...}]

        log.info("ash result userlist" + str);
        if(result.get("resultCode").equals("0000")) {

            if (ObjectUtils.isEmpty(result.get("resultData"))) { //관련된 사용자가 없어 빈객체만 전달되었는지 확인
                // user_role, dept_nm, user_nm, email, tel_num_enc

            }
            else {
                list = (ArrayList<Map<String, Object>>)result.get("resultData");
                String birthDateString = "";
                int age = 0 ;
                for (Map<String, Object> d : list) {
                    // 각 객체의 "telNumEnc" 값을 복호화
                    if(d.containsKey("birthEnc")) {
                        birthDateString = AES256Util.decrypt((String) d.get("birthEnc"));
                        d.put("birthEnc", birthDateString);
                        if(!(birthDateString.isEmpty() && birthDateString.equals(""))){
                            LocalDate birthDate = LocalDate.parse(birthDateString);
                            LocalDate currentDate = LocalDate.now();
                            age = Period.between(birthDate, currentDate).getYears();
                            // 나이 계산
                            d.put("age" , age );
                        }

                    }
                    if(d.containsKey("deptNm")){
                        if(d.get("deptNm") == null) {
                            d.put("deptNm", "healthcare");
                        }
                    }
                }
            }
        }
        return list;
    }
}
