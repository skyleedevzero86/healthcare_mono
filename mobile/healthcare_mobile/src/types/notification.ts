export interface Notification {
  notificationSeq: number;
  userId: string;
  type: 'HEALTH_REMINDER' | 'CHECKUP' | 'CONSULTATION' | 'EXERCISE' | 'MEAL' | 'GENERAL';
  title: string;
  message: string;
  createdAt: string;
  read: boolean;
  action?: {
    type: string;
    data: any;
  };
}

export interface NotificationFilter {
  readStatus: 'all' | 'unread' | 'read';
  type: string;
}

