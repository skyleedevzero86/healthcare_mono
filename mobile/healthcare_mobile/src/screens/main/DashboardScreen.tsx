import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { getCurrentLocation, sendNotification } from '../../store/slices/permissionSlice';
import PermissionRequest from '../../components/PermissionRequest';
import ExpoGoLimitationBanner from '../../components/ExpoGoLimitationBanner';
import { useDashboard } from '../../hooks/useDashboard';
import { useHealthAnalysis } from '../../hooks/useHealthAnalysis';
import { HealthDataCard } from '../../components/HealthDataCard';
import { AIAdviceCard } from '../../components/AIAdviceCard';
import { HealthScoreCard } from '../../components/HealthScoreCard';

const DashboardScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { permissions } = useSelector((state: RootState) => state.permission);
  const [showPermissionRequest, setShowPermissionRequest] = useState(false);
  const [showLimitationBanner, setShowLimitationBanner] = useState(true);

  const { user, healthScore, loading, latestHealthData, refresh } = useDashboard();
  const { analysis, loading: aiLoading } = useHealthAnalysis(latestHealthData);

  const handleLocationRequest = async () => {
    try {
      await dispatch(getCurrentLocation()).unwrap();
    } catch (error) {
      console.error('위치 요청 실패:', error);
    }
  };

  const handleNotificationRequest = async () => {
    try {
      await dispatch(
        sendNotification({
          title: '헬스케어 알림',
          body: '건강 상태를 확인해보세요!',
        })
      ).unwrap();
    } catch (error) {
      console.error('알림 요청 실패:', error);
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView
        style={styles.container}
        refreshControl={<RefreshControl refreshing={loading} onRefresh={refresh} />}
      >
        <View style={styles.header}>
          <Text style={styles.greeting}>안녕하세요, {user?.userNm || '사용자'}님!</Text>
          <Text style={styles.subtitle}>오늘의 건강 상태를 확인해보세요</Text>
        </View>

        {showLimitationBanner && (
          <ExpoGoLimitationBanner onDismiss={() => setShowLimitationBanner(false)} />
        )}

        {latestHealthData && <HealthDataCard data={latestHealthData} />}

        {healthScore && <HealthScoreCard healthScore={healthScore} />}

        <AIAdviceCard analysis={analysis} loading={aiLoading} />

        <View style={styles.quickActions}>
          <Text style={styles.cardTitle}>빠른 액션</Text>
          <View style={styles.actionGrid}>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>건강 데이터 입력</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>차트 보기</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>커뮤니티</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>설정</Text>
            </TouchableOpacity>
          </View>
        </View>

        <View style={styles.permissionSection}>
          <Text style={styles.cardTitle}>권한 관리</Text>
          <View style={styles.permissionGrid}>
            <TouchableOpacity
              style={[
                styles.permissionButton,
                permissions.location === 'granted' && styles.grantedButton,
              ]}
              onPress={handleLocationRequest}
            >
              <Text
                style={[
                  styles.permissionButtonText,
                  permissions.location === 'granted' && styles.grantedText,
                ]}
              >
                위치 정보
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[
                styles.permissionButton,
                permissions.notifications === 'granted' && styles.grantedButton,
              ]}
              onPress={handleNotificationRequest}
            >
              <Text
                style={[
                  styles.permissionButtonText,
                  permissions.notifications === 'granted' && styles.grantedText,
                ]}
              >
                알림
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.permissionButton}
              onPress={() => setShowPermissionRequest(true)}
            >
              <Text style={styles.permissionButtonText}>권한 설정</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>

      <PermissionRequest
        visible={showPermissionRequest}
        onClose={() => setShowPermissionRequest(false)}
        onComplete={() => setShowPermissionRequest(false)}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  container: {
    flex: 1,
    padding: 20,
  },
  header: {
    marginBottom: 20,
  },
  greeting: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  quickActions: {
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
  actionGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  actionButton: {
    width: '48%',
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    marginBottom: 10,
    alignItems: 'center',
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  permissionSection: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  permissionGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  permissionButton: {
    width: '30%',
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    padding: 12,
    marginBottom: 10,
    alignItems: 'center',
  },
  grantedButton: {
    backgroundColor: '#4CAF50',
  },
  permissionButtonText: {
    color: '#666',
    fontSize: 12,
    fontWeight: '600',
  },
  grantedText: {
    color: '#fff',
  },
});

export default DashboardScreen;
