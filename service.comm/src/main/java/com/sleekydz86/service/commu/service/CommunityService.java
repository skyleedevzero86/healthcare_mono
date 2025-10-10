package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;

import java.util.List;

public interface CommunityService {
    public int writeBoard(Community community) throws Exception;
    public Community findBoard(Long commuId);
    public List<Community> findBoardList();
}