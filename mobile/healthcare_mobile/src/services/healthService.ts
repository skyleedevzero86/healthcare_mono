import { apiService } from './api';
import { API_ENDPOINTS } from '../constants/api';
import { HealthData, HealthScore, ApiResponse } from '../types';

class HealthService {
  async fetchHealthData(userId: string): Promise<HealthData[]> {
    const response: ApiResponse<HealthData[]> = await apiService.post(
      API_ENDPOINTS.HEALTHCARE.HEALTH_INFO,
      { userId }
    );

    if (response.resultCode === '0000') {
      return response.resultData || [];
    }

    throw new Error(response.resultMessage || '건강 데이터 조회에 실패했습니다.');
  }

  async insertHealthData(data: HealthData): Promise<HealthData> {
    const response: ApiResponse<HealthData> = await apiService.post(
      API_ENDPOINTS.HEALTHCARE.INSERT_HEALTH_INFO,
      data
    );

    if (response.resultCode === '0000') {
      return response.resultData;
    }

    throw new Error(response.resultMessage || '건강 데이터 저장에 실패했습니다.');
  }

  async fetchHealthScoreList(userId: string): Promise<HealthScore | null> {
    const response: ApiResponse<HealthScore> = await apiService.post(
      API_ENDPOINTS.HEALTHCARE.HEALTH_SCORE_LIST,
      { userId }
    );

    if (response.resultCode === '0000') {
      return response.resultData || null;
    }

    if (response.resultCode === '3001') {
      return null;
    }

    throw new Error(response.resultMessage || '건강 점수 조회에 실패했습니다.');
  }

  async fetchHealthChart(userId: string, startDate: string, endDate: string): Promise<HealthData[]> {
    const response: ApiResponse<HealthData[]> = await apiService.post(
      API_ENDPOINTS.HEALTHCARE.HEALTH_INFO_CHART,
      { userId, startDate, endDate }
    );

    if (response.resultCode === '0000') {
      return response.resultData || [];
    }

    throw new Error(response.resultMessage || '건강 차트 데이터 조회에 실패했습니다.');
  }

  async fetchRealtimeBiodata(userId: string): Promise<HealthData | null> {
    const response: ApiResponse<HealthData> = await apiService.post(
      API_ENDPOINTS.HEALTHCARE.REALTIME_BIODATA,
      { userId }
    );

    if (response.resultCode === '0000') {
      return response.resultData || null;
    }

    if (response.resultCode === '3001') {
      return null;
    }

    throw new Error(response.resultMessage || '실시간 생체 데이터 조회에 실패했습니다.');
  }
}

export const healthService = new HealthService();

