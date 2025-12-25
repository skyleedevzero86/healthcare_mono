import React from 'react';
import { View, Text, StyleSheet, ActivityIndicator } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { HealthAnalysis } from '../services/healthAnalysisService';

interface AIAdviceCardProps {
  analysis: HealthAnalysis | null;
  loading: boolean;
}

export const AIAdviceCard: React.FC<AIAdviceCardProps> = ({ analysis, loading }) => {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Ionicons name="chatbubbles" size={20} color="#2196F3" />
        <Text style={styles.title}>건강 조언</Text>
      </View>
      {loading ? (
        <View style={styles.loading}>
          <ActivityIndicator size="small" color="#2196F3" />
          <Text style={styles.loadingText}>AI가 건강 데이터를 분석 중...</Text>
        </View>
      ) : (
        <View style={styles.content}>
          <Text style={styles.adviceText}>
            {analysis?.formattedAdvice || '건강 데이터를 입력하면 AI 조언을 받을 수 있습니다.'}
          </Text>
        </View>
      )}
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
    alignItems: 'center',
    marginBottom: 15,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginLeft: 10,
  },
  loading: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingVertical: 20,
  },
  loadingText: {
    fontSize: 14,
    color: '#666',
  },
  content: {
    paddingVertical: 10,
  },
  adviceText: {
    fontSize: 14,
    color: '#333',
    lineHeight: 22,
  },
});

