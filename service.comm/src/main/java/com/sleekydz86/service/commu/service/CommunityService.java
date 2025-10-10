package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;

public interface CommunityService {
    public int writeBoard(Community community) throws Exception;
    public Community findBoard(Long id) ;
}