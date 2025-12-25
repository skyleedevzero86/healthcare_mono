import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { HealthData } from '../types/health';
import { healthAnalysisService } from '../services/healthAnalysisService';
import { HealthStatusBadge } from './HealthStatusBadge';

interface HealthCardProps {
  data: HealthData;
  title?: string;
}

const HealthCard: React.FC<HealthCardProps> = ({ data, title = '건강 데이터' }) => {
  const heartrateStatus = healthAnalysisService.getHealthStatus(data.heartrate, 'heartrate');
  const temperatureStatus = healthAnalysisService.getHealthStatus(data.temperature, 'temperature');
  const spo2Status = healthAnalysisService.getHealthStatus(data.spo2, 'spo2');

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{title}</Text>
      <View style={styles.dataGrid}>
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
        <HealthStatusBadge
          status={spo2Status}
          value={`${data.spo2}%`}
          label="산소포화도"
        />
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
});

export default HealthCard;
