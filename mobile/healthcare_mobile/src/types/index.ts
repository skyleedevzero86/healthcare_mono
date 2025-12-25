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

export { HealthData, HealthScore, Checkup, CheckupItem } from './health';

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
  Diet: undefined;
  Exercise: undefined;
  Recommend: undefined;
  Consultation: undefined;
  Notification: undefined;
  Settings: undefined;
};

export type MainTabParamList = {
  Dashboard: undefined;
  HealthInfo: undefined;
  Community: undefined;
  Profile: undefined;
  Permission: undefined;
  Diet: undefined;
  Exercise: undefined;
  Recommend: undefined;
  Consultation: undefined;
  Notification: undefined;
  Settings: undefined;
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


export interface MealItem {
  foodCode: string;
  foodName: string;
  quantity: number;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
}

export interface MealRecord {
  mealSeq: number;
  userId: string;
  mealDate: string;
  mealType: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  mealTime: string;
  items: MealItem[];
  totalCalories: number;
  totalProtein: number;
  totalCarbs: number;
  totalFat: number;
}

export interface DietPlan {
  planSeq: number;
  userId: string;
  goal: 'WEIGHT_LOSS' | 'WEIGHT_MAINTENANCE' | 'WEIGHT_GAIN' | 'MUSCLE_GAIN';
  activityLevel: 'SEDENTARY' | 'LIGHT' | 'MODERATE' | 'ACTIVE' | 'VERY_ACTIVE';
  duration: number;
  dailyCalories: number;
  days: DietDay[];
}

export interface DietDay {
  day: number;
  totalCalories: number;
  meals: MealPlan[];
}

export interface MealPlan {
  mealType: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  calories: number;
  items: MealItem[];
  nutrition: {
    protein: number;
    carbs: number;
    fat: number;
  };
}

export interface ExerciseRecord {
  exerciseSeq: number;
  userId: string;
  exerciseDate: string;
  exerciseCode: string;
  exerciseName: string;
  duration: number;
  heartRateAvg?: number;
  heartRateMax?: number;
  distance?: number;
  intensity: 'LOW' | 'MODERATE' | 'HIGH' | 'VERY_HIGH';
  calories: number;
  notes?: string;
}

export interface ExerciseProgram {
  programSeq: number;
  userId: string;
  goal: 'WEIGHT_LOSS' | 'MUSCLE_GAIN' | 'STRENGTH' | 'FLEXIBILITY' | 'GENERAL_FITNESS';
  exerciseType: 'CARDIO' | 'STRENGTH' | 'FLEXIBILITY' | 'MIXED';
  weeklyFrequency: number;
  duration: number;
  fitnessLevel: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  weeks: ExerciseWeek[];
}

export interface ExerciseWeek {
  week: number;
  exercises: ExerciseSchedule[];
}

export interface ExerciseSchedule {
  day: number;
  exerciseCode: string;
  exerciseName: string;
  duration: number;
  sets?: number;
  reps?: number;
  intensity: 'LOW' | 'MODERATE' | 'HIGH' | 'VERY_HIGH';
  notes?: string;
}

export interface HealthContent {
  contentId: number;
  title: string;
  description: string;
  category: string;
  tags: string[];
  views: number;
  likes: number;
  content: string;
}

export interface HealthService {
  serviceId: number;
  name: string;
  type: 'HOSPITAL' | 'CLINIC' | 'PHARMACY' | 'FITNESS' | 'OTHER';
  description: string;
  address: string;
  phone: string;
  rating: number;
  reviewCount: number;
  hours: string;
}

export interface Doctor {
  doctorSeq: number;
  doctorNm: string;
  specialty: string;
  hospital: string;
  experience: number;
  rating: number;
  reviewCount: number;
  available: boolean;
  specialties: string[];
}

export interface Consultation {
  consultationSeq: number;
  userId: string;
  doctorSeq: number;
  doctorNm: string;
  consultationType: 'GENERAL' | 'CHECKUP_REVIEW' | 'MEDICATION' | 'LIFESTYLE' | 'EMERGENCY';
  consultationDate: string;
  subject: string;
  content: string;
  status: 'PENDING' | 'APPROVED' | 'COMPLETED' | 'CANCELLED';
  doctorResponse?: string;
  responseDate?: string;
  shareConsent: boolean;
}

export interface ServiceReservation {
  reservationSeq: number;
  userId: string;
  serviceId: number;
  hospitalName: string;
  reservationType: 'GENERAL' | 'CHECKUP' | 'FOLLOW_UP' | 'CONSULTATION' | 'VACCINATION';
  department: string;
  reservationDateTime: string;
  symptoms?: string;
  phone: string;
  status: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';
}

export { Notification, NotificationFilter } from './notification';

export { Settings } from './settings';

export interface NutritionGoals {
  dailyCalories: number;
  dailyProtein: number;
  dailyCarbs: number;
  dailyFat: number;
}

export interface NutritionProgress {
  today: {
    calories: number;
    protein: number;
    carbs: number;
    fat: number;
  };
  goals: NutritionGoals;
}