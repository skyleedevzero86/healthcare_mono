package com.sleekydz86.service.commu.repository;

import com.sleekydz86.service.commu.entity.Community;
import com.sleekydz86.service.commu.exception.BusinessException;
import com.sleekydz86.service.commu.dto.ApiResultCode;
import com.sleekydz86.service.commu.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final CommunityMapper communityMapper;

    @Transactional
    public int writeBoard(Community community) {
        try {
            log.debug("게시글 작성 시작: userId={}", community.getUserId());
            if (community.getRegDate() == null) {
                community.beforePersist();
            }
            communityMapper.writeBoard(community);
            log.info("게시글 작성 완료: commuSeq={}, userId={}", 
                    community.getCommuSeq(), community.getUserId());
            return community.getCommuSeq();
        } catch (Exception e) {
            log.error("게시글 작성 실패: userId={}", community.getUserId(), e);
            throw new BusinessException(
                "게시글 작성 중 오류가 발생했습니다: " + e.getMessage(), 
                e, 
                ApiResultCode.INSERT_FAIL
            );
        }
    }

    public Community findBoard(int commuSeq) {
        try {
            log.debug("게시글 조회: commuSeq={}", commuSeq);
            Community community = communityMapper.findBoard(commuSeq);
            if (community == null) {
                log.warn("게시글을 찾을 수 없음: commuSeq={}", commuSeq);
            }
            return community;
        } catch (Exception e) {
            log.error("게시글 조회 실패: commuSeq={}", commuSeq, e);
            throw new BusinessException(
                "게시글 조회 중 오류가 발생했습니다: " + e.getMessage(),
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        }
    }

    public List<Community> findBoardList(Map<String, Object> map) {
        try {
            log.debug("게시글 목록 조회: 조건={}", map);
            
            int pageIdx = map.containsKey("pageIdx") && map.get("pageIdx") != null
                    ? Integer.parseInt(map.get("pageIdx").toString())
                    : 0;
            int pageSize = map.containsKey("pageSize") && map.get("pageSize") != null
                    ? Integer.parseInt(map.get("pageSize").toString())
                    : 10;
            
            map.put("pageIdx", pageIdx);
            map.put("pageSize", pageSize);

            List<Community> result = communityMapper.findBoardList(map);
            log.info("게시글 목록 조회 완료: 결과 수={}", result.size());
            return result;
            
        } catch (NumberFormatException e) {
            log.error("페이징 파라미터 형식 오류: {}", map, e);
            throw new BusinessException(
                "잘못된 페이징 파라미터입니다.",
                e,
                ApiResultCode.PARAM_VALID_ERR
            );
        } catch (Exception e) {
            log.error("게시글 목록 조회 실패: 조건={}", map, e);
            throw new BusinessException(
                "게시글 목록 조회 중 오류가 발생했습니다: " + e.getMessage(),
                e,
                ApiResultCode.UNKNOWN_ERR
            );
        }
    }

    @Transactional
    public int updateBoard(Community community) {
        try {
            log.debug("게시글 수정 시작: commuSeq={}", community.getCommuSeq());
            
            Community existing = communityMapper.findBoard(community.getCommuSeq());
            if (existing == null) {
                log.warn("수정할 게시글을 찾을 수 없음: commuSeq={}", community.getCommuSeq());
                throw new BusinessException(
                    "게시글을 찾을 수 없습니다.",
                    ApiResultCode.RESULT_IS_EMPTY
                );
            }
            
            int result = communityMapper.updateBoard(community);
            log.info("게시글 수정 완료: commuSeq={}", community.getCommuSeq());
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("게시글 수정 실패: commuSeq={}", community.getCommuSeq(), e);
            throw new BusinessException(
                "게시글 수정 중 오류가 발생했습니다: " + e.getMessage(),
                e,
                ApiResultCode.UPDATE_FAIL
            );
        }
    }

    @Transactional
    public int deleteBoard(int commuSeq) {
        try {
            log.debug("게시글 삭제 시작: commuSeq={}", commuSeq);
            
            Community community = communityMapper.findBoard(commuSeq);
            if (community == null) {
                log.warn("삭제할 게시글을 찾을 수 없음: commuSeq={}", commuSeq);
                throw new BusinessException(
                    "게시글을 찾을 수 없습니다.",
                    ApiResultCode.RESULT_IS_EMPTY
                );
            }
            
            int result = communityMapper.deleteBoard(commuSeq);
            log.info("게시글 삭제 완료: commuSeq={}", commuSeq);
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("게시글 삭제 실패: commuSeq={}", commuSeq, e);
            throw new BusinessException(
                "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage(),
                e,
                ApiResultCode.DELETE_FAIL
            );
        }
    }
}