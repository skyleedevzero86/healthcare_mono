import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { Notification } from '../types/notification';
import { notificationService } from '../services/notificationService';

interface UseNotificationsResult {
  notifications: Notification[];
  filteredNotifications: Notification[];
  loading: boolean;
  filter: 'all' | 'unread' | 'read';
  typeFilter: string;
  setFilter: (filter: 'all' | 'unread' | 'read') => void;
  setTypeFilter: (type: string) => void;
  markAllAsRead: () => void;
  loadNotifications: () => void;
}

export const useNotifications = (): UseNotificationsResult => {
  const { user } = useSelector((state: RootState) => state.auth);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [filter, setFilter] = useState<'all' | 'unread' | 'read'>('all');
  const [typeFilter, setTypeFilter] = useState<string>('all');
  const [loading, setLoading] = useState(false);

  const loadNotifications = () => {
    setLoading(true);
    setTimeout(() => {
      setNotifications([
        {
          notificationSeq: 1,
          userId: user?.userId || '',
          type: 'HEALTH_REMINDER',
          title: '건강 데이터 입력 알림',
          message: '오늘의 건강 데이터를 입력해주세요.',
          createdAt: new Date().toISOString(),
          read: false,
        },
        {
          notificationSeq: 2,
          userId: user?.userId || '',
          type: 'CHECKUP',
          title: '건강검진 결과',
          message: '건강검진 결과가 업로드되었습니다.',
          createdAt: new Date(Date.now() - 86400000).toISOString(),
          read: true,
        },
      ]);
      setLoading(false);
    }, 500);
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const filteredNotifications = notificationService.filterNotifications(
    notifications,
    filter,
    typeFilter
  );

  const markAllAsRead = () => {
    setNotifications(notifications.map((n) => ({ ...n, read: true })));
  };

  return {
    notifications,
    filteredNotifications,
    loading,
    filter,
    typeFilter,
    setFilter,
    setTypeFilter,
    markAllAsRead,
    loadNotifications,
  };
};

