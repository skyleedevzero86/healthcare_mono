import { HealthData } from '../types/health';

export interface HealthStatus {
  status: '정상' | '낮음' | '높음';
  color: string;
}

export interface HealthAnalysis {
  overallStatus: '양호' | '주의';
  analysis: string[];
  recommendations: string[];
  formattedAdvice: string;
}

export interface IHealthAnalysisService {
  analyzeHealthData(data: HealthData | null): HealthAnalysis;
  getHealthStatus(value: number, type: 'heartrate' | 'temperature' | 'spo2'): HealthStatus;
}

class HealthAnalysisService implements IHealthAnalysisService {
  getHealthStatus(value: number, type: 'heartrate' | 'temperature' | 'spo2'): HealthStatus {
    switch (type) {
      case 'heartrate':
        if (value < 60) return { status: '낮음', color: '#4CAF50' };
        if (value > 100) return { status: '높음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      case 'temperature':
        if (value < 36.1) return { status: '낮음', color: '#4CAF50' };
        if (value > 37.2) return { status: '높음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      case 'spo2':
        if (value < 95) return { status: '낮음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      default:
        return { status: '정상', color: '#2196F3' };
    }
  }

  analyzeHealthData(data: HealthData | null): HealthAnalysis {
    if (!data) {
      return {
        overallStatus: '양호',
        analysis: [],
        recommendations: ['건강 정보 메뉴에서 데이터를 입력해보세요.'],
        formattedAdvice: '건강 데이터가 없습니다. 데이터를 입력해주세요.\n\n추천사항: 건강 정보 메뉴에서 데이터를 입력해보세요.',
      };
    }

    const heartrateStatus = this.getHealthStatus(data.heartrate, 'heartrate');
    const temperatureStatus = this.getHealthStatus(data.temperature, 'temperature');
    const spo2Status = this.getHealthStatus(data.spo2, 'spo2');

    const analysis: string[] = [];
    const recommendations: string[] = [];
    let overallStatus: '양호' | '주의' = '양호';

    this.analyzeHeartRate(data.heartrate, heartrateStatus, analysis, recommendations);
    if (heartrateStatus.status !== '정상') overallStatus = '주의';

    this.analyzeTemperature(data.temperature, temperatureStatus, analysis, recommendations);
    if (temperatureStatus.status !== '정상') overallStatus = '주의';

    this.analyzeSpO2(data.spo2, spo2Status, analysis, recommendations);
    if (spo2Status.status !== '정상') overallStatus = '주의';

    this.analyzeSteps(data.step, analysis, recommendations);

    if (recommendations.length === 0) {
      recommendations.push('현재 상태를 유지하며 규칙적인 건강 관리를 계속하세요.');
    }

    const statusText = overallStatus === '양호' ? '전반적으로 양호한 상태' : '일부 주의가 필요한 상태';
    const formattedAdvice = `최근 건강 데이터를 분석한 결과, ${statusText}입니다.\n\n${analysis.join('\n')}\n\n추천사항: ${recommendations.join(' ')}`;

    return {
      overallStatus,
      analysis,
      recommendations,
      formattedAdvice,
    };
  }

  private analyzeHeartRate(
    value: number,
    status: HealthStatus,
    analysis: string[],
    recommendations: string[]
  ): void {
    if (status.status === '높음') {
      analysis.push('심박수가 높은 편입니다. 스트레스나 과도한 운동이 원인일 수 있습니다.');
      recommendations.push('충분한 휴식과 스트레스 관리가 필요합니다.');
    } else if (status.status === '낮음') {
      analysis.push('심박수가 낮은 편입니다. 운동 부족이나 저혈압을 의심해볼 수 있습니다.');
      recommendations.push('가벼운 운동을 시작해보세요.');
    } else {
      analysis.push('심박수가 정상 범위에 있어 심혈관 건강이 좋습니다.');
    }
  }

  private analyzeTemperature(
    value: number,
    status: HealthStatus,
    analysis: string[],
    recommendations: string[]
  ): void {
    if (status.status === '높음') {
      analysis.push('체온이 높습니다. 감염이나 염증이 있을 수 있습니다.');
      recommendations.push('충분한 수분 섭취와 휴식을 취하세요.');
    } else if (status.status === '낮음') {
      analysis.push('체온이 낮습니다. 면역력 저하나 대사 기능 저하를 의심해볼 수 있습니다.');
      recommendations.push('영양가 있는 음식을 섭취하고 충분한 수면을 취하세요.');
    } else {
      analysis.push('체온이 정상이므로 감염 증상은 없어 보입니다.');
    }
  }

  private analyzeSpO2(
    value: number,
    status: HealthStatus,
    analysis: string[],
    recommendations: string[]
  ): void {
    if (status.status === '낮음') {
      analysis.push('산소포화도가 낮습니다. 호흡기 문제나 폐 기능 저하를 의심해볼 수 있습니다.');
      recommendations.push('깊은 호흡 운동과 실내 환기를 자주 하세요.');
    } else {
      analysis.push('산소포화도가 양호하여 호흡 기능이 정상입니다.');
    }
  }

  private analyzeSteps(value: number, analysis: string[], recommendations: string[]): void {
    if (value < 5000) {
      analysis.push('걸음수가 부족합니다. 신체 활동이 부족한 상태입니다.');
      recommendations.push('하루 8,000보 이상 걷기를 목표로 하세요.');
    } else if (value > 12000) {
      analysis.push('걸음수가 충분합니다. 활발한 신체 활동을 하고 있습니다.');
    } else {
      analysis.push('걸음수가 적당합니다. 꾸준한 활동을 유지하세요.');
    }
  }
}

export const healthAnalysisService = new HealthAnalysisService();

