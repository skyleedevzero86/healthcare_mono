import { Platform } from 'react-native';
import { isIOS, isAndroid } from '../utils/platform';
import { healthService } from './healthService';
import { HealthData } from '../types/health';

interface HealthKitData {
  heartRate: number;
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  bodyTemperature: number;
  stress: number;
  steps: number;
  sleepHours: number;
  respiratoryRate: number;
  spo2: number;
}

class HealthKitService {
  private isAvailable(): boolean {
    return isIOS || isAndroid;
  }

  async requestAuthorization(): Promise<boolean> {
    if (!this.isAvailable()) {
      return false;
    }

    try {
      if (isIOS) {
        return await this.requestIOSAuthorization();
      } else if (isAndroid) {
        return await this.requestAndroidAuthorization();
      }
      return false;
    } catch (error) {
      console.error('HealthKit 권한 요청 실패:', error);
      return false;
    }
  }

  private async requestIOSAuthorization(): Promise<boolean> {
    try {
      return true;
    } catch (error) {
      console.error('iOS HealthKit 권한 요청 실패:', error);
      return false;
    }
  }

  private async requestAndroidAuthorization(): Promise<boolean> {
    try {
      return true;
    } catch (error) {
      console.error('Android Health Connect 권한 요청 실패:', error);
      return false;
    }
  }

  async getLatestHealthData(userId: string): Promise<HealthKitData | null> {
    if (!this.isAvailable()) {
      return null;
    }

    try {
      const realtimeData = await healthService.fetchRealtimeBiodata(userId);
      
      if (!realtimeData) {
        return null;
      }

      return {
        heartRate: realtimeData.heartrate || 0,
        bloodPressureSystolic: realtimeData.bloodpressMax || 0,
        bloodPressureDiastolic: realtimeData.bloodpressMin || 0,
        bodyTemperature: realtimeData.temperature || 0,
        stress: realtimeData.stress || 0,
        steps: realtimeData.step || 0,
        sleepHours: realtimeData.sleep || 0,
        respiratoryRate: realtimeData.repiratory || 0,
        spo2: realtimeData.spo2 || 0,
      };
    } catch (error) {
      console.error('생체 데이터 조회 실패:', error);
      return null;
    }
  }

  async getTodaySteps(userId: string): Promise<number> {
    try {
      const data = await this.getLatestHealthData(userId);
      return data?.steps || 0;
    } catch (error) {
      console.error('오늘 걸음수 조회 실패:', error);
      return 0;
    }
  }

  async getYesterdaySleep(userId: string): Promise<number> {
    try {
      const data = await this.getLatestHealthData(userId);
      return data?.sleepHours || 0;
    } catch (error) {
      console.error('어제 수면시간 조회 실패:', error);
      return 0;
    }
  }

  async getCurrentStress(userId: string): Promise<number> {
    try {
      const data = await this.getLatestHealthData(userId);
      return data?.stress || 0;
    } catch (error) {
      console.error('현재 스트레스 조회 실패:', error);
      return 0;
    }
  }

  getNormalHeartRateRange(): { min: number; max: number } {
    return { min: 60, max: 100 };
  }

  getNormalBloodPressureRange(): { min: number; max: number } {
    return { min: 90, max: 120 };
  }

  getNormalTemperatureRange(): { min: number; max: number } {
    return { min: 36.1, max: 37.2 };
  }

  getNormalStressRange(): { min: number; max: number } {
    return { min: 0, max: 50 };
  }

  formatHealthStatus(value: number, type: 'heartRate' | 'bloodPressure' | 'temperature' | 'stress'): string {
    switch (type) {
      case 'heartRate':
        const hrRange = this.getNormalHeartRateRange();
        if (value >= hrRange.min && value <= hrRange.max) {
          return `정상 심박수(${hrRange.min}~${hrRange.max})`;
        }
        return value < hrRange.min ? '낮은 심박수' : '높은 심박수';
      
      case 'bloodPressure':
        const bpRange = this.getNormalBloodPressureRange();
        if (value >= bpRange.min && value <= bpRange.max) {
          return `정상 혈압(${bpRange.min}~${bpRange.max})`;
        }
        return value < bpRange.min ? '낮은 혈압' : '높은 혈압';
      
      case 'temperature':
        const tempRange = this.getNormalTemperatureRange();
        if (value >= tempRange.min && value <= tempRange.max) {
          return `정상 체온(${tempRange.min}~${tempRange.max})`;
        }
        return value < tempRange.min ? '낮은 체온' : '높은 체온';
      
      case 'stress':
        const stressRange = this.getNormalStressRange();
        if (value >= stressRange.min && value <= stressRange.max) {
          return `정상 스트레스(${stressRange.min}~${stressRange.max})`;
        }
        return '높은 스트레스';
      
      default:
        return '정상';
    }
  }
}

export const healthKitService = new HealthKitService();

