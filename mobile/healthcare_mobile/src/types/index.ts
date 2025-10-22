export interface User {
  userSeq: number;
  userId: string;
  userNm: string;
  userRoleFk: string;
  email: string;
  telNumEnc: string;
  birthEnc: string;
  gender: string;
  height: number;
  weight: number;
  bloodType: string;
  deptNm?: string;
  doctorSeq?: number;
  guardianSeq?: number;
}

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

export interface CommunityPost {
  commuSeq: number;
  content: string;
  regDate: string;
  heartrate: number;
  temperature: number;
  bloodpress: number;
  smoking: number;
  drinking: number;
  exercise: number;
  age: number;
  userId: string;
  userNm: string;
  bodyAge: number;
}

export interface ApiResponse<T = any> {
  resultCode: string;
  resultMessage: string;
  resultData: T;
}

export interface SigninRequest {
  userId: string;
  userPwEnc: string;
  userRoleFk: string;
  source: string;
}

export interface SignupRequest {
  userId: string;
  userPwEnc: string;
  userNm: string;
  userRoleFk: string;
  birthEnc: string;
  telNumEnc: string;
  email: string;
  deptNm?: string;
  doctorSeq?: number;
  guardian?: string;
  guardianSeq?: number;
  height?: number;
  weight?: number;
  gender: string;
  bloodType: string;
  agreementYn: string;
}

export interface JwtToken {
  grantType: string;
  accessToken: string;
  refreshToken: string;
}

export type RootStackParamList = {
  Auth: undefined;
  Main: undefined;
  Login: undefined;
  Signup: undefined;
  Dashboard: undefined;
  HealthInfo: undefined;
  Community: undefined;
  Profile: undefined;
};

export type MainTabParamList = {
  Dashboard: undefined;
  HealthInfo: undefined;
  Community: undefined;
  Profile: undefined;
  Permission: undefined;
};

export interface PermissionStatus {
  location: 'granted' | 'denied' | 'limited' | 'undetermined';
  camera: 'granted' | 'denied' | 'limited' | 'undetermined';
  microphone: 'granted' | 'denied' | 'limited' | 'undetermined';
  healthData: 'granted' | 'denied' | 'limited' | 'undetermined';
  notifications: 'granted' | 'denied' | 'limited' | 'undetermined';
}

export interface PermissionRequest {
  type: 'location' | 'camera' | 'microphone' | 'healthData' | 'notifications';
  title: string;
  message: string;
  required: boolean;
}

export interface LocationData {
  latitude: number;
  longitude: number;
  accuracy: number;
  timestamp: number;
}

export interface SensorData {
  accelerometer: {
    x: number;
    y: number;
    z: number;
  };
  gyroscope: {
    x: number;
    y: number;
    z: number;
  };
  magnetometer: {
    x: number;
    y: number;
    z: number;
  };
  timestamp: number;
}