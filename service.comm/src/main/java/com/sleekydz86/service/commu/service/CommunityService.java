package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;

import java.util.List;
import java.util.Map;

public interface CommunityService {
    int writeBoard(Community community);

    Community findBoard(int commuSeq);

    List<Community> findBoardList(Map<String,Object> map);
    
    int updateBoard(Community community);
    
    int deleteBoard(int commuSeq);
}