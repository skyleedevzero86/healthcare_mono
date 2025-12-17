package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import com.sleekydz86.service.healthcare.service.cache.CacheService;
import com.sleekydz86.service.healthcare.service.sharding.PatientShardingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService 단위 테스트")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientShardingService shardingService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("Test Patient");
    }

    @Test
    @DisplayName("환자 생성 성공")
    void createPatient_Success() {
        when(shardingService.createPatient(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(patient);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(shardingService, times(1)).createPatient(patient);
        verify(cacheService, times(1)).cachePatientData(patient);
    }

    @Test
    @DisplayName("환자 조회 성공 - 캐시에서")
    void getPatient_Success_FromCache() {
        when(cacheService.getCachedPatient(1L)).thenReturn(patient);

        Patient result = patientService.getPatient(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(cacheService, times(1)).getCachedPatient(1L);
        verify(shardingService, never()).findPatientById(anyLong());
    }

    @Test
    @DisplayName("환자 조회 성공 - DB에서")
    void getPatient_Success_FromDB() {
        when(cacheService.getCachedPatient(1L)).thenReturn(null);
        when(shardingService.findPatientById(1L)).thenReturn(patient);

        Patient result = patientService.getPatient(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(cacheService, times(1)).getCachedPatient(1L);
        verify(shardingService, times(1)).findPatientById(1L);
        verify(cacheService, times(1)).cachePatientData(patient);
    }

    @Test
    @DisplayName("환자 조회 실패 - 없음")
    void getPatient_NotFound() {
        when(cacheService.getCachedPatient(1L)).thenReturn(null);
        when(shardingService.findPatientById(1L)).thenReturn(null);

        Patient result = patientService.getPatient(1L);

        assertThat(result).isNull();
        verify(cacheService, times(1)).getCachedPatient(1L);
        verify(shardingService, times(1)).findPatientById(1L);
        verify(cacheService, never()).cachePatientData(any());
    }

    @Test
    @DisplayName("환자 수정 성공")
    void updatePatient_Success() {
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setName("Updated Patient");

        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        Patient result = patientService.updatePatient(patient);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Patient");
        verify(patientRepository, times(1)).save(patient);
        verify(cacheService, times(1)).cachePatientData(updatedPatient);
    }

    @Test
    @DisplayName("환자 삭제 성공")
    void deletePatient_Success() {
        doNothing().when(patientRepository).deleteById(1L);
        doNothing().when(cacheService).evictPatientData(1L);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
        verify(cacheService, times(1)).evictPatientData(1L);
    }
}

