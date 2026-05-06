import { Notification } from '../types/notification';
import { getTimeAgo as formatTimeAgo } from '../utils/dateUtils';

export function getNotificationIcon(type: Notification['type']): string {
  switch (type) {
    case 'HEALTH_REMINDER':
      return 'heart';
    case 'CHECKUP':
      return 'medical';
    case 'CONSULTATION':
      return 'chatbubbles';
    case 'EXERCISE':
      return 'fitness';
    case 'MEAL':
      return 'restaurant';
    default:
      return 'notifications';
  }
}

export function getTimeAgo(dateString: string): string {
  return formatTimeAgo(dateString);
}

export function filterNotifications(
  notifications: Notification[],
  filter: 'all' | 'unread' | 'read',
  typeFilter: string
): Notification[] {
  return notifications.filter((notif) => {
    if (filter === 'unread' && notif.read) return false;
    if (filter === 'read' && !notif.read) return false;
    if (typeFilter !== 'all' && notif.type !== typeFilter) return false;
    return true;
  });
}
