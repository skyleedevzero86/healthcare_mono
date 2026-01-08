import { getHealthStatus, analyzeHealthData } from '../healthAnalysisService';
import { HealthData } from '../../types/health';

describe('HealthAnalysisService', () => {
  describe('getHealthStatus', () => {
    it('should return normal status for heartrate in range', () => {
      const result = getHealthStatus(75, 'heartrate');
      expect(result.status).toBe('정상');
      expect(result.color).toBe('#2196F3');
    });

    it('should return high status for high heartrate', () => {
      const result = getHealthStatus(110, 'heartrate');
      expect(result.status).toBe('높음');
      expect(result.color).toBe('#F44336');
    });

    it('should return low status for low heartrate', () => {
      const result = getHealthStatus(50, 'heartrate');
      expect(result.status).toBe('낮음');
      expect(result.color).toBe('#4CAF50');
    });

    it('should return normal status for temperature in range', () => {
      const result = getHealthStatus(36.5, 'temperature');
      expect(result.status).toBe('정상');
    });

    it('should return high status for high temperature', () => {
      const result = getHealthStatus(38.0, 'temperature');
      expect(result.status).toBe('높음');
    });

    it('should return normal status for spo2 in range', () => {
      const result = getHealthStatus(98, 'spo2');
      expect(result.status).toBe('정상');
    });

    it('should return low status for low spo2', () => {
      const result = getHealthStatus(90, 'spo2');
      expect(result.status).toBe('낮음');
    });
  });

  describe('analyzeHealthData', () => {
    it('should return default analysis when data is null', () => {
      const result = analyzeHealthData(null);
      expect(result.overallStatus).toBe('양호');
      expect(result.recommendations.length).toBeGreaterThan(0);
    });

    it('should analyze normal health data', () => {
      const data: HealthData = {
        userId: 'test',
        time: '2024-01-01T00:00:00Z',
        heartrate: 75,
        temperature: 36.5,
        spo2: 98,
        step: 8000,
        stress: 50,
        bloodpressMin: 80,
        bloodpressMax: 120,
        repiratory: 16,
        sleep: 7,
      };

      const result = analyzeHealthData(data);
      expect(result.overallStatus).toBe('양호');
      expect(result.analysis.length).toBeGreaterThan(0);
    });

    it('should detect warning status for abnormal data', () => {
      const data: HealthData = {
        userId: 'test',
        time: '2024-01-01T00:00:00Z',
        heartrate: 110,
        temperature: 38.0,
        spo2: 90,
        step: 3000,
        stress: 80,
        bloodpressMin: 80,
        bloodpressMax: 120,
        repiratory: 16,
        sleep: 5,
      };

      const result = analyzeHealthData(data);
      expect(result.overallStatus).toBe('주의');
      expect(result.recommendations.length).toBeGreaterThan(0);
    });
  });
});

