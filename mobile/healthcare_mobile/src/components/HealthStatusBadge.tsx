import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { HealthStatus } from '../services/healthAnalysisService';

interface HealthStatusBadgeProps {
  status: HealthStatus;
  value: string | number;
  label: string;
}

export const HealthStatusBadge: React.FC<HealthStatusBadgeProps> = ({
  status,
  value,
  label,
}) => {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <Text style={[styles.value, { color: status.color }]}>{value}</Text>
      <Text style={[styles.status, { color: status.color }]}>{status.status}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
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
    marginBottom: 2,
  },
  status: {
    fontSize: 12,
    fontWeight: '600',
  },
});

