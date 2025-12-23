package com.sleekydz86.service.llm.infrastructure.adapter.auxiliary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.llm.config.KmBertProperties;
import com.sleekydz86.service.llm.domain.model.MedicalEntities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KmBertService {

    private final RestTemplate restTemplate;
    private final KmBertProperties kmBertProperties;
    private final ObjectMapper objectMapper;

    public float[] getEmbedding(String text) {
        if (!kmBertProperties.isEnabled() || text == null || text.isEmpty()) {
            return null;
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (kmBertProperties.getApiKey() != null && !kmBertProperties.getApiKey().isEmpty()) {
                headers.setBearerAuth(kmBertProperties.getApiKey());
            }

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
            String serverUrl = kmBertProperties.getBaseUrl() + "/pipeline/feature-extraction";

            log.debug("KM-BERT 임베딩 요청: URL={}, Text length={}", serverUrl, text.length());

            ResponseEntity<String> response = restTemplate.exchange(
                    serverUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("KM-BERT 서버 응답 오류: status={}", response.getStatusCode());
                return null;
            }

            return parseEmbeddingResponse(response.getBody());

        } catch (RestClientException e) {
            log.error("KM-BERT 서버 통신 오류", e);
            return null;
        } catch (Exception e) {
            log.error("KM-BERT 임베딩 생성 중 오류", e);
            return null;
        }
    }

    public MedicalEntities extractMedicalEntities(String text) {
        if (!kmBertProperties.isEnabled() || text == null || text.isEmpty()) {
            return MedicalEntities.builder()
                    .diseases(new ArrayList<>())
                    .medications(new ArrayList<>())
                    .symptoms(new ArrayList<>())
                    .bodyParts(new ArrayList<>())
                    .metadata(new HashMap<>())
                    .build();
        }

        float[] embedding = getEmbedding(text);
        if (embedding == null) {
            return MedicalEntities.builder()
                    .diseases(new ArrayList<>())
                    .medications(new ArrayList<>())
                    .symptoms(new ArrayList<>())
                    .bodyParts(new ArrayList<>())
                    .metadata(new HashMap<>())
                    .build();
        }

        return extractEntitiesFromText(text, embedding);
    }

    public boolean isMedicalRelated(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        String[] medicalKeywords = {
            "병", "질환", "증상", "약", "치료", "진단", "의사", "병원",
            "혈압", "심박수", "체온", "콜레스테롤", "혈당", "인슐린",
            "고혈압", "당뇨", "감기", "두통", "복통", "발열"
        };

        for (String keyword : medicalKeywords) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private float[] parseEmbeddingResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            if (root.isArray() && root.size() > 0) {
                JsonNode embeddingArray = root.get(0);
                if (embeddingArray.isArray()) {
                    float[] embedding = new float[embeddingArray.size()];
                    for (int i = 0; i < embeddingArray.size(); i++) {
                        embedding[i] = (float) embeddingArray.get(i).asDouble();
                    }
                    return embedding;
                }
            }

            return null;
        } catch (Exception e) {
            log.error("KM-BERT 임베딩 파싱 오류", e);
            return null;
        }
    }

    private MedicalEntities extractEntitiesFromText(String text, float[] embedding) {
        List<String> diseases = new ArrayList<>();
        List<String> medications = new ArrayList<>();
        List<String> symptoms = new ArrayList<>();
        List<String> bodyParts = new ArrayList<>();

        String[] diseaseKeywords = {"고혈압", "당뇨", "감기", "독감", "천식", "알레르기", "관절염", "골다공증"};
        String[] medicationKeywords = {"아스피린", "타이레놀", "이부프로펜", "항생제", "인슐린", "혈압약"};
        String[] symptomKeywords = {"두통", "복통", "발열", "기침", "콧물", "인후통", "어지러움", "피로"};
        String[] bodyPartKeywords = {"머리", "가슴", "배", "팔", "다리", "목", "등", "어깨"};

        String lowerText = text.toLowerCase();

        for (String keyword : diseaseKeywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                diseases.add(keyword);
            }
        }

        for (String keyword : medicationKeywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                medications.add(keyword);
            }
        }

        for (String keyword : symptomKeywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                symptoms.add(keyword);
            }
        }

        for (String keyword : bodyPartKeywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                bodyParts.add(keyword);
            }
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("embedding_dimension", embedding != null ? embedding.length : 0);
        metadata.put("text_length", text.length());

        return MedicalEntities.builder()
                .diseases(diseases)
                .medications(medications)
                .symptoms(symptoms)
                .bodyParts(bodyParts)
                .metadata(metadata)
                .build();
    }
}

