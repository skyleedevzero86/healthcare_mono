import { healthService } from '../healthService';
import { apiService } from '../api';
import { API_ENDPOINTS } from '../../constants/api';

jest.mock('../api');

describe('HealthService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('fetchHealthData', () => {
    it('should fetch health data successfully', async () => {
      const mockData = [
        {
          userId: 'test',
          time: '2024-01-01T00:00:00Z',
          heartrate: 70,
          temperature: 36.5,
          spo2: 98,
          step: 5000,
          stress: 50,
          bloodpressMin: 80,
          bloodpressMax: 120,
          repiratory: 16,
          sleep: 7,
        },
      ];

      (apiService.post as jest.Mock).mockResolvedValue({
        resultCode: '0000',
        resultData: mockData,
      });

      const result = await healthService.fetchHealthData('test');

      expect(apiService.post).toHaveBeenCalledWith(API_ENDPOINTS.HEALTHCARE.HEALTH_INFO, {
        userId: 'test',
      });
      expect(result).toEqual(mockData);
    });

    it('should throw error when API call fails', async () => {
      (apiService.post as jest.Mock).mockResolvedValue({
        resultCode: '5001',
        resultMessage: '서버 오류',
      });

      await expect(healthService.fetchHealthData('test')).rejects.toThrow('서버 오류');
    });
  });

  describe('insertHealthData', () => {
    it('should insert health data successfully', async () => {
      const mockData = {
        userId: 'test',
        time: '2024-01-01T00:00:00Z',
        heartrate: 70,
        temperature: 36.5,
        spo2: 98,
        step: 5000,
        stress: 50,
        bloodpressMin: 80,
        bloodpressMax: 120,
        repiratory: 16,
        sleep: 7,
      };

      (apiService.post as jest.Mock).mockResolvedValue({
        resultCode: '0000',
        resultData: mockData,
      });

      const result = await healthService.insertHealthData(mockData);

      expect(apiService.post).toHaveBeenCalledWith(
        API_ENDPOINTS.HEALTHCARE.INSERT_HEALTH_INFO,
        mockData
      );
      expect(result).toEqual(mockData);
    });
  });

  describe('fetchHealthScoreList', () => {
    it('should fetch health score successfully', async () => {
      const mockScore = {
        userId: 'test',
        dailyDate: '2024-01-01',
        userSleepScore: 80,
        userExerciseScore: 75,
        userStressScore: 70,
        healthScore: 75,
      };

      (apiService.post as jest.Mock).mockResolvedValue({
        resultCode: '0000',
        resultData: mockScore,
      });

      const result = await healthService.fetchHealthScoreList('test');

      expect(result).toEqual(mockScore);
    });

    it('should return null when no data found', async () => {
      (apiService.post as jest.Mock).mockResolvedValue({
        resultCode: '3001',
        resultMessage: '조회된 결과가 없습니다.',
      });

      const result = await healthService.fetchHealthScoreList('test');

      expect(result).toBeNull();
    });
  });
});

