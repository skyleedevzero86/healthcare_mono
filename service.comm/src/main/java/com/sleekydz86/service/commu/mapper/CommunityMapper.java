package com.sleekydz86.service.commu.mapper;

import com.sleekydz86.service.commu.entity.Community;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommunityMapper {
    int writeBoard(Community community);
    Community findBoard(@Param("commuSeq") int commuSeq);
    List<Community> findBoardList(Map<String, Object> map);
    int updateBoard(Community community);
    int deleteBoard(@Param("commuSeq") int commuSeq);
}

