package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;

import java.util.List;

public interface CommunityService {
    public int writeBoard(Community community);

    public Community findBoard(int commuSeq);

    public List<Community> findBoardList();
}