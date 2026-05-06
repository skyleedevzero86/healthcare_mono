import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { Notification } from '../types/notification';
import { getNotificationIcon, getTimeAgo } from '../services/notificationService';

interface NotificationItemProps {
  notification: Notification;
  onPress?: () => void;
}

export const NotificationItem: React.FC<NotificationItemProps> = ({
  notification,
  onPress,
}) => {
  const iconName = getNotificationIcon(notification.type);
  const timeAgo = getTimeAgo(notification.createdAt);

  return (
    <TouchableOpacity
      style={[styles.container, !notification.read && styles.unreadContainer]}
      onPress={onPress}
    >
      <View style={styles.icon}>
        <Ionicons name={iconName as any} size={24} color="#2196F3" />
      </View>
      <View style={styles.content}>
        <View style={styles.header}>
          <Text style={styles.title}>{notification.title}</Text>
          {!notification.read && <View style={styles.unreadDot} />}
        </View>
        <Text style={styles.message}>{notification.message}</Text>
        <Text style={styles.time}>{timeAgo}</Text>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  unreadContainer: {
    backgroundColor: '#e3f2fd',
  },
  icon: {
    marginRight: 15,
  },
  content: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
  },
  title: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    flex: 1,
  },
  unreadDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#2196F3',
    marginLeft: 8,
  },
  message: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  time: {
    fontSize: 12,
    color: '#999',
  },
});

