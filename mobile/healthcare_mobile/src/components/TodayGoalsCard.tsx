import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface TodayGoalsCardProps {
  currentSteps: number;
  targetSteps: number;
  yesterdaySleep: number;
  currentStress: number;
}

export const TodayGoalsCard: React.FC<TodayGoalsCardProps> = ({
  currentSteps,
  targetSteps,
  yesterdaySleep,
  currentStress,
}) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>오늘의 목표</Text>
      
      <View style={styles.goalItem}>
        <View style={styles.iconContainer}>
          <Ionicons name="walk" size={24} color="#FF9500" />
        </View>
        <View style={styles.content}>
          <Text style={styles.goalText}>
            오늘 목표 걸음수는 <Text style={styles.highlight}>{targetSteps.toLocaleString()}</Text>보
          </Text>
          <Text style={styles.currentText}>현재 {currentSteps.toLocaleString()}보</Text>
        </View>
      </View>

      <View style={styles.goalItem}>
        <View style={styles.iconContainer}>
          <Ionicons name="bed" size={24} color="#007AFF" />
        </View>
        <View style={styles.content}>
          <Text style={styles.goalText}>
            어제 취침시간은 <Text style={styles.highlight}>{yesterdaySleep}</Text>시간이에요.
          </Text>
          <Text style={styles.message}>개운한 하루 보내세요</Text>
        </View>
      </View>

      <View style={styles.goalItem}>
        <View style={styles.iconContainer}>
          <Ionicons name="happy-outline" size={24} color="#34C759" />
        </View>
        <View style={styles.content}>
          <Text style={styles.goalText}>
            현재 스트레스는 <Text style={styles.highlight}>{currentStress}</Text>이에요.
          </Text>
          <Text style={styles.message}>좋은하루 되세요.</Text>
        </View>
      </View>
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
  goalItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 20,
  },
  iconContainer: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#F5F5F5',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  content: {
    flex: 1,
  },
  goalText: {
    fontSize: 14,
    color: '#333333',
    marginBottom: 4,
    lineHeight: 20,
  },
  highlight: {
    color: '#FF3B30',
    fontWeight: 'bold',
  },
  currentText: {
    fontSize: 14,
    color: '#666666',
  },
  message: {
    fontSize: 14,
    color: '#666666',
    marginTop: 4,
  },
});

