package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    CommunityRepository communityRepository;

    @Override
    @Transactional(readOnly = false)
    public int writeBoard(Community community) {
        return communityRepository.writeBoard(community);
    }

    @Override
    public Community findBoard(Long commuid) {
        return communityRepository.findBoard(commuid);
    }

    @Override
    @Transactional
    public List<Community> findBoardList() {
        return communityRepository.findBoardList();
    }
}