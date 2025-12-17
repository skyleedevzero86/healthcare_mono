package com.sleekydz86.service.commu.service;

import com.sleekydz86.service.commu.entity.Community;
import com.sleekydz86.service.commu.entity.Usermng;
import com.sleekydz86.service.commu.metrics.CommunityMetrics;
import com.sleekydz86.service.commu.repository.CommunityRepository;
import com.sleekydz86.service.commu.repository.UserRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMetrics communityMetrics;

    @Override
    @Transactional
    @CacheEvict(value = "community", allEntries = true)
    public int writeBoard(Community community) {
        String requestId = UUID.randomUUID().toString();
        String userId = community.getUserId() != null ? String.valueOf(community.getUserId()) : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "writeBoard");
        
        communityMetrics.incrementBoardPostsCreated();
        Timer.Sample sample = communityMetrics.startBoardPostProcessingTimer();
        
        try {
            log.info("게시글 생성 중: {}", userId);
            
            int result = communityRepository.writeBoard(community);
            
            if (result > 0) {
                log.info("게시글 생성 완료: 사용자 {}, 게시글 번호: {}", userId, community.getCommuSeq());
            } else {
                log.warn("게시글 생성 실패 - 데이터베이스 삽입 결과 0: {}", userId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생: 사용자 {}", userId, e);
            throw e;
        } finally {
            sample.stop(communityMetrics.getBoardPostProcessingTime());
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "community", key = "#commuSeq")
    public Community findBoard(int commuSeq) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("commuSeq", String.valueOf(commuSeq));
        MDC.put("requestId", requestId);
        MDC.put("operation", "findBoard");
        
        communityMetrics.incrementBoardPostsRead();
        Timer.Sample sample = communityMetrics.startBoardQueryTimer();
        
        try {
            log.info("게시글 조회 중: {}", commuSeq);
            
            Community result = communityRepository.findBoard(commuSeq);
            
            if (result != null) {
                log.info("게시글 조회 완료: {}", commuSeq);
            } else {
                log.warn("게시글을 찾을 수 없음: {}", commuSeq);
            }
            
            return result;
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생: {}", commuSeq, e);
            throw e;
        } finally {
            sample.stop(communityMetrics.getBoardQueryTime());
            MDC.clear();
        }
    }

    @Override
    @Cacheable(value = "community", key = "'list_' + #map['pageIdx'] + '_' + (#map['searchKeyword'] != null ? #map['searchKeyword'] : '')")
    public List<Community> findBoardList(Map<String,Object> map) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "findBoardList");
        MDC.put("pageIdx", map.get("pageIdx") != null ? map.get("pageIdx").toString() : "unknown");
        
        communityMetrics.incrementBoardListQueries();
        Timer.Sample sample = communityMetrics.startBoardQueryTimer();
        
        try {
            log.info("게시글 목록 조회 중, 페이지: {}", map.get("pageIdx"));
            
            List<Community> result = communityRepository.findBoardList(map);
            
            log.info("게시글 목록 조회 완료, 결과 크기: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            throw e;
        } finally {
            sample.stop(communityMetrics.getBoardQueryTime());
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "community", allEntries = true)
    public int updateBoard(Community community) {
        String requestId = UUID.randomUUID().toString();
        String userId = community.getUserId() != null ? String.valueOf(community.getUserId()) : "unknown";
        MDC.put("userId", userId);
        MDC.put("requestId", requestId);
        MDC.put("operation", "updateBoard");
        MDC.put("commuSeq", String.valueOf(community.getCommuSeq()));
        
        communityMetrics.incrementBoardPostsUpdated();
        Timer.Sample sample = communityMetrics.startBoardPostProcessingTimer();
        
        try {
            log.info("게시글 수정 중: 사용자 {}, 게시글 번호: {}", userId, community.getCommuSeq());
            
            int result = communityRepository.updateBoard(community);
            
            if (result > 0) {
                log.info("게시글 수정 완료: 사용자 {}, 게시글 번호: {}", userId, community.getCommuSeq());
            } else {
                log.warn("게시글 수정 실패 - 게시글을 찾을 수 없음: {}", community.getCommuSeq());
            }
            
            return result;
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: 사용자 {}, 게시글 번호: {}", userId, community.getCommuSeq(), e);
            throw e;
        } finally {
            sample.stop(communityMetrics.getBoardPostProcessingTime());
            MDC.clear();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "community", allEntries = true)
    public int deleteBoard(int commuSeq) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", "deleteBoard");
        MDC.put("commuSeq", String.valueOf(commuSeq));
        
        communityMetrics.incrementBoardPostsDeleted();
        Timer.Sample sample = communityMetrics.startBoardPostProcessingTimer();
        
        try {
            log.info("게시글 삭제 중: {}", commuSeq);
            
            int result = communityRepository.deleteBoard(commuSeq);
            
            if (result > 0) {
                log.info("게시글 삭제 완료: {}", commuSeq);
            } else {
                log.warn("게시글 삭제 실패 - 게시글을 찾을 수 없음: {}", commuSeq);
            }
            
            return result;
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: {}", commuSeq, e);
            throw e;
        } finally {
            sample.stop(communityMetrics.getBoardPostProcessingTime());
            MDC.clear();
        }
    }
}