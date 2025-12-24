export interface Settings {
  notifications: {
    healthReminder: boolean;
    checkupNotification: boolean;
    exerciseReminder: boolean;
    mealReminder: boolean;
  };
  healthGoals: {
    dailySteps: number;
    weeklyExercise: number;
    dailyCalories: number;
  };
  app: {
    darkMode: boolean;
    autoLogin: boolean;
    biometric: boolean;
    language: string;
  };
}

