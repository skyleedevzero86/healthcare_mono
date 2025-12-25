import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { HealthScore } from '../types/health';

interface HealthScoreCardProps {
  healthScore: HealthScore;
  onChartPress?: () => void;
}

export const HealthScoreCard: React.FC<HealthScoreCardProps> = ({
  healthScore,
  onChartPress,
}) => {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>건강 점수</Text>
        {onChartPress && (
          <TouchableOpacity style={styles.chartButton} onPress={onChartPress}>
            <Ionicons name="pie-chart" size={16} color="#2196F3" />
            <Text style={styles.chartButtonText}>차트</Text>
          </TouchableOpacity>
        )}
      </View>
      <View style={styles.scoreContainer}>
        <Text style={styles.mainScore}>{healthScore.healthScore}</Text>
        <Text style={styles.scoreLabel}>점</Text>
      </View>
      <View style={styles.scoreBreakdown}>
        <View style={styles.scoreItem}>
          <Text style={styles.scoreItemLabel}>수면</Text>
          <Text style={styles.scoreItemValue}>{healthScore.userSleepScore}</Text>
        </View>
        <View style={styles.scoreItem}>
          <Text style={styles.scoreItemLabel}>운동</Text>
          <Text style={styles.scoreItemValue}>{healthScore.userExerciseScore}</Text>
        </View>
        <View style={styles.scoreItem}>
          <Text style={styles.scoreItemLabel}>스트레스</Text>
          <Text style={styles.scoreItemValue}>{healthScore.userStressScore}</Text>
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
  scoreContainer: {
    alignItems: 'center',
    marginBottom: 20,
  },
  mainScore: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#2196F3',
  },
  scoreLabel: {
    fontSize: 18,
    color: '#666',
  },
  scoreBreakdown: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  scoreItem: {
    alignItems: 'center',
  },
  scoreItemLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  scoreItemValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
});

