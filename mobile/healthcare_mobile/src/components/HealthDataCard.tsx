import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { HealthData } from '../types/health';
import { getHealthStatus } from '../services/healthAnalysisService';
import { HealthStatusBadge } from './HealthStatusBadge';

interface HealthDataCardProps {
  data: HealthData;
  onChartPress?: () => void;
}

export const HealthDataCard: React.FC<HealthDataCardProps> = ({ data, onChartPress }) => {
  const heartrateStatus = getHealthStatus(data.heartrate, 'heartrate');
  const temperatureStatus = getHealthStatus(data.temperature, 'temperature');
  const spo2Status = getHealthStatus(data.spo2, 'spo2');

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>실시간 건강 데이터</Text>
        {onChartPress && (
          <TouchableOpacity style={styles.chartButton} onPress={onChartPress}>
            <Ionicons name="bar-chart" size={16} color="#2196F3" />
            <Text style={styles.chartButtonText}>차트</Text>
          </TouchableOpacity>
        )}
      </View>
      <View style={styles.grid}>
        <HealthStatusBadge
          status={heartrateStatus}
          value={`${data.heartrate} bpm`}
          label="심박수"
        />
        <HealthStatusBadge
          status={temperatureStatus}
          value={`${data.temperature}°C`}
          label="체온"
        />
        <View style={styles.item}>
          <Text style={styles.label}>혈압</Text>
          <Text style={styles.value}>
            {data.bloodpressMax}/{data.bloodpressMin}
          </Text>
        </View>
        <HealthStatusBadge
          status={spo2Status}
          value={`${data.spo2}%`}
          label="산소포화도"
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 15,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  chartButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#e3f2fd',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
    gap: 4,
  },
  chartButtonText: {
    color: '#2196F3',
    fontSize: 12,
    fontWeight: '600',
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  item: {
    width: '48%',
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 15,
    marginBottom: 10,
    alignItems: 'center',
  },
  label: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  value: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2196F3',
  },
});

