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
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {


    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional//읽기전용으로 하면 데이터 변경이 안됨.
    public int writeBoard(Community community) {
       // Usermng um = userRepository.findOne(community.getUserId());
       // community.setUserNm(um.getUser_nm());
        return communityRepository.writeBoard(community);
    }

    @Override
    public Community findBoard(int commuSeq) {
        return communityRepository.findBoard(commuSeq);
    }

    @Override
    @Transactional
    public List<Community> findBoardList(Map<String,Object> map) {
        return communityRepository.findBoardList(map);
    }
}