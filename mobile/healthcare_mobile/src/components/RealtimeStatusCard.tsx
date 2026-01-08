import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface RealtimeStatusItem {
  icon: keyof typeof Ionicons.glyphMap;
  label: string;
  value: number;
  status: string;
  iconColor: string;
}

interface RealtimeStatusCardProps {
  heartRate: number;
  bloodPressure: { systolic: number; diastolic: number };
  temperature: number;
  stress: number;
}

export const RealtimeStatusCard: React.FC<RealtimeStatusCardProps> = ({
  heartRate,
  bloodPressure,
  temperature,
  stress,
}) => {
  const items: RealtimeStatusItem[] = [
    {
      icon: 'heart',
      label: heartRate > 0 ? `정상 심박수(60~100)` : '심박수 정상 심박수(~)',
      value: heartRate,
      status: 'normal',
      iconColor: '#FF3B30',
    },
    {
      icon: 'water',
      label: bloodPressure.systolic > 0 ? `정상 혈압(90~120)` : '혈압 정상 혈압(~)',
      value: bloodPressure.systolic,
      status: 'normal',
      iconColor: '#FF3B30',
    },
    {
      icon: 'thermometer',
      label: temperature > 0 ? `정상 체온(36.1~37.2)` : '체온 정상 체온(~)',
      value: temperature,
      status: 'normal',
      iconColor: '#FF3B30',
    },
    {
      icon: 'flash',
      label: stress > 0 ? `정상 스트레스(0~50)` : '스트레스 정상 스트레스(~)',
      value: stress,
      status: 'normal',
      iconColor: '#AF52DE',
    },
  ];

  return (
    <View style={styles.container}>
      <Text style={styles.title}>실시간 상태</Text>
      {items.map((item, index) => (
        <View key={index} style={styles.item}>
          <View style={[styles.iconContainer, { backgroundColor: item.iconColor }]}>
            <Ionicons name={item.icon} size={24} color="#FFFFFF" />
          </View>
          <View style={styles.content}>
            <Text style={styles.label}>{item.label}</Text>
            <Text style={styles.value}>{item.value}</Text>
          </View>
        </View>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333333',
    marginBottom: 16,
  },
  item: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  iconContainer: {
    width: 48,
    height: 48,
    borderRadius: 24,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  content: {
    flex: 1,
  },
  label: {
    fontSize: 14,
    color: '#666666',
    marginBottom: 4,
  },
  value: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333333',
  },
});

