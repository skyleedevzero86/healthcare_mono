export interface HealthData {
  userId: string;
  time: string;
  heartrate: number;
  temperature: number;
  spo2: number;
  step: number;
  stress: number;
  bloodpressMin: number;
  bloodpressMax: number;
  repiratory: number;
  sleep: number;
}

export interface HealthScore {
  userId: string;
  dailyDate: string;
  userSleepScore: number;
  userExerciseScore: number;
  userStressScore: number;
  healthScore: number;
}

export interface CheckupItem {
  itemCode: string;
  itemName: string;
  itemValue: number;
  referenceRange: string;
  status: 'normal' | 'abnormal';
}

export interface Checkup {
  checkupSeq: number;
  userId: string;
  checkupDate: string;
  checkupType: 'GENERAL' | 'CANCER' | 'CARDIOVASCULAR' | 'METABOLIC' | 'OTHER';
  hospitalName: string;
  items: CheckupItem[];
  riskLevel: 'normal' | 'low' | 'medium' | 'high';
}

