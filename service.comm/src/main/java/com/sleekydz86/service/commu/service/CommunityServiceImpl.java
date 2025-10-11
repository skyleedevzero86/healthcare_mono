package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.domain.Community;
import com.sleekydz86.service.commu.domain.Usermng;
import com.sleekydz86.service.commu.repository.CommunityRepository;
import com.sleekydz86.service.commu.repository.UserRepository;
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


    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = false)
    public int writeBoard(Community community) {
        Usermng um = userRepository.findOne(community.getUserId());
        community.setUserNm(um.getUser_nm());
        return communityRepository.writeBoard(community);
    }

    @Override
    public Community findBoard(int commuSeq) {
        return communityRepository.findBoard(commuSeq);
    }

    @Override
    @Transactional
    public List<Community> findBoardList() {
        return communityRepository.findBoardList();
    }
}