package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    CommunityRepository communityRepository;

    @Override
    public int writeBoard(Community community) throws Exception {
        return communityRepository.writeBoard(community);
    }

    @Override
    public Community findBoard(Long id) {
        return communityRepository.findBoard(id);
    }

    @Override
    public List<Community> findBoardList() {
        return communityRepository.findBoardList();
    }
}