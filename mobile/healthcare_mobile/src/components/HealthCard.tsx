import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { HealthData } from '../types';

interface HealthCardProps {
  data: HealthData;
  title?: string;
}

const HealthCard: React.FC<HealthCardProps> = ({ data, title = '건강 데이터' }) => {
  const getHealthStatus = (value: number, type: string) => {
    switch (type) {
      case 'heartrate':
        if (value < 60) return { status: '낮음', color: '#4CAF50' };
        if (value > 100) return { status: '높음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      case 'temperature':
        if (value < 36.1) return { status: '낮음', color: '#4CAF50' };
        if (value > 37.2) return { status: '높음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      case 'spo2':
        if (value < 95) return { status: '낮음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      default:
        return { status: '정상', color: '#2196F3' };
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{title}</Text>
      <View style={styles.dataGrid}>
        <View style={styles.dataItem}>
          <Text style={styles.dataLabel}>심박수</Text>
          <Text style={[styles.dataValue, { color: getHealthStatus(data.heartrate, 'heartrate').color }]}>
            {data.heartrate} bpm
          </Text>
          <Text style={[styles.dataStatus, { color: getHealthStatus(data.heartrate, 'heartrate').color }]}>
            {getHealthStatus(data.heartrate, 'heartrate').status}
          </Text>
        </View>
        <View style={styles.dataItem}>
          <Text style={styles.dataLabel}>체온</Text>
          <Text style={[styles.dataValue, { color: getHealthStatus(data.temperature, 'temperature').color }]}>
            {data.temperature}°C
          </Text>
          <Text style={[styles.dataStatus, { color: getHealthStatus(data.temperature, 'temperature').color }]}>
            {getHealthStatus(data.temperature, 'temperature').status}
          </Text>
        </View>
        <View style={styles.dataItem}>
          <Text style={styles.dataLabel}>산소포화도</Text>
          <Text style={[styles.dataValue, { color: getHealthStatus(data.spo2, 'spo2').color }]}>
            {data.spo2}%
          </Text>
          <Text style={[styles.dataStatus, { color: getHealthStatus(data.spo2, 'spo2').color }]}>
            {getHealthStatus(data.spo2, 'spo2').status}
          </Text>
        </View>
        <View style={styles.dataItem}>
          <Text style={styles.dataLabel}>걸음수</Text>
          <Text style={styles.dataValue}>{data.step.toLocaleString()}</Text>
        </View>
        <View style={styles.dataItem}>
          <Text style={styles.dataLabel}>혈압</Text>
          <Text style={styles.dataValue}>
            {data.bloodpressMax}/{data.bloodpressMin}
          </Text>
        </View>
        <View style={styles.dataItem}>
          <Text style={styles.dataLabel}>수면</Text>
          <Text style={styles.dataValue}>{data.sleep}시간</Text>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  dataGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  dataItem: {
    width: '48%',
    marginBottom: 15,
  },
  dataLabel: {
    fontSize: 12,
    color: '#666',
    marginBottom: 4,
  },
  dataValue: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 2,
  },
  dataStatus: {
    fontSize: 12,
    fontWeight: '600',
  },
});

export default HealthCard;
