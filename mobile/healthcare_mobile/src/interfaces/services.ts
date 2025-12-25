export interface IService {
  readonly name: string;
}

export interface IHealthAnalysisService {
  analyzeHealthData(data: any): any;
  getHealthStatus(value: number, type: string): any;
}

export interface INotificationService {
  getNotificationIcon(type: string): string;
  getTimeAgo(dateString: string): string;
  filterNotifications(notifications: any[], filter: string, typeFilter: string): any[];
}

export interface IHealthDataService {
  getLatestHealthData(healthData: any[]): any | null;
  formatHealthValue(value: number, type: string): string;
}

